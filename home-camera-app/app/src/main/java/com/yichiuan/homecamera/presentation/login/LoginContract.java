package com.yichiuan.homecamera.presentation.login;

import android.support.annotation.StringRes;

import com.yichiuan.homecamera.presentation.base.BaseView;
import com.yichiuan.homecamera.presentation.base.MvpPresenter;

public interface LoginContract {
    interface View extends BaseView<Presenter> {
        void setProgressIndicator(boolean active);
        void showErrorMessage(@StringRes int resId);
        void enterMainUi();
    }

    interface Presenter extends MvpPresenter {
        void login(String email, String password);
    }
}