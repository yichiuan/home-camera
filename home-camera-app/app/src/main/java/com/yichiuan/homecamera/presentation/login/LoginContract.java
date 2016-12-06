package com.yichiuan.homecamera.presentation.login;

import com.yichiuan.homecamera.presentation.base.BaseView;
import com.yichiuan.homecamera.presentation.base.MvpPresenter;

public interface LoginContract {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends MvpPresenter {
        void login(String email, String password);
    }
}