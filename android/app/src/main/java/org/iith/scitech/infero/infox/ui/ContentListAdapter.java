package org.iith.scitech.infero.infox.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.data.ContentListProvider;
import org.iith.scitech.infero.infox.util.JsonUtils;
import org.iith.scitech.infero.infox.util.PrefUtils;
import org.iith.scitech.infero.infox.widget.EducationWidget;
import org.iith.scitech.infero.infox.widget.MusicWidget;
import org.iith.scitech.infero.infox.widget.VideoWidget;
import org.iith.scitech.infero.infox.widget.WeatherWidget;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shashank on 24/1/15.
 */

public class ContentListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    private Boolean isPreparedAudio = false;
    private Boolean isStartedAudio = false;
    private Boolean isFinishedAudio = false;
    private int currentPercentageAudio = 0;
    private Boolean isPreparingAudio = false;
    private Boolean audioPlaybackThreadRunning = false;

    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;

    public ContentListAdapter(Context context, List<String> values) {
        super(context, R.layout.content_tile_education, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getViewTypeCount() {
        return 4; //return 4, you have two types that the getView() method will return, normal(0) and for the last row(1)
    }

    @Override
    public int getItemViewType(int position) {
        int retVal = -1;
        JSONObject jsonObject = null;
        String tileType = JsonUtils.getData(values.get(position), "tileType");
        switch (tileType)
        {
            case BrowseActivity.TILE_EDUCATION:
                retVal = 0;
                break;

            case BrowseActivity.TILE_WEATHER:
                retVal = 1;
                break;

            case BrowseActivity.TILE_MUSIC:
                retVal =  2;
                break;

            case BrowseActivity.TILE_VIDEO:
                retVal = 3;
                break;
        }
        return retVal;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String s = values.get(position);
        //Log.v("DEB","value: "+s);
        View rowView = convertView;

        int theType = getItemViewType(position);
        //Log.v("HELLO", theType+"");

        if (theType==0)
        {
            rowView = layoutInflater.inflate(R.layout.content_tile_education, parent, false);

            ImageView educationTypeI = (ImageView) rowView.findViewById(R.id.content_tile_education_img);
            TextView educationTypeT = (TextView) rowView.findViewById(R.id.content_tile_education_type);
            TextView educationText = (TextView) rowView.findViewById(R.id.content_tile_education_text);

            educationText.setText(JsonUtils.getData(s,"content"));

            switch (JsonUtils.getData(s,"category"))
            {
                case "EDU":
                    educationTypeI.setImageResource(R.drawable.ic_education);
                    educationTypeT.setText("EDUCATION");
                    break;
            }
        }
        else if (theType==1)
        {
            rowView = layoutInflater.inflate(R.layout.content_tile_weather, parent, false);

            ImageView weatherTypeI = (ImageView) rowView.findViewById(R.id.img_content_type);
            TextView weatherTemp = (TextView) rowView.findViewById(R.id.content_tile_weather_temp);
            TextView weatherTypeT = (TextView) rowView.findViewById(R.id.content_tile_weather_type);
            TextView weatherTime = (TextView) rowView.findViewById(R.id.content_tile_weather_time);
            TextView weatherDay = (TextView) rowView.findViewById(R.id.content_tile_weather_day);

            weatherTemp.setText(JsonUtils.getData(s,"content").split(";")[0]+" ºC");
            weatherTime.setText(JsonUtils.getData(s,"content").split(";")[1]);
            weatherDay.setText(JsonUtils.getData(s,"content").split(";")[2]);

            switch (JsonUtils.getData(s,"category"))
            {
                case "PS":
                    weatherTypeI.setImageResource(R.drawable.ic_weather_02);
                    weatherTypeT.setText("Partly Sunny");
                    break;
            }
        }
        else if (theType==2)
        {
            rowView = layoutInflater.inflate(R.layout.content_tile_music, parent, false);

            seekBar = (SeekBar) rowView.findViewById(R.id.content_tile_music_seekBar);
            mediaPlayer = new MediaPlayer();
            try
            {
                //mediaPlayer.setDataSource("/sdcard/Music/maine.mp3");//Write your location here
                if(URLUtil.isValidUrl(JsonUtils.getData(s,"content")))
                {
                    Uri uri = Uri.parse(JsonUtils.getData(s,"content"));
                    mediaPlayer.setDataSource(context, uri);
                    Toast.makeText(context, "Content not available offline: Streaming online", Toast.LENGTH_SHORT).show();
                }
                else
                    mediaPlayer.setDataSource(JsonUtils.getData(s,"content"));
            }
            catch(Exception e){e.printStackTrace();}

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
                        Toast.makeText(context, "Audio Buffering: " + percent + "%", Toast.LENGTH_SHORT).show();
                        currentPercentageAudio = percent;
                    }
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(context, "Audio Playback Complete", Toast.LENGTH_SHORT).show();
                    audioPlaybackThreadRunning = false;
                    isStartedAudio = false;
                    isFinishedAudio = true;
                }
            });

            rowView.findViewById(R.id.content_tile_music_pauseBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mediaPlayer.isPlaying())
                        mediaPlayer.pause();
                }
            });

            rowView.findViewById(R.id.content_tile_music_playBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPreparedAudio && !isStartedAudio) {
                        if (isFinishedAudio) {
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
                            isFinishedAudio = false;
                        }
                        mediaPlayer.start();

                        isStartedAudio = true;
                    } else if (isPreparedAudio && isStartedAudio) {
                        if(!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                        else
                            Toast.makeText(context, "Audio still Buffering", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!isPreparingAudio) {
                            Toast.makeText(context, "Preparing Audio Now", Toast.LENGTH_SHORT).show();
                            mediaPlayer.prepareAsync();
                            isPreparingAudio = true;
                        } else
                            Toast.makeText(context, "Audio not yet prepared", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        else if (theType==3)
        {
            rowView = layoutInflater.inflate(R.layout.content_tile_video, parent, false);
            rowView.findViewById(R.id.content_tile_video_videoView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*final Dialog dialog = new Dialog(context);
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    lp.dimAmount = 0;
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    dialog.getWindow().setAttributes(lp);
                    dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.content_dialog_video);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();

                    final VideoView videoView = (VideoView) dialog.findViewById(R.id.content_tile_video_videoView);
                    videoView.setVideoPath(values.get(position).split(";")[1]);
                    videoView.setZOrderMediaOverlay(true);
                    videoView.requestFocus();
                    MediaController mediaController = new MediaController(dialog.getContext());
                    mediaController.setAnchorView(videoView);
                    videoView.setMediaController(mediaController);
                    //mediaController.show();

                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            //Log.i("Video", "Duration = " + videoView.getDuration());
                            Toast.makeText(dialog.getContext(), "Video prepared", Toast.LENGTH_SHORT).show();
                            //videoView.start();
                        }
                    });

                    videoView.start();*/
                    PrefUtils.setCurrentVideoPath(context, JsonUtils.getData(values.get(position),"content"));
                    Intent intent = new Intent(context, VideoDialog.class);
                    context.startActivity(intent);
                }
            });

        }//}

        rowView.findViewById(R.id.tile_popup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                Point point = new Point();
                point.x = location[0];
                point.y = location[1];
                showStatusPopup(context, point, position);
            }
        });

        return rowView;
    }


    private void showStatusPopup(final Context context, Point p, final int tilePos) {

        // Inflate the popup_layout.xml
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout, null);

        // Creating the PopupWindow
        final PopupWindow changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(layout);
        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setFocusable(true);

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = -20;
        int OFFSET_Y = 50;

        //Clear the default translucent background
        changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        layout.findViewById(R.id.popup_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //removeTile(context,v.getId());
                changeStatusPopUp.dismiss();
                //changeStatusPopUp.getContentView().getParent().getParent().getParent().getParent().getParent()
                //Log.d("Delete Post Id: ", Integer.toString(v.getRootView().getRootView().getId()));
                Toast.makeText(context, "Delete Post: " + tilePos, Toast.LENGTH_SHORT).show();

                int content_id = Integer.parseInt(JsonUtils.getData(values.get(tilePos), "content_id"));
                ContentListProvider clp = new ContentListProvider(context);
                clp.open();
                clp.deleteContentById(content_id);

                values.remove(tilePos);
                notifyDataSetChanged();
            }
        });
    }

/*    public String getData(String data, String key)
    {
        JSONObject jsonObject = null;
        String ret = "";
        try
        {
            jsonObject = new JSONObject(data);
            ret = jsonObject.getString(key);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
*/

}
