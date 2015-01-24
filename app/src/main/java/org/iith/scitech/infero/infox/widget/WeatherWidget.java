package org.iith.scitech.infero.infox.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.iith.scitech.infero.infox.R;

/**
 * Created by shashank on 23/1/15.
 */
public class WeatherWidget
{
    private View tileView;
    private Context context;

    private ImageView weatherTypeI;
    private TextView weatherTemp;
    private TextView weatherTypeT;
    private TextView weatherTime;


    public WeatherWidget(View tileView, Context context, String dataSource)
    {
        this.tileView = tileView;
        this.context = context;

        initializeTileViews();
        setDataSource(dataSource);
    }

    private void initializeTileViews()
    {
        weatherTypeI = (ImageView) tileView.findViewById(R.id.img_content_type);
        weatherTemp = (TextView) tileView.findViewById(R.id.content_tile_weather_temp);
        weatherTypeT = (TextView) tileView.findViewById(R.id.content_tile_weather_type);
        weatherTime = (TextView) tileView.findViewById(R.id.content_tile_weather_time);
    }


    private void setDataSource(String dataSource)
    {
        weatherTemp.setText(dataSource.split(";")[0]+" ºC");
        weatherTime.setText(dataSource.split(";")[1]);

        switch (dataSource.split(";")[2])
        {
            case "PS":
                weatherTypeI.setImageResource(R.drawable.ic_weather_02);
                weatherTypeT.setText("Partly Sunny");
                break;

        }
    }

}
