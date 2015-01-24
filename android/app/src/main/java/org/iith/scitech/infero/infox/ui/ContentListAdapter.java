package org.iith.scitech.infero.infox.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.widget.EducationWidget;
import org.iith.scitech.infero.infox.widget.MusicWidget;
import org.iith.scitech.infero.infox.widget.VideoWidget;
import org.iith.scitech.infero.infox.widget.WeatherWidget;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shashank on 24/1/15.
 */

public class ContentListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

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
        switch (values.get(position).split(";")[0])
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String s = values.get(position);
        //Log.v("DEB","value: "+s);
        View rowView = convertView;

        int theType = getItemViewType(position);

        if (theType==0)
        {
            rowView = layoutInflater.inflate(R.layout.content_tile_education, parent, false);

            ImageView educationTypeI = (ImageView) rowView.findViewById(R.id.content_tile_education_img);
            TextView educationTypeT = (TextView) rowView.findViewById(R.id.content_tile_education_type);
            TextView educationText = (TextView) rowView.findViewById(R.id.content_tile_education_text);

            educationText.setText(s.split(";")[2]);

            switch (s.split(";")[1])
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

            weatherTemp.setText(s.split(";")[1]+" ºC");
            weatherTime.setText(s.split(";")[2]);

            switch (s.split(";")[3])
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
        }

        else if (theType==3)
        {
            rowView = layoutInflater.inflate(R.layout.content_tile_video, parent, false);
        }//}

        return rowView;
    }
}
