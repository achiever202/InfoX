package org.iith.scitech.infero.infox.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.util.JsonUtils;
import org.iith.scitech.infero.infox.util.PrefUtils;

/**
 * Created by shashank on 25/1/15.
 */
public class MusicDialog extends Activity
{
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    Button playPause;
    private Boolean isPreparedAudio = false;
    private Boolean isStartedAudio = false;
    private Boolean isFinishedAudio = false;
    private int currentPercentageAudio = 0;
    private Boolean isPreparingAudio = false;
    private Boolean audioPlaybackThreadRunning = false;
    private Boolean isPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_dialog_music);

        seekBar = (SeekBar) findViewById(R.id.content_dialog_music_seekBar);
        playPause = (Button) findViewById(R.id.content_dialog_music_playPauseButton);

        String musicPath = PrefUtils.getCurrentMusicPath(MusicDialog.this);

        mediaPlayer = new MediaPlayer();
        try
        {
            //mediaPlayer.setDataSource("/sdcard/Music/maine.mp3");//Write your location here
            if(URLUtil.isValidUrl(musicPath))
            {
                Uri uri = Uri.parse(musicPath);
                mediaPlayer.setDataSource(MusicDialog.this, uri);
                Toast.makeText(MusicDialog.this, "Content not available offline: Streaming online", Toast.LENGTH_SHORT).show();
            }
            else
                mediaPlayer.setDataSource(PrefUtils.getDownloadDirectory(MusicDialog.this)+"/"+musicPath);
        }
        catch(Exception e){e.printStackTrace();}

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                    Drawable d = getResources().getDrawable(R.drawable.ic_music_pause_light);
                    playPause.setBackground(d);
                    isPlaying = false;
                }
                else
                    if(!isPlaying) {
                        mediaPlayer.start();
                        Drawable d = getResources().getDrawable(R.drawable.ic_video_play_light);
                        playPause.setBackground(d);
                        isPlaying = true;
                    }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2)
            {
                if (arg2 && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(arg1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mediaPlayer.getDuration());
                new Thread() {
                    public void run() {
                        audioPlaybackThreadRunning = true;
                        try {
                            while (mediaPlayer.getDuration() != mediaPlayer.getCurrentPosition() && audioPlaybackThreadRunning) {
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            }
                        } catch (Exception e) {
                            Log.e("log", e.toString());
                        }
                    }
                }.start();
                isPreparedAudio = true;
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (!(percent == currentPercentageAudio) && percent != 100) {
                    Toast.makeText(MusicDialog.this, "Audio Buffering: " + percent + "%", Toast.LENGTH_SHORT).show();
                    currentPercentageAudio = percent;
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(MusicDialog.this, "Audio Playback Complete", Toast.LENGTH_SHORT).show();
                audioPlaybackThreadRunning = false;
                isStartedAudio = false;
                isFinishedAudio = true;
            }
        });

        mediaPlayer.prepareAsync();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
            if(mediaPlayer!=null)
            {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
    @Override
    protected void onPause()
    {
        super.onPause();
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    */

    /*
    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
