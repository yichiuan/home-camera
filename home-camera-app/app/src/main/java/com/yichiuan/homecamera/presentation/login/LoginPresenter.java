package com.yichiuan.homecamera.presentation.login;

import com.yichiuan.homecamera.R;
import com.yichiuan.homecamera.data.UserRepository;
import com.yichiuan.homecamera.data.remote.model.Token;
import com.yichiuan.homecamera.presentation.base.BasePresenter;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class LoginPresenter extends BasePresenter implements LoginContract.Presenter {
    private static final String LOG_TAG = "LoginPresenter";
    private final LoginContract.View loginView;
    private final UserRepository userRepository;

    public LoginPresenter(LoginContract.View loginView, UserRepository userRepository) {
        this.loginView = loginView;
        this.userRepository = userRepository;
        loginView.setPresenter(this);
    }

    @Override
    public void login(String username, String password) {
        loginView.setProgressIndicator(true);

        addSubscription(userRepository.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Token>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Timber.tag(LOG_TAG).e(e);
                        loginView.setProgressIndicator(false);
                        loginView.showErrorMessage(R.string.login_error_message);
                    }

                    @Override
                    public void onNext(Token token) {
                        Timber.tag(LOG_TAG).i("token = " + token.toString());
                        userRepository.saveToken(token.token());
                        loginView.setProgressIndicator(false);
                        loginView.enterMainUi();
                    }
                }));
    }
}