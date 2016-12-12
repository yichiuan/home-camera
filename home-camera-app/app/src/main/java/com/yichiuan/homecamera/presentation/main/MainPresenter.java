package com.yichiuan.homecamera.presentation.main;

import com.yichiuan.homecamera.data.UserRepository;
import com.yichiuan.homecamera.data.remote.model.User;
import com.yichiuan.homecamera.presentation.base.BasePresenter;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainPresenter extends BasePresenter implements MainContract.Presenter {
    private static final String TAG = "MainPresenter";
    private final MainContract.View mainView;
    private final UserRepository userRepository;

    public MainPresenter(MainContract.View view, UserRepository userRepository) {
        this.mainView = view;
        this.userRepository = userRepository;
        view.setPresenter(this);
        Timber.tag(TAG);
    }

    @Override
    public void loadUser() {
        mainView.setProgressIndicator(true);

        addSubscription(userRepository.getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        mainView.setProgressIndicator(false);
                    }

                    @Override
                    public void onNext(User user) {
                        Timber.i("user : " + user.toString());
                        mainView.setProgressIndicator(false);
                    }
                }));
    }
}