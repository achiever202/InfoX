package org.iith.scitech.infero.infox.ui;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.URLUtil;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.util.JsonUtils;
import org.iith.scitech.infero.infox.util.PrefUtils;

/**
 * Created by shashank on 25/1/15.
 */
public class VideoDialog extends Activity
{
    MediaController mediaController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_dialog_video);

        String videoPath = PrefUtils.getCurrentVideoPath(VideoDialog.this);
        Log.v("VideoPAth: ", videoPath);
        final VideoView videoView = (VideoView) findViewById(R.id.content_tile_video_videoView);

        if(URLUtil.isValidUrl(videoPath))
        {
            Uri uri = Uri.parse(videoPath);
            videoView.setVideoPath(videoPath);
            Toast.makeText(VideoDialog.this, "Content not available offline: Streaming online", Toast.LENGTH_SHORT).show();
        }
        else {
            videoView.setVideoPath(PrefUtils.getDownloadDirectory(VideoDialog.this) + "/" + videoPath);
            Log.v("VIDEO DIALOG:: ", PrefUtils.getDownloadDirectory(VideoDialog.this) + "/" + videoPath);
        }

        videoView.setZOrderMediaOverlay(true);
        videoView.setZOrderOnTop(true);
        videoView.requestFocus();

        //mediaController.show();

        mediaController = new MediaController(VideoDialog.this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        mediaController.setPadding(0, 0, 40, 20);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
            @Override
            public void onPrepared(MediaPlayer mp) {
                /*mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        mediaController = new MediaController(VideoDialog.this);
                        mediaController.setAnchorView(videoView);
                        videoView.setMediaController(mediaController);
                    }
                });*/
                //Log.i("Video", "Duration = " + videoView.getDuration());
                Toast.makeText(VideoDialog.this, "Video prepared", Toast.LENGTH_SHORT).show();
                //videoView.start();
            }
        });

        videoView.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
