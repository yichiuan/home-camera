package com.yichiuan.homecamera.data;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yichiuan.homecamera.BuildConfig;
import com.yichiuan.homecamera.data.remote.UserService;
import com.yichiuan.homecamera.data.remote.model.Token;
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

    private static final String USER_API_BASE= BuildConfig.API_SERVER_URL;

    private UserService userService;

    private UserRepository() {

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging =
                    new HttpLoggingInterceptor((message) ->
                            Timber.tag("UserRepository").d(message));
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

            httpClientBuilder.addInterceptor(logging);

            StethoHelper.configureInterceptor(httpClientBuilder);
        }

        httpClientBuilder.connectTimeout(ONNECT_TIMEOUT_SECINDS, TimeUnit.SECONDS);

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

    private static class SingletonHolder {
        private static final UserRepository INSTANCE = new UserRepository();
    }

    public static UserRepository getInstance() {
        return SingletonHolder.INSTANCE;
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
}
