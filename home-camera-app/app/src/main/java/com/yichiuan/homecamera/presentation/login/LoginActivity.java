package com.yichiuan.homecamera.presentation.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.yichiuan.homecamera.Injection;
import com.yichiuan.homecamera.R;
import com.yichiuan.homecamera.presentation.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;


public class LoginActivity extends AppCompatActivity implements LoginContract.View {
    private static final String LOG_TAG = "LoginActivity";

    @BindView(R.id.edittext_username)
    TextInputEditText usernameEdittext;

    @BindView(R.id.edittext_password)
    TextInputEditText passwordEdittext;

    @BindView(R.id.btn_login)
    Button loginBtn;

    @BindView(R.id.textinputlayout_username)
    TextInputLayout usernameTextinputlayout;

    @BindView(R.id.textinputlayout_password)
    TextInputLayout passwordTextinputlayout;

    private LoginContract.Presenter presenter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        presenter = new LoginPresenter(this, Injection.provideTasksRepository(this));

        loginBtn.setEnabled(false);

        Observable.combineLatest(RxTextView.textChanges(usernameEdittext),
                RxTextView.textChanges(passwordEdittext), (username, password) -> {
                    boolean usernameCheck = username.length() >= 3;
                    boolean passwordCheck = password.length() >= 3;
                    return usernameCheck && passwordCheck;
                }).subscribe(isCheckOk -> {
            loginBtn.setEnabled(isCheckOk);
        });


        loginBtn.setOnClickListener((v) -> {

            final String username = usernameEdittext.getText().toString();
            if (!username.matches("[A-Za-z0-9]+")) {
                usernameTextinputlayout.setErrorEnabled(true);
                usernameTextinputlayout.setError(getString(R.string.login_username_invalid_format));
                return;
            } else {
                usernameTextinputlayout.setErrorEnabled(false);
            }

            loginBtn.setEnabled(false);
            presenter.login(username, passwordEdittext.getText().toString());
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    protected void onStop() {
        presenter.stop();
        super.onStop();
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (active) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(LoginActivity.this);
            }
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.login_authenticating));
            progressDialog.show();
        } else {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void enterMainUi() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void showErrorMessage(@StringRes int resId) {
        Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG).show();
        loginBtn.setEnabled(true);
    }
}
