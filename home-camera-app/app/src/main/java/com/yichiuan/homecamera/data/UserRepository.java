package com.yichiuan.homecamera.data;

import android.content.Context;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yichiuan.homecamera.BuildConfig;
import com.yichiuan.homecamera.data.local.UserPreferences;
import com.yichiuan.homecamera.data.remote.AuthenticationInterceptor;
import com.yichiuan.homecamera.data.remote.UserService;
import com.yichiuan.homecamera.data.remote.model.Token;
import com.yichiuan.homecamera.data.remote.model.User;
import com.yichiuan.homecamera.util.StethoHelper;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import timber.log.Timber;


public class UserRepository {

    private static final int ONNECT_TIMEOUT_SECINDS = 10;

    private static final String USER_API_BASE = BuildConfig.API_SERVER_URL;

    private static UserRepository INSTANCE = null;

    private UserPreferences userPreferences;

    private UserService userService;

    private AuthenticationInterceptor authenticationInterceptor;

    private UserRepository(Context context) {

        userPreferences = new UserPreferences(context);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging =
                    new HttpLoggingInterceptor((message) ->
                            Timber.tag("UserRepository").d(message));
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            httpClientBuilder.addInterceptor(logging);

            StethoHelper.configureInterceptor(httpClientBuilder);
        }

        httpClientBuilder.connectTimeout(ONNECT_TIMEOUT_SECINDS, TimeUnit.SECONDS);
        authenticationInterceptor = new AuthenticationInterceptor(userPreferences.getAccessToken());
        httpClientBuilder.addInterceptor(authenticationInterceptor);

        OkHttpClient client = httpClientBuilder.build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(UserAdapterFactory.create())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(USER_API_BASE)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        userService = retrofit.create(UserService.class);
    }

    public static UserRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new UserRepository(context);
        }
        return INSTANCE;
    }

    public Observable<Token> login(String username, String password) {
        final String userAndPassword = username + ":" + password;

        String credentials;
        try {
            credentials = "Basic " + Base64.encodeToString(
                    userAndPassword.getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            credentials = "";
        }

        return userService.login(credentials);
    }

    public Observable<User> getUser() {
        return userService.getUser();
    }

    public boolean isLoggedIn() {
        return userPreferences.isLoggedIn();
    }

    public void saveToken(String token) {
        authenticationInterceptor.setupCredentialsWith(token);
        userPreferences.setAccessToken(token);
    }
}
