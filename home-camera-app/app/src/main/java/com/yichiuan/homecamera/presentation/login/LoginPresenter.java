package com.yichiuan.homecamera.presentation.login;


import com.yichiuan.homecamera.presentation.base.BasePresenter;

public class LoginPresenter extends BasePresenter implements LoginContract.Presenter {
    private final LoginContract.View view;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void login(String email, String password) {

    }
}