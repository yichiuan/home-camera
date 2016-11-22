package com.yichiuan.homecamera.presentation.monitor;

import com.yichiuan.homecamera.presentation.base.BaseView;
import com.yichiuan.homecamera.presentation.base.MvpPresenter;

public interface MonitorContract {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends MvpPresenter {
    }
}