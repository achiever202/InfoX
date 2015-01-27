package org.iith.scitech.infero.infox.ui;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.util.ContactUtils;
import org.iith.scitech.infero.infox.util.PrefUtils;

/**
 * Created by shashank on 25/1/15.
 */
public class DataTransferDialog extends Activity
{
    MediaController mediaController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_dialog_data_transfer);

        TextView number = (TextView) findViewById(R.id.dataTransfer_phNo);
        number.setText(ContactUtils.getFormattedContact(PrefUtils.getCurrentDataTransferNumber(DataTransferDialog.this)));


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
