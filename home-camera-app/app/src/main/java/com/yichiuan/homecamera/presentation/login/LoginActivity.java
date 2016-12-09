package com.yichiuan.homecamera.presentation.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yichiuan.homecamera.R;
import com.yichiuan.homecamera.presentation.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity implements LoginContract.View {
    @BindView(R.id.edittext_username)
    EditText usernameEdittext;
    @BindView(R.id.edittext_password)
    EditText passwordEdittext;
    @BindView(R.id.btn_login)
    Button loginBtn;

    private LoginContract.Presenter presenter;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        presenter = new LoginPresenter(this);

        loginBtn.setOnClickListener((v) -> {
            loginBtn.setEnabled(false);
            presenter.login(usernameEdittext.getText().toString(),
                    passwordEdittext.getText().toString());
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
            progressDialog.setMessage("Authenticating...");
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
    public void showErrorMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        loginBtn.setEnabled(true);
    }
}
