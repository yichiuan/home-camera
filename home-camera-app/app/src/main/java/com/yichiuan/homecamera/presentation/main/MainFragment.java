package com.yichiuan.homecamera.presentation.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yichiuan.homecamera.R;
import com.yichiuan.homecamera.presentation.monitor.MonitorActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainFragment extends Fragment implements MainContract.View {
    private MainContract.Presenter presenter;

    private ProgressDialog progressDialog;

    @BindView(R.id.button_start)
    Button buttonStart;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, root);

        buttonStart.setOnClickListener((v) -> {
            Intent intent = new Intent(getContext(), MonitorActivity.class);
            startActivity(intent);
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
        presenter.loadUser();
    }

    @Override
    public void onStop() {
        presenter.stop();
        super.onStop();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (active) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getContext());
            }
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        } else {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }
}