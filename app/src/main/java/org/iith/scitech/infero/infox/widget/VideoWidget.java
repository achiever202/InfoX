package org.iith.scitech.infero.infox.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import org.iith.scitech.infero.infox.R;

/**
 * Created by shashank on 23/1/15.
 */
public class VideoWidget
{
    private View tileView;
    private Context context;

    private VideoView videoView;

    private Boolean isPreparedVideo = false;
    private Boolean isStartedVideo = false;
    private Boolean isPreparingVideo = false;


    private void initializeTileViews()
    {
        videoView = (VideoView) tileView.findViewById(R.id.content_tile_video_videoView);
    }

    private MediaPlayer.OnPreparedListener prepareListener()
    {
        return new

                MediaPlayer.OnPreparedListener()  {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        isPreparedVideo = true;
                        Log.i("Video", "Duration = " + videoView.getDuration());
                        Toast.makeText(context, "Video prepared: Click again to play", Toast.LENGTH_SHORT).show();
                        //videoView.start();
                    }
                };
    }

    public VideoWidget(View tileView, Context context, String dataSource)
    {
        this.tileView = tileView;
        this.context = context;

        initializeTileViews();
        videoView.setVideoPath(dataSource);
        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(prepareListener());

        attachButton();

    }

    private void attachButton()
    {
        tileView.findViewById(R.id.content_tile_video_playBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPreparedVideo && !isStartedVideo) {
                    videoView.start();
                    isStartedVideo = true;
                }
                else
                if(isPreparedVideo && isStartedVideo) {
                    Toast.makeText(context, "Video Buffer", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!isPreparingVideo) {
                        Toast.makeText(context, "Preparing Video for play", Toast.LENGTH_SHORT).show();
                        isPreparingVideo = true;
                    }
                    else
                        Toast.makeText(context, "Video not yet prepared", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
