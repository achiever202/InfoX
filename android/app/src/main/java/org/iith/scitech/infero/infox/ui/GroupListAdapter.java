package org.iith.scitech.infero.infox.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.util.ContactUtils;
import org.iith.scitech.infero.infox.util.PrefUtils;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by shashank on 24/1/15.
 */

public class GroupListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public GroupListAdapter(Context context, List<String> values) {
        super(context, R.layout.group_tile, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getViewTypeCount() {
        return 1; //return 4, you have two types that the getView() method will return, normal(0) and for the last row(1)
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
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
            rowView = layoutInflater.inflate(R.layout.group_tile, parent, false);

            ImageView personImage = (ImageView) rowView.findViewById(R.id.group_tile_person_img);
            TextView personName = (TextView) rowView.findViewById(R.id.group_tile_person_name);

            personName.setText(getData(s,"name"));
            personImage.setImageResource(R.drawable.person_image_empty);
        }


        rowView.findViewById(R.id.group_tile_LL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                Point point = new Point();
                point.x = location[0];
                point.y = location[1];
                //Log.v("DEB", location[0]+" "+location[1]);
                showDialogBox(context,position);
            }
        });

        return rowView;
    }

    public void showDialogBox(final Context context, final int tiltPos)
    {
        PrefUtils.setCurrentDataTransferNumber(context, getData(values.get(tiltPos),"number"));
        Intent intent = new Intent(context, DataTransferDialog.class);
        context.startActivity(intent);
    }

    public String getData(String data, String key)
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


}
