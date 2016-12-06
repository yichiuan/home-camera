package com.yichiuan.homecamera.presentation.monitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.yichiuan.homecamera.R;
import com.yichiuan.homecamera.util.NetworkUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MonitorFragment extends Fragment implements MonitorContract.View {

    private static final int MESSAGE_ID_RECONNECTING = 0x01;

    @BindView(R.id.video_view)
    PLVideoTextureView videoView;

    @BindView(R.id.loading_view)
    View loadingView;

    @BindView(R.id.btn_saysomething)
    Button btnSaysomething;

    @BindView(R.id.btn_menu)
    Button btnMenu;

    @BindView(R.id.edittext_message)
    EditText edittextMessage;

    @BindView(R.id.btn_close)
    Button btnClose;

    @BindView(R.id.switcher)
    ViewSwitcher switcher;

    private MonitorContract.Presenter presenter;

    private static final String DEFAULT_TEST_URL = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
    private static final String HOME_CAM_URL = "rtmp://192.168.1.11/rtmp/live";
    private String videoPath = HOME_CAM_URL;

    public static MonitorFragment newInstance() {
        MonitorFragment fragment = new MonitorFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_monitor, container, false);
        ButterKnife.bind(this, root);

        initVideoView();

        switcher.setDisplayedChild(0);

        btnSaysomething.setOnClickListener((v) -> switcher.showNext());

        btnClose.setOnClickListener((v) -> switcher.showNext());

        return root;
    }

    private void initVideoView() {
        videoView.setBufferingIndicator(loadingView);
        loadingView.setVisibility(View.VISIBLE);


        AVOptions options = new AVOptions();

        final int isLiveStreaming = 1;
        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, isLiveStreaming);
        if (isLiveStreaming == 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        int codec = 1;
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        videoView.setAVOptions(options);

        videoView.setOnErrorListener(onErrorListener);
        videoView.setVideoPath(videoPath);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public void onStop() {
        presenter.stop();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }

    @Override
    public void setPresenter(MonitorContract.Presenter presenter) {
        this.presenter = presenter;
    }

    private PLMediaPlayer.OnErrorListener onErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            boolean isNeedReconnect = false;
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToastTips("Invalid URL !");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showToastTips("404 resource not found !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showToastTips("Connection refused !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showToastTips("Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showToastTips("Empty playlist !");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showToastTips("Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    showToastTips("Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showToastTips("Unauthorized Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showToastTips("Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showToastTips("Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    showToastTips("unknown error !");
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
            if (isNeedReconnect) {
                sendReconnectMessage();
            } else {
                getActivity().finish();
            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };

    private void showToastTips(final String tips) {

        Toast.makeText(getActivity().getApplicationContext(),
                tips,
                Toast.LENGTH_SHORT).show();
    }

    private void sendReconnectMessage() {
        showToastTips("Reconnecting...");
        loadingView.setVisibility(View.VISIBLE);
        handler.removeCallbacksAndMessages(null);
        handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
    }

    protected Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != MESSAGE_ID_RECONNECTING) {
                return;
            }

            // TODO check the live stream is available

            if (!NetworkUtil.isNetworkAvailable(getActivity().getApplicationContext())) {
                sendReconnectMessage();
                return;
            }

            videoView.setVideoPath(videoPath);
            videoView.start();
        }
    };
}