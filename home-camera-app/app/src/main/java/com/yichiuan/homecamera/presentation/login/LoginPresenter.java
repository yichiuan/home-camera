package com.yichiuan.homecamera.presentation.login;

import com.yichiuan.homecamera.data.UserRepository;
import com.yichiuan.homecamera.data.remote.model.User;
import com.yichiuan.homecamera.presentation.base.BasePresenter;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class LoginPresenter extends BasePresenter implements LoginContract.Presenter {
    private static final String TAG = "LoginPresenter";
    private final LoginContract.View loginView;
    private final UserRepository userRepository;

    public LoginPresenter(LoginContract.View loginView, UserRepository userRepository) {
        this.loginView = loginView;
        this.userRepository = userRepository;
        loginView.setPresenter(this);
        Timber.tag(TAG);
    }

    @Override
    public void login(String username, String password) {
        loginView.setProgressIndicator(true);

        addSubscription(userRepository.login(username, password)
                .subscribeOn(Schedulers.io())
                .flatMap(token -> {
                    userRepository.saveToken(token.token());
                    return userRepository.getUser();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        loginView.setProgressIndicator(false);
                        loginView.showErrorMessage("Login failed");
                    }

                    @Override
                    public void onNext(User user) {
                        Timber.i("user : " + user.toString());
                        loginView.setProgressIndicator(false);
                        loginView.enterMainUi();
                    }
                }));
    }
}