package com.yichiuan.homecamera.presentation.monitor;


import com.yichiuan.homecamera.presentation.base.BasePresenter;

public class MonitorPresenter extends BasePresenter implements MonitorContract.Presenter {
    private final MonitorContract.View view;

    public MonitorPresenter(MonitorContract.View view) {
        this.view = view;
        view.setPresenter(this);
    }
}