package com.yichiuan.homecamera.presentation.login;

import com.yichiuan.homecamera.data.UserRepository;
import com.yichiuan.homecamera.data.remote.model.Token;
import com.yichiuan.homecamera.presentation.base.BasePresenter;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LoginPresenter extends BasePresenter implements LoginContract.Presenter {
    private final LoginContract.View loginView;

    public LoginPresenter(LoginContract.View loginView) {
        this.loginView = loginView;
        loginView.setPresenter(this);
    }

    @Override
    public void login(String username, String password) {

        loginView.setProgressIndicator(true);

        addSubscription(UserRepository.getInstance().login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Token>() {
                    @Override
                    public void onCompleted() {
                        Timber.i("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        loginView.setProgressIndicator(false);
                        loginView.showErrorMessage("Login failed");
                    }

                    @Override
                    public void onNext(Token token) {
                        Timber.i(token.token());
                        loginView.setProgressIndicator(false);
                        loginView.enterMainUi();
                    }
                }));
    }
}