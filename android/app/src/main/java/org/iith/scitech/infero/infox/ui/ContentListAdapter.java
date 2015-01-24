package org.iith.scitech.infero.infox.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
                values.remove(tilePos);
                notifyDataSetChanged();
            }
        });
    }
}
