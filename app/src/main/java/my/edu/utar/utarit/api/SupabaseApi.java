package my.edu.utar.utarit.api;

import java.util.List;
import java.util.Map;

import my.edu.utar.utarit.model.Chat;
import my.edu.utar.utarit.model.Comment;
import my.edu.utar.utarit.model.Message;
import my.edu.utar.utarit.model.Post;
import my.edu.utar.utarit.model.Profile;
import my.edu.utar.utarit.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseApi {

    // Get all users
    @GET("users")
    Call<List<Profile>> getAllUsers(@Header("apikey") String apiKey, @Header("Authorization") String bearer);

    @POST("users")
    Call<Profile> createUser(@Header("apikey") String apiKey, @Header("Authorization") String bearer, @Body Map<String, Object> body);

    @GET("rest/v1/profiles")
    Call<List<Profile>> searchUsersByUsername(
            @Header("apikey") String apiKey,
            @Query("username") String usernameQuery
    );
    @GET("rest/v1/profile")
    Call<List<Profile>> getUserProfile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Query("id") String filter
    );

    @PATCH("rest/v1/profile")
    Call<List<Profile>> updateUserProfile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Query("id") String filter,
            @Body Map<String, Object> updates
    );


    @GET("rest/v1/posts")
    Call<List<Post>> getAllPosts(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth
    );

    @POST("rest/v1/posts")
    Call<Post> addPost(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Body Post post
    );

    @PATCH("rest/v1/posts")
    Call<Post> updatePost(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Query("id") String idFilter,
            @Body Post post
    );

    @PATCH("posts/{postId}")
    Call<Post> updatePostLikes(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Path("postId") String postId,
            @Body int likes
    );

    @GET("posts")
    Call<List<Post>> getPosts(@Header("Authorization") String bearer);

    @POST("posts/{postId}/like")
    Call<Void> likePost(@Header("Authorization") String bearer,
                        @Path("postId") String postId,
                        @Query("userId") String userId);

    Call<List<Comment>> getComments(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Query("postId") String postId
    );


    @POST("comments")
    Call<Comment> addComment(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Body Map<String, Object> commentBody
    );


    @GET("messages")
    Call<List<Message>> getMessages(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Query("or") String userFilter,  // eg: "senderId.eq.{userId},receiverId.eq.{userId}"
            @Query("order") String order     // eg: "timestamp.desc"
    );

    @POST("messages")
    Call<Message> sendMessage(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Body Message message
    );
}
