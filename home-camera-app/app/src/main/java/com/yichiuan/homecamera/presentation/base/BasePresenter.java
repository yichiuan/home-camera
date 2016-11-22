package com.yichiuan.homecamera.presentation.base;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BasePresenter implements MvpPresenter {

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        compositeSubscription.clear();
    }

    protected void addSubscription(Subscription subscription) {
        this.compositeSubscription.add(subscription);
    }
}