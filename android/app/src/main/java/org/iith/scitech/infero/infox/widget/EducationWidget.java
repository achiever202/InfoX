package org.iith.scitech.infero.infox.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.iith.scitech.infero.infox.R;

/**
 * Created by shashank on 23/1/15.
 */
public class EducationWidget
{
    private View tileView;
    private Context context;

    private ImageView educationTypeI;
    private TextView educationTypeT;
    private TextView educationText;


    public EducationWidget(View tileView, Context context, String dataSource)
    {
        this.tileView = tileView;
        this.context = context;

        initializeTileViews();
        setDataSource(dataSource);
    }

    private void initializeTileViews()
    {
        educationTypeI = (ImageView) tileView.findViewById(R.id.content_tile_education_img);
        educationTypeT = (TextView) tileView.findViewById(R.id.content_tile_education_type);
        educationText = (TextView) tileView.findViewById(R.id.content_tile_education_text);
    }


    private void setDataSource(String dataSource)
    {
        educationText.setText(dataSource.split(";")[1]);

        switch (dataSource.split(";")[0])
        {
            case "EDU":
                educationTypeI.setImageResource(R.drawable.ic_education);
                educationTypeT.setText("EDUCATION");
                break;

        }
    }

}
