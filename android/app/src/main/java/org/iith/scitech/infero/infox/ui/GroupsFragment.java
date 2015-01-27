package org.iith.scitech.infero.infox.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.iith.scitech.infero.infox.R;
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
public class GroupsFragment extends Fragment {

    private int mCurrentSelectedPosition = 0;

    PullToRefreshListView groupList;
    ListView actualListView;
    Boolean isRefreshing = false;
    GroupListAdapter groupListAdapter;
    List<String> groupListValue;
    public static final String ARG_POSITION = "section_number";
    ContactUtils cu;
    ViewGroup progressViewGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        groupListValue = new ArrayList<String>();
        groupListAdapter = new GroupListAdapter(getActivity(), groupListValue);

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
