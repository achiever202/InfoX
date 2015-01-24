package org.iith.scitech.infero.infox.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.ui.BrowseActivity;

/**
 * Created by shashank on 23/1/15.
 */
public class MusicWidget
{
    private View tileView;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private Context context;
    private String audioFilePath;

    private Boolean isPreparedAudio = false;
    private Boolean isStartedAudio = false;
    private Boolean isFinishedAudio = false;
    private int currentPercentageAudio = 0;
    private Boolean isPreparingAudio = false;
    private Boolean audioPlaybackThreadRunning = false;


    public MusicWidget(View tileView, Context context, String dataSource)
    {
        this.tileView = tileView;
        this.context = context;

        initializeTileViews();
        mediaPlayer = new MediaPlayer();

        setDataSource(dataSource);
        setSeekBarChangeListener();
        setOnPreparedListener();
        setBufferUpdateListener();
        setCompletionListener();
        attachPlayButton();

    }

    private void initializeTileViews()
    {
        seekBar = (SeekBar) tileView.findViewById(R.id.content_tile_music_seekBar);
    }


    private void setDataSource(String dataSource)
    {
        try
        {
            //mediaPlayer.setDataSource("/sdcard/Music/maine.mp3");//Write your location here
            Uri uri = Uri.parse(dataSource);
            mediaPlayer.setDataSource(context, uri);
        }
        catch(Exception e){e.printStackTrace();}
    }


    public void setSeekBarChangeListener()
    {
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
    }

    public void setOnPreparedListener()
    {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mediaPlayer.getDuration());
                Thread t = thread();
                t.start();
                isPreparedAudio = true;
                mediaPlayer.start();
            }
        });
    }

    private void setBufferUpdateListener()
    {
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if(!(percent==currentPercentageAudio) && percent!=100) {
                    Toast.makeText(context, "Audio Buffering: " + percent + "%", Toast.LENGTH_SHORT).show();
                    currentPercentageAudio = percent;
                }
            }
        });
    }


    private void setCompletionListener()
    {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(context, "Audio Playback Complete", Toast.LENGTH_SHORT).show();
                audioPlaybackThreadRunning = false;
                isStartedAudio = false;
                isFinishedAudio = true;
            }
        });
    }

    private Thread thread()
    {
        return new Thread()
        {
            public void run() {
                audioPlaybackThreadRunning = true;
                try
                {
                    while(mediaPlayer.getDuration()!=mediaPlayer.getCurrentPosition() && audioPlaybackThreadRunning)
                    {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
                catch (Exception e)
                {
                    Log.e("log",e.toString());
                }
            }
        };
    }


    private void attachPlayButton()
    {
        tileView.findViewById(R.id.content_tile_music_playBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPreparedAudio && !isStartedAudio) {
                    if(isFinishedAudio)
                    {
                        Thread t = thread();
                        t.start();
                        isFinishedAudio = false;
                    }
                    mediaPlayer.start();
                    isStartedAudio = true;
                }
                else
                if(isPreparedAudio && isStartedAudio) {
                    Toast.makeText(context, "Audio still Buffering", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!isPreparingAudio) {
                        Toast.makeText(context, "Preparing Audio Now", Toast.LENGTH_SHORT).show();
                        mediaPlayer.prepareAsync();
                        isPreparingAudio = true;
                    }
                    else
                        Toast.makeText(context, "Audio not yet prepared", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*BACKUP CODE*/

    /*final SeekBar seekBar = (SeekBar) tileView.findViewById(R.id.content_tile_music_seekBar);
                final MediaPlayer mediaPlayer = new MediaPlayer();
                try{
                    //mediaPlayer.setDataSource("/sdcard/Music/maine.mp3");//Write your location here
                    Uri uri = Uri.parse("http://media.djmazadownload.com/music/320/indian_movies/Khamoshiyan%20(2015)/01%20-%20Khamoshiyan%20-%20Khamoshiyan%20%5BDJMaza.Info%5D.mp3");
                    mediaPlayer.setDataSource(BrowseActivity.this, uri);

                }catch(Exception e){e.printStackTrace();}

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
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



                mediaPlayer.setOnPreparedListener(new
                                                        MediaPlayer.OnPreparedListener()  {
                                                            @Override
                                                            public void onPrepared(MediaPlayer mp) {
                                                                seekBar.setMax(mediaPlayer.getDuration());
                                                                new Thread()
                                                                {
                                                                    public void run() {
                                                                        audioPlaybackThreadRunning = true;
                                                                        try
                                                                        {
                                                                            while(mediaPlayer.getDuration()!=mediaPlayer.getCurrentPosition() && audioPlaybackThreadRunning)
                                                                            {
                                                                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                                                                //bStop.setText(song1.getCurrentPosition());
                                                                            }
                                                                            //if (mediaPlayer.getDuration()==mediaPlayer.getCurrentPosition()) {
                                                                            //    Toast.makeText(BrowseActivity.this, "Audio finished", Toast.LENGTH_SHORT).show();
                                                                            //    this.execute = false;
                                                                                //this.interrupt();
                                                                                //this.join();
                                                                            //}
                                                                            //t.suspend();
                                                                        }
                                                                        catch (Exception e)
                                                                        {
                                                                            Log.e("log",e.toString());
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
                        if(!(percent==currentPercentageAudio) && percent!=100) {
                            Toast.makeText(BrowseActivity.this, "Audio Buffering: " + percent + "%", Toast.LENGTH_SHORT).show();
                            currentPercentageAudio = percent;
                        }
                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Toast.makeText(BrowseActivity.this, "Audio Playback Complete", Toast.LENGTH_SHORT).show();
                        audioPlaybackThreadRunning = false;
                        //mediaPlayer.stop();
                        //mediaPlayer.release();
                        //seekBar.setProgress(0);
                        isStartedAudio = false;
                        //isPreparedAudio = false;
                        //isPreparingAudio = false;
                        isFinishedAudio = true;
                    }
                });


                tileView.findViewById(R.id.content_tile_music_playBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isPreparedAudio && !isStartedAudio) {
                            if(isFinishedAudio)
                            {
                                new Thread()
                                {
                                    public void run() {
                                        audioPlaybackThreadRunning = true;
                                        try
                                        {
                                            while(mediaPlayer.getDuration()!=mediaPlayer.getCurrentPosition() && audioPlaybackThreadRunning)
                                            {
                                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                                //bStop.setText(song1.getCurrentPosition());
                                            }
                                        }
                                        catch (Exception e)
                                        {
                                            Log.e("log",e.toString());
                                        }
                                    }
                                }.start();
                                isFinishedAudio = false;
                            }
                            mediaPlayer.start();
                            isStartedAudio = true;
                        }
                        else
                        if(isPreparedAudio && isStartedAudio) {
                            Toast.makeText(BrowseActivity.this, "Audio still Buffering", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(!isPreparingAudio) {
                                Toast.makeText(BrowseActivity.this, "Preparing Audio Now", Toast.LENGTH_SHORT).show();
                                mediaPlayer.prepareAsync();
                                isPreparingAudio = true;
                            }
                            else
                                Toast.makeText(BrowseActivity.this, "Audio not yet prepared", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

}
