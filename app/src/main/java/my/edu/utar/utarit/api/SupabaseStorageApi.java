package my.edu.utar.utarit.api;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface SupabaseStorageApi {
    @Multipart
    @POST("storage/v1/object/{bucket}/{path}")
    Call<ResponseBody> uploadFile(
            @Header("Authorization") String bearerToken,
            @Header("apikey") String apiKey,
            @Path("bucket") String bucket,
            @Path("path") String path,
            @Part MultipartBody.Part file
    );
}
