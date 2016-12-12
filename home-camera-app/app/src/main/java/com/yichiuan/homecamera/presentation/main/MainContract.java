package com.yichiuan.homecamera.presentation.main;

import com.yichiuan.homecamera.presentation.base.BaseView;
import com.yichiuan.homecamera.presentation.base.MvpPresenter;

public interface MainContract {
    interface View extends BaseView<Presenter> {
        void setProgressIndicator(boolean active);
    }

    interface Presenter extends MvpPresenter {
        void loadUser();
    }
}