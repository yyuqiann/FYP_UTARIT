package my.edu.utar.utarit.api;

import java.util.List;
import java.util.Map;

import my.edu.utar.utarit.model.Comment;
import my.edu.utar.utarit.model.Post;
import my.edu.utar.utarit.model.PostCreateRequest;
import my.edu.utar.utarit.model.Profile;
import my.edu.utar.utarit.model.Message;

import my.edu.utar.utarit.network.model.SignInRequest;
import my.edu.utar.utarit.network.model.SignInResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseApi {

   // CORRECT Supabase Auth endpoint
    @Headers("Content-Type: application/json")
    @POST("auth/v1/token?grant_type=password")
    Call<SignInResponse> signInWithEmailPassword(
            @Header("apikey") String apiKey,
            @Body SignInRequest request
    );

    // Sign up endpoint
    @Headers("Content-Type: application/json")
    @POST("auth/v1/signup")
    Call<SignInResponse> signUpWithEmailPassword(
            @Header("apikey") String apiKey,
            @Body SignInRequest request
    );

    @POST("posts")
    Call<List<Post>> createPost(@Body PostCreateRequest request);

 // Update user_metadata for a specific user
 @PATCH("auth/v1/admin/users/{userId}")
 Call<Void> updateUserMetadata(
         @Path("userId") String userId,
         @Body Map<String, Object> userMetadata,
         @Header("Authorization") String authorization
 );


 // Get current user info
    @GET("auth/v1/user")
    Call<SignInResponse.User> getCurrentUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer
    );
   @GET("rest/v1/users")
   Call<List<Profile>> getAllUsers(
           @Header("apikey") String apiKey
   );

   @GET("rest/v1/users?select=id,username,email")
   Call<List<Profile>> getUserByEmail(
           @Header("apikey") String apiKey,
           @Query("email") String emailFilter
   );

    // --- POSTS with proper Supabase query syntax ---
    @GET("rest/v1/posts?select=id,content,created_at,user_id,post_likes(*),post_comments(*)&order=created_at.desc")
    Call<List<Post>> getAllPostsWithDetails(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authHeader
    );
    @GET("posts?select=id,content,created_at,user_id,post_likes(*),post_comments(*)&order=created_at.desc")
    Call<List<Post>> getPosts();




    // Get posts with like count and comment count
    @GET("rest/v1/posts")
    Call<List<Post>> getPostsWithCounts(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authHeader,
            @Query("select") String selectQuery
    );

    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    // --- POST LIKES ---
    @GET("rest/v1/post_likes?select=*")
    Call<List<Map<String, Object>>> getPostLikes(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Query("post_id") String postId,
            @Query("user_id") String userId
    );

 @Headers({
         "Content-Type: application/json",
         "Prefer: return=representation"
 })
 @POST("rest/v1/posts")
 Call<List<Post>> addPost(
         @Header("apikey") String apiKey,
         @Header("Authorization") String auth,
         @Body Map<String, Object> body
 );




    // Insert like
    @POST("rest/v1/post_likes")
    Call<Void> addLike(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authHeader,
            @Body Map<String, Object> body
    );

    // Delete like
    @DELETE("rest/v1/post_likes")
    Call<Void> removeLike(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authHeader,
            @Query("post_id") String postId,
            @Query("user_id") String userId
    );


    // --- REMOVE like ---


    // --- COMMENTS with proper joins ---
    // Add comment
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    @POST("rest/v1/post_comments")
    Call<List<Comment>> addPostComment(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authHeader,
            @Body Map<String, Object> body
    );

    // Get comments for a post
    @GET("rest/v1/post_comments")
    Call<List<Comment>> getPostComments(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authHeader,
            @Query("select") String select,
            @Query("post_id") String postIdFilter  // must be "eq.{postId}"
    );



    @POST("rest/v1/users")
    Call<List<Profile>> createUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Body Map<String, Object> body
    );

    @GET("rest/v1/users")
    Call<List<Profile>> searchUsersByUsername(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Query("username") String usernameLike
    );

    @GET("rest/v1/users")
    Call<List<Profile>> getUserByEmailAndPassword(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Query("email") String email,
            @Query("password") String password
    );

    @GET("rest/v1/profiles")
    Call<List<Profile>> getUserProfile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Query("id") String idFilter
    );

    @PATCH("rest/v1/profiles")
    @Headers("Prefer: return=representation")
    Call<List<Profile>> updateUserProfile(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Query("id") String idFilter,
            @Body Map<String, Object> updates
    );


    @PATCH("rest/v1/posts")
    @Headers("Prefer: return=representation")
    Call<Post> updatePost(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Query("id") String idFilter,
            @Body Post post
    );

    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })



    // --- MESSAGES ---
    @GET("rest/v1/messages")
    Call<List<Message>> getMessages(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Query("or") String userFilter,
            @Query("order") String order
    );

    @Headers("Content-Type: application/json")
    @POST("rest/v1/messages")
    Call<Message> sendMessage(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Body Message message
    );
}
