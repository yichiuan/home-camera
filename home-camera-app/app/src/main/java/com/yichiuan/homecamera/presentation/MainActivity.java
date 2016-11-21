package com.yichiuan.homecamera.presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.yichiuan.homecamera.R;

public class MainActivity extends AppCompatActivity {

    PLVideoTextureView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
