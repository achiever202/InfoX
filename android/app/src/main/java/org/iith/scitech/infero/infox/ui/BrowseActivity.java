package org.iith.scitech.infero.infox.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.data.ContentListProvider;
import org.iith.scitech.infero.infox.swipetodismiss.SwipeDismissListViewTouchListener;
import org.iith.scitech.infero.infox.util.HttpServerRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Shashank Jaiswal on 17/1/15.
 */
public class BrowseActivity extends ActionBarActivity implements BrowseFragment.NavigationDrawerCallbacks
{
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private BrowseFragment mNavigationFragment;

    public static final String TILE_EDUCATION = "tile_education";

    public static final String TILE_WEATHER = "tile_weather";

    public static final String TILE_MUSIC = "tile_music";

    public static final String TILE_VIDEO = "tile_video";


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    LinearLayout.LayoutParams prms;
    ViewGroup progressViewGroup;

    PullToRefreshListView mListView;
    ListView actualListView;
    List<String> values;
    ContentListAdapter adapter;
    Boolean isRefreshing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);


        mNavigationFragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout));


        prms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        prms.weight = 1;
        prms.gravity = Gravity.CENTER_VERTICAL;

        mListView = (PullToRefreshListView) findViewById(R.id.contentListView);
        actualListView = mListView.getRefreshableView();
        registerForContextMenu(actualListView);

        values = new ArrayList<String>();

        adapter = new ContentListAdapter(BrowseActivity.this, values);
        actualListView.setAdapter(adapter);
        mListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(BrowseActivity.this, "End of List!", Toast.LENGTH_SHORT).show();
            }
        });
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                new GetNetworkDataTask().execute();
            }
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        mListView.getRefreshableView(),
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    adapter.remove(adapter.getItem(position-1));
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        mListView.getRefreshableView().setOnTouchListener(touchListener);
        mListView.getRefreshableView().setOnScrollListener(touchListener.makeScrollListener());

        mListView.getRefreshableView().setDividerHeight(0);

        addProgressBar();

        new GetLocalDataTask().execute();

    }

    public void addProgressBar()
    {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        progressViewGroup = (ViewGroup)layoutInflater.inflate(R.layout.content_progress_bar, mListView.getRefreshableView(), false);
        mListView.getRefreshableView().addHeaderView(progressViewGroup, null, false);
        isRefreshing = true;
    }

    public void removeProgressBar()
    {
        mListView.getRefreshableView().removeHeaderView(progressViewGroup);
    }


    private class GetNetworkDataTask extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... params) {
            // Perform data fetching here
            Log.v("NET", "Sending...");

            JSONObject jsonObject = null;
            try
            {
                jsonObject = new JSONObject(new HttpServerRequest(BrowseActivity.this).getReply(new String[]{"download.php"}));
            }
            catch (Exception e) {
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

            ContentListProvider clp = new ContentListProvider(BrowseActivity.this);
            clp.open();

            for(int i=0;i<jsonArray.length();i++) {
                JSONObject indObject = null;
                try {
                    indObject = (JSONObject) jsonArray.get(i);
                    clp.insertContents(indObject.getString("content_id"), indObject.getString("file_name"), indObject.getString("content"), indObject.getString("time_added"), indObject.getString("time_expiry"), indObject.getString("langId"), indObject.getString("category"), indObject.getString("tileType"));
                    if(indObject.getInt("downloadRequired")==1)
                    {
                        clp.insertDownloads(clp.getContentIdByContent(indObject.getString("content"), indObject.getString("time_added"), indObject.getString("time_expiry")), "NO", 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

                return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            // Do some UI related stuff here

            for(int i=0;i<jsonArray.length();i++) {
                JSONObject indObject = null;
                try
                {
                    indObject = (JSONObject) jsonArray.get(i);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                addTile(BrowseActivity.this, indObject);
                Toast.makeText(BrowseActivity.this, indObject.toString(), Toast.LENGTH_SHORT).show();
            }

            mListView.onRefreshComplete();
            super.onPostExecute(jsonArray);
        }
    }

    private class GetLocalDataTask extends AsyncTask<Void, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(Void... params) {
            // Perform data fetching here
            ContentListProvider clp = new ContentListProvider(BrowseActivity.this);
            clp.open();
            Cursor res = clp.getAllContents();
            res.moveToFirst();
            ArrayList arrayList = new ArrayList();

            JSONObject jsonObject = null;

            while(res.isAfterLast() == false){
                jsonObject = new JSONObject();
                try {
                    jsonObject.put("tileType", res.getString(res.getColumnIndex("content_type_id")));
                    jsonObject.put("category", res.getString(res.getColumnIndex("category_id")));
                    jsonObject.put("content", res.getString(res.getColumnIndex("file_path")));
                    jsonObject.put("downloadRequired", 0);
                    jsonObject.put("langId", res.getString(res.getColumnIndex("lang_id")));
                    jsonObject.put("file_name", res.getString(res.getColumnIndex("file_name")));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                arrayList.add(jsonObject.toString());
                //Log.v("HELLO", jsonObject.toString());
                res.moveToNext();
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            // Do some UI related stuff here
            for(int i=0;i<arrayList.size();i++) {
                JSONObject indObject = null;
                try
                {
                    indObject = new JSONObject(arrayList.get(i).toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                //Log.v("HELLO", indObject.toString());
                addTile(BrowseActivity.this, indObject);
                //Toast.makeText(BrowseActivity.this, indObject.toString(), Toast.LENGTH_SHORT).show();
            }

            if(isRefreshing) {
                removeProgressBar();
                isRefreshing = false;
            }

            mListView.onRefreshComplete();
            super.onPostExecute(arrayList);
        }
    }


    private void addTile(Context context, JSONObject result)
    {
        values.add(result.toString());
        adapter.notifyDataSetChanged();
    }





    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if(position==2)
        {
            GroupsFragment newFragment = new GroupsFragment();
            Bundle args = new Bundle();
            args.putInt(GroupsFragment.ARG_POSITION, position);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return;
        }
        else
        if(position==3)
        {
            SettingsFragment newFragment = new SettingsFragment();
            Bundle args = new Bundle();
            args.putInt(SettingsFragment.ARG_POSITION, position);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            return;
        }

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position))
                .commit();
    }





    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }




    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }










    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        static int  sectionNumber = -1;
        public static PlaceholderFragment newInstance(int secNum) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            sectionNumber = secNum;
            args.putInt(ARG_SECTION_NUMBER, secNum);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            /*if(sectionNumber==1)
                rootView = inflater.inflate(R.layout.fragment_main, container, false);
            else
            if(sectionNumber==2) {
                rootView = inflater.inflate(R.layout.fragment_groups, container, false);
                groupList = (ListView) rootView.findViewById(R.id.groups_list);
                groupList.setAdapter(groupListAdapter);
                new GetContactsTask().execute();
            }
            else
            if(sectionNumber==3)
                rootView = inflater.inflate(R.layout.fragment_settings, container, false);*/
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((BrowseActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
