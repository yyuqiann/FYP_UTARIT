package my.edu.utar.utarit.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.utar.utarit.model.Comment;
import my.edu.utar.utarit.model.Post;
import my.edu.utar.utarit.model.PostCreateRequest;
import my.edu.utar.utarit.model.Profile;
import my.edu.utar.utarit.network.model.SignInRequest;
import my.edu.utar.utarit.network.model.SignInResponse;
import my.edu.utar.utarit.utils.SessionManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import my.edu.utar.utarit.api.SupabaseApi;

public class SupabaseClient {
    private static final String BASE_URL = "https://zfhjixzpcpfjdbgsdcxn.supabase.co/";
    public static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpmaGppeHpwY3BmamRiZ3NkY3huIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTg3NzQxMzMsImV4cCI6MjA3NDM1MDEzM30.7--8nk7745mYJX_AlEv3O4MzivSpM_ElUJsY904gvD4"; // replace with your anon/public API key

    private static SupabaseApi api;
    private static String currentUserId;
    private static String currentUserName;

    public static SupabaseApi getApi() {
        if (api == null) {
            synchronized (SupabaseClient.class) {
                if (api == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    api = retrofit.create(SupabaseApi.class);
                }
            }
        }
        return api;
    }

    // ================= CURRENT USER =================
    public static void setCurrentUserId(String userId) { currentUserId = userId; }
    public static String getCurrentUserId() { return currentUserId; }

    public static void setCurrentUserName(String username) { currentUserName = username; }
    public static String getCurrentUserName() { return currentUserName; }

    // ================= AUTH =================
    public static void signIn(String email, String password, Context context, AuthCallback callback) {
        SignInRequest request = new SignInRequest(email, password);

        getApi().signInWithEmailPassword(API_KEY, request)
                .enqueue(new Callback<SignInResponse>() {
                    @Override
                    public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            SignInResponse signInResponse = response.body();
                            setCurrentUserId(signInResponse.getUser().getId());
                            getUsernameAndSaveSession(signInResponse, context, callback);
                        } else {
                            callback.onError("Login failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<SignInResponse> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    private static void getUsernameAndSaveSession(SignInResponse signInResponse, Context context, AuthCallback callback) {
        getApi().getUserByEmail(API_KEY, "eq." + signInResponse.getUser().getEmail())
                .enqueue(new Callback<List<Profile>>() {
                    @Override
                    public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                        String username = "Unknown";
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            username = response.body().get(0).getDisplayName();
                        }
                        setCurrentUserName(username);

                        SessionManager.getInstance(context).saveSession(
                                signInResponse.getAccessToken(),
                                signInResponse.getUser().getId(),
                                username,
                                signInResponse.getUser().getEmail()
                        );

                        callback.onSuccess(signInResponse);
                    }

                    @Override
                    public void onFailure(Call<List<Profile>> call, Throwable t) {
                        callback.onError("Failed to fetch username: " + t.getMessage());
                    }
                });
    }

    // ================= POSTS =================
    public static void createPost(String content, Context context, PostCallback callback) {
        String accessToken = SessionManager.getInstance(context).getAccessToken();
        String userId = SessionManager.getInstance(context).getUserId();

        if (accessToken == null || userId == null) {
            callback.onError("No access token or user ID found. Please log in again.");
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("user_id", userId);
        body.put("content", content);

        String authHeader = "Bearer " + accessToken;
        getApi().addPost(API_KEY, authHeader, body)
                .enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Post createdPost = response.body().get(0);
                            callback.onSuccess(createdPost);
                        } else {
                            callback.onError("Failed to create post: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });

    }





    public static void getPosts(Context context, PostsCallback callback) {
        getApi().getPosts()
                .enqueue(new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Failed to fetch posts: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    // ================= LIKES =================


    public static void addPostLike(String postId, Context context, LikeCallback callback) {
        String userId = SessionManager.getInstance(context).getUserId();
        Map<String, Object> likeData = new HashMap<>();
        likeData.put("post_id", postId);
        likeData.put("user_id", userId);

        getApi().addLike(API_KEY, "Bearer " + SessionManager.getInstance(context).getAccessToken(), likeData)
                .enqueue(new Callback<Void>() { // <-- use Void
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(true);
                        } else {
                            callback.onError("Failed to like post: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public static void removeLike(String postId, Context context, LikeCallback callback) {
        String userId = SessionManager.getInstance(context).getUserId();
        String accessToken = SessionManager.getInstance(context).getAccessToken();

        getApi().removeLike(
                API_KEY,
                "Bearer " + accessToken,
                "eq." + postId,   // must be eq.
                "eq." + userId    // must be eq.
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(false);
                } else {
                    callback.onError("Failed to unlike: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }




    // ================= COMMENTS =================
    public static void addPostComment(String postId, String text, Context context, CommentCallback callback) {
        String token = SessionManager.getInstance(context).getAccessToken();
        String userId = SessionManager.getInstance(context).getUserId();

        if (token == null || userId == null) {
            callback.onError("User not logged in");
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("post_id", postId);
        body.put("user_id", userId);
        body.put("content", text); // must match DB column

        getApi().addPostComment(API_KEY, "Bearer " + token, body)
                .enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            // Return the first (new) comment
                            callback.onSuccess(response.body().get(0));
                        } else {
                            callback.onError("Failed to add comment: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Comment>> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    public static void getPostComments(String postId, Context context, CommentsCallback callback) {
        String token = SessionManager.getInstance(context).getAccessToken();
        String filter = "eq." + postId;

        // select all necessary fields, add username if FK exists
        String select = "id,content,created_at,user_id";

        getApi().getPostComments(API_KEY, "Bearer " + token, select, filter)
                .enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Failed to fetch comments: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Comment>> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    // ================= CALLBACKS =================
    public interface AuthCallback {
        void onSuccess(SignInResponse response);
        void onError(String error);
    }

    public interface PostsCallback {
        void onSuccess(List<Post> posts);
        void onError(String error);
    }

    public interface PostCallback {
        void onSuccess(Post post);
        void onError(String error);
    }

    public interface LikeCallback {
        void onSuccess(boolean isLiked);
        void onError(String error);
    }

    public interface CommentCallback {
        void onSuccess(Comment comment);
        void onError(String error);
    }

    public interface CommentsCallback {
        void onSuccess(List<Comment> comments);
        void onError(String error);
    }
}
