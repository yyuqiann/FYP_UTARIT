package my.edu.utar.utarit.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import my.edu.utar.utarit.api.SupabaseApi;
import my.edu.utar.utarit.SupabaseConfig;

public class SupabaseClient {

    public static final String BASE_URL = "https://ioqewcnjzyqizjqeclyy.supabase.co/";
    public static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlvcWV3Y25qenlxaXpqcWVjbHl5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTgwOTY4MTMsImV4cCI6MjA3MzY3MjgxM30.WqX_33DLnmeVb8WWe5BiAUOJYijD1FffN6epN2xxfhE";
    private static SupabaseApi api;
    private static String accessToken;

    public static SupabaseApi getApi() {
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SupabaseConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            api = retrofit.create(SupabaseApi.class);
        }
        return api;
    }

    public static void setAccessToken(String token) {
        accessToken = token;
    }

    public static String getAccessToken() {
        return accessToken;
    }
}
