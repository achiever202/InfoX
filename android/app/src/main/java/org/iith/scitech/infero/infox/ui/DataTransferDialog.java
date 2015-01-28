package org.iith.scitech.infero.infox.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.data.ContentListProvider;
import org.iith.scitech.infero.infox.swipetodismiss.SwipeDismissListViewTouchListener;
import org.iith.scitech.infero.infox.ui.AllJoyn.ChatApplication;
import org.iith.scitech.infero.infox.ui.AllJoyn.Globals;
import org.iith.scitech.infero.infox.util.ContactUtils;
import org.iith.scitech.infero.infox.util.JsonUtils;
import org.iith.scitech.infero.infox.util.PrefUtils;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashank on 25/1/15.
 */
public class DataTransferDialog extends Activity
{
    ViewGroup logViewGroup;

    ListView mListView;
    List<String> values;
    ContentListAdapter adapter;
    Boolean isRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_dialog_data_transfer);


        getActionBar().setTitle(PrefUtils.getCurrentDataTransferName(DataTransferDialog.this));

        mListView = (ListView) findViewById(R.id.dataTransfer_ListView);
        registerForContextMenu(mListView);

        values = new ArrayList<String>();

        adapter = new ContentListAdapter(DataTransferDialog.this, values);
        mListView.setAdapter(adapter);


        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        mListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    if(PrefUtils.canSwipeToDelete(DataTransferDialog.this))
                                    {
                                        int content_id = Integer.parseInt(JsonUtils.getData(adapter.getItem(position-1), "content_id"));
                                        //Log.v("DEB", jsonObject.toString());
                                        ContentListProvider clp = new ContentListProvider(DataTransferDialog.this);
                                        clp.open();
                                        clp.deleteContentById(content_id);
                                    }
                                    adapter.remove(adapter.getItem(position-1));
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        mListView.setOnTouchListener(touchListener);
        mListView.setOnScrollListener(touchListener.makeScrollListener());

        mListView.setDividerHeight(0);

        addLogView();


        Button receive = (Button) findViewById(R.id.dataTransfer_receive);
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

    }

    public void addLogView()
    {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        logViewGroup = (ViewGroup)layoutInflater.inflate(R.layout.data_transfer_log, mListView, false);
        mListView.addHeaderView(logViewGroup, null, false);
        isRefreshing = true;
    }

    public void removeLogView()
    {
        mListView.removeHeaderView(logViewGroup);
    }



        //TextView number = (TextView) findViewById(R.id.dataTransfer_phNo);
        //number.setText(ContactUtils.getFormattedContact(PrefUtils.getCurrentDataTransferNumber(DataTransferDialog.this)));

        //ChatApplication mChatApplication = (ChatApplication)getIntent().getSerializableExtra("mChatApplicat");

        /*List<String> foundChannels = Globals.mChatApplication.getFoundChannels();
        for(int i=0;i< foundChannels.size();i++)
        {
            Log.v("Found Channels(): ", foundChannels.get(i));
        }*/
        //Serializable mChatApplication = getIntent().getSerializableExtra("mChatApplication");


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
