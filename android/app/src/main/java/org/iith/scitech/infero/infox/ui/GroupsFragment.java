package org.iith.scitech.infero.infox.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.data.ContentListProvider;
import org.iith.scitech.infero.infox.ui.AllJoyn.AllJoynService;
import org.iith.scitech.infero.infox.ui.AllJoyn.ChatApplication;
import org.iith.scitech.infero.infox.ui.AllJoyn.Globals;
import org.iith.scitech.infero.infox.ui.AllJoyn.Observable;
import org.iith.scitech.infero.infox.ui.AllJoyn.Observer;
import org.iith.scitech.infero.infox.util.ContactUtils;
import org.iith.scitech.infero.infox.util.HttpServerRequest;
import org.iith.scitech.infero.infox.util.PrefUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class GroupsFragment extends Fragment implements Observer {

    private int mCurrentSelectedPosition = 0;
    public ChatApplication mChatApplication = null;

    public List<String> getFoundChannels()
    {
        return mChatApplication.getFoundChannels();
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        Log.i("DEB", "update(" + arg + ")");
        String qualifier = (String)arg;

        if (qualifier.equals(ChatApplication.APPLICATION_QUIT_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.HISTORY_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_HISTORY_CHANGED_EVENT);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!Globals.dataTransferType.equals(""))
                        Toast.makeText(getActivity(), "Data " + Globals.dataTransferType, Toast.LENGTH_SHORT).show();
                    if(Globals.dataTransferType.equals("Received")) {
                        GlobalListView.adapter.clear();
                        GlobalListView.adapter.addAll(mChatApplication.getHistory());
                        GlobalListView.adapter.notifyDataSetChanged();
                    }
                }
            });
            //GlobalListView.adapter.clear();
            //GlobalListView.adapter.addAll(mChatApplication.getHistory());
            //GlobalListView.adapter.notifyDataSetChanged();
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.HOST_CHANNEL_STATE_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_CHANNEL_STATE_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.ALLJOYN_ERROR_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
            mHandler.sendMessage(message);
        }
    }

    private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
    private static final int HANDLE_CHANNEL_STATE_CHANGED_EVENT = 1;
    private static final int HANDLE_ALLJOYN_ERROR_EVENT = 2;
    private static final int HANDLE_HISTORY_CHANGED_EVENT = 3;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_APPLICATION_QUIT_EVENT:
                {
                    Log.i("DEB", "mHandler.handleMessage(): HANDLE_APPLICATION_QUIT_EVENT");
                    //finish();
                }
                break;
                case HANDLE_CHANNEL_STATE_CHANGED_EVENT:
                {
                    Log.i("DEB", "mHandler.handleMessage(): HANDLE_CHANNEL_STATE_CHANGED_EVENT");
                    updateChannelState();
                }
                break;
                case HANDLE_ALLJOYN_ERROR_EVENT:
                {
                    Log.i("DEB", "mHandler.handleMessage(): HANDLE_ALLJOYN_ERROR_EVENT");
                    alljoynError();
                }
                break;
                default:
                    break;
            }
        }
    };

    public static final int DIALOG_ALLJOYN_ERROR_ID = 3;

    private void alljoynError() {
        if (mChatApplication.getErrorModule() == ChatApplication.Module.GENERAL ||
                mChatApplication.getErrorModule() == ChatApplication.Module.USE) {
            Toast.makeText(getActivity(),DIALOG_ALLJOYN_ERROR_ID+"", Toast.LENGTH_SHORT).show();
        }
    }



    private void updateChannelState() {
        AllJoynService.HostChannelState channelState = mChatApplication.hostGetChannelState();
        String name = mChatApplication.hostGetChannelName();
        boolean haveName = true;
        if (name == null) {
            haveName = false;
            name = "Not set";
        }
        Log.v("Channel Name", name);
        switch (channelState) {
            case IDLE:
                Log.v("Channel Status", "Idle");
                break;
            case NAMED:
                Log.v("Channel Status","Named");
                break;
            case BOUND:
                Log.v("Channel Status","Bound");
                break;
            case ADVERTISED:
                Log.v("Channel Status","Advertised");
                break;
            case CONNECTED:
                Log.v("Channel Status","Connected");
                break;
            default:
                Log.v("Channel Status","Unknown");
                break;
        }

        /*

        if (channelState == AllJoynService.HostChannelState.IDLE) {
            mSetNameButton.setEnabled(true);
            if (haveName) {
                mStartButton.setEnabled(true);
            } else {
                mStartButton.setEnabled(false);
            }
            mStopButton.setEnabled(false);
        } else {
            mSetNameButton.setEnabled(false);
            mStartButton.setEnabled(false);
            mStopButton.setEnabled(true);
        }*/
    }



    PullToRefreshListView groupList;
    ListView actualListView;
    Boolean isRefreshing = false;
    GroupListAdapter groupListAdapter;
    List<String> groupListValue;
    public static final String ARG_POSITION = "section_number";
    ContactUtils cu;
    ViewGroup progressViewGroup;

    private ArrayAdapter<String> mHistoryList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    private void updateHistory() {
        Log.i("DEB", "updateHistory()");
        mHistoryList.clear();
        List<String> messages = mChatApplication.getHistory();
        for (String message : messages) {
            mHistoryList.add(message);
        }
        mHistoryList.notifyDataSetChanged();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);

        mChatApplication = (ChatApplication) getActivity().getApplication();
        mChatApplication.checkin();
        updateChannelState();
        //updateHistory();
        mChatApplication.addObserver(this);




        groupListValue = new ArrayList<String>();
        groupListAdapter = new GroupListAdapter(getActivity(), groupListValue, mChatApplication);

        groupList = (PullToRefreshListView) getActivity().findViewById(R.id.groups_list_view);
        groupList.getRefreshableView().setDividerHeight(0);
        addProgressBar();
        actualListView = groupList.getRefreshableView();
        registerForContextMenu(actualListView);
        actualListView.setAdapter(groupListAdapter);

        cu = new ContactUtils(getActivity().getContentResolver());
        new SetContactsTask().execute();


        groupList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                groupList.setRefreshing();
                new GetContactsTask().execute();
            }
        });

        //groupList.getRefreshableView().setClickable(false);
        //groupList.setClickable(false);
        //
        // groupList.getRefreshableView().setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        //groupList.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        /*groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("DEB", position+"");
                Toast.makeText(getActivity(), position, Toast.LENGTH_SHORT).show();
            }
        });*/

        actualListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("DEB", position+"");
                Toast.makeText(getActivity(), position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        /*groupList.getRefreshableView().setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("DEB", position+"");
                Toast.makeText(getActivity(), position, Toast.LENGTH_SHORT).show();
            }
        });*/

        // Inflate the layout for this fragment
        Log.v("Contact", ContactUtils.getAlphabetContact(ContactUtils.getFormattedContact(PrefUtils.getPhoneNumber(getActivity()))));
        mChatApplication.hostSetChannelName(ContactUtils.getAlphabetContact(ContactUtils.getFormattedContact(PrefUtils.getPhoneNumber(getActivity()))));
        Log.v("DEB", mChatApplication.hostGetChannelName());
        mChatApplication.hostInitChannel();
        mChatApplication.hostStartChannel();
        Log.v("DEB: Channel State:: ", mChatApplication.hostGetChannelState().toString());

        Globals glb = new Globals(mChatApplication);
    }



    public void addProgressBar()
    {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        progressViewGroup = (ViewGroup)layoutInflater.inflate(R.layout.content_progress_bar, groupList.getRefreshableView(), false);
        groupList.getRefreshableView().addHeaderView(progressViewGroup, null, false);
        isRefreshing = true;
    }

    public void removeProgressBar()
    {
        groupList.getRefreshableView().removeHeaderView(progressViewGroup);
    }



    private class SetContactsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String[] contactNumAndName = cu.getAllContactNumbersAndName();
            for(int i=0;i<contactNumAndName.length;i++)
            {
                groupListValue.add(contactNumAndName[i]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            groupListAdapter.notifyDataSetChanged();

            if(isRefreshing) {
                removeProgressBar();
                isRefreshing = false;
            }
            groupList.onRefreshComplete();
            super.onPostExecute(result);
        }
    }

    private class GetContactsTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {
            // Perform data fetching here
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                String[] contactNumAndName = cu.getAllContactNumbersAndName();
                JSONArray jsonArray = new JSONArray();
                for(int i=0; i<contactNumAndName.length;i++) {
                    jsonArray.put(new JSONObject(contactNumAndName[i]));
                }
                jsonObject.put("status", "200");
                jsonObject.put("phNo", PrefUtils.getPhoneNumber(getActivity()));
                jsonObject.put("data", jsonArray);
                jsonObject = new JSONObject(new HttpServerRequest(getActivity()).getReply(new String[]{"queryContacts.php", "data", jsonObject.toString()}));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            JSONArray jsonArray = null;
            try {
                jsonArray = (JSONArray) jsonObject.get("data");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            groupListValue = new ArrayList<String>();


            for(int i=0;i<jsonArray.length();i++) {
                String indObject = null;
                try {
                    indObject = jsonArray.get(i).toString();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                groupListValue.add(indObject);
            }
            return groupListValue;
        }

        @Override
        protected void onPostExecute(List<String> arrayList) {
            groupListAdapter.notifyDataSetChanged();
            groupList.onRefreshComplete();
            super.onPostExecute(arrayList);
        }
    }



}
