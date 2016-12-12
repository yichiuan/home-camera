package com.yichiuan.homecamera.data.remote;

import com.yichiuan.homecamera.data.remote.model.Token;
import com.yichiuan.homecamera.data.remote.model.User;

import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;


public interface UserService {
    @GET("/auth/token")
    Observable<Token> login(@Header("Authorization") String authorization);

    @GET("/user/me")
    Observable<User> getUser();
}
