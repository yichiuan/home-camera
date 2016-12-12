package com.yichiuan.homecamera.data.remote;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class AuthenticationInterceptor implements Interceptor {
    private String credentials = null;
    private boolean hasToken = false;

    public AuthenticationInterceptor(String token) {
        if (!TextUtils.isEmpty(token)) {
            setupCredentialsWith(token);
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();

        if (hasToken) {
            requestBuilder.header("Authorization", credentials);
        }

        return chain.proceed(requestBuilder.build());
    }

    public void setupCredentialsWith(String token) {
        hasToken = true;
        credentials = "Bearer " + token;
    }
}