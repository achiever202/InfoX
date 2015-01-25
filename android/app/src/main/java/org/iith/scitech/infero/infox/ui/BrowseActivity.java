package org.iith.scitech.infero.infox.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import org.iith.scitech.infero.infox.swipetodismiss.SwipeDismissListViewTouchListener;
import org.iith.scitech.infero.infox.util.HttpServerRequest;
import org.iith.scitech.infero.infox.util.ServerRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by shashank on 17/1/15.
 */
public class BrowseActivity extends ActionBarActivity implements NavigationFragment.NavigationDrawerCallbacks
{
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationFragment mNavigationFragment;

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


        mNavigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
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


    private class GetNetworkDataTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
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

            return jsonObject;

            /*new String[]
                    {
                            TILE_EDUCATION+";EDU;In 1879, Maxwell published a paper on the viscous stresses arising in rarefied gases. At the time, a reviewer commented that it also might be useful if Maxwell could use his theoretical findings to derive a velocity boundary condition for rarefied gas flows at solid surfaces. Consequently, in an appendix to the paper, Maxwell proposed his now-famous velocity slip boundary condition."
                    };*/
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            // Do some UI related stuff here
            JSONArray jsonArray = null;
            try {
                jsonArray = (JSONArray) jsonObject.get("data");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

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
            super.onPostExecute(jsonObject);
        }
    }

    private class GetLocalDataTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            // Perform data fetching here
            Log.v("NET", "Sending...");

            JSONObject jsonObject = null;
            try
            {
                jsonObject = new JSONObject("{\"status\":200,\"data\":[{\"tileType\":\"tile_education\",\"category\":\"EDU\",\"content\":\"Ye duniya, ye duniya peetal di. Ye duniya peetal di. Baby doll mai sone di.\",\"downloadRequired\":0},{\"tileType\":\"tile_weather\",\"category\":\"PS\",\"content\":\"24;05:00 PM;23rd Jan\",\"downloadRequired\":0}]}");
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return jsonObject;

            /*new String[]
                    {
                            TILE_EDUCATION+";EDU;In 1879, Maxwell published a paper on the viscous stresses arising in rarefied gases. At the time, a reviewer commented that it also might be useful if Maxwell could use his theoretical findings to derive a velocity boundary condition for rarefied gas flows at solid surfaces. Consequently, in an appendix to the paper, Maxwell proposed his now-famous velocity slip boundary condition."
                    };*/
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            // Do some UI related stuff here
            JSONArray jsonArray = null;
            try {
                jsonArray = (JSONArray) jsonObject.get("data");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

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
                //Toast.makeText(BrowseActivity.this, indObject.toString(), Toast.LENGTH_SHORT).show();
            }

            if(isRefreshing) {
                removeProgressBar();
                isRefreshing = false;
            }

            mListView.onRefreshComplete();
            super.onPostExecute(jsonObject);
        }
    }

    /*private class GetLocalDataTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            // Perform data fetching here
            try
            {
                Thread.sleep(4000);
            }
            catch (InterruptedException e) {
            }
            return new String[]
                    {
                        TILE_WEATHER + ";24;05:00 PM;PS",
                        TILE_EDUCATION + ";EDU;In 1879, Maxwell published a paper on the viscous stresses arising in rarefied gases. At the time, a reviewer commented that it also might be useful if Maxwell could use his theoretical findings to derive a velocity boundary condition for rarefied gas flows at solid surfaces. Consequently, in an appendix to the paper, Maxwell proposed his now-famous velocity slip boundary condition.",
                        TILE_MUSIC + ";http://media.djmazadownload.com/music/320/indian_movies/Khamoshiyan%20(2015)/03%20-%20Khamoshiyan%20-%20Baatein%20Ye%20Kabhi%20Na%20(Male)%20%5BDJMaza.Info%5D.mp3",
                        TILE_VIDEO + ";http://www.ebookfrenzy.com/android_book/movie.mp4"
                    };
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // Do some UI related stuff here
            for(int i=0;i<result.length();i++) {
                addTile(BrowseActivity.this, result.get());
            }

            if(isRefreshing) {
                removeProgressBar();
                isRefreshing = false;
            }

            mListView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }*/

    private void addTile(Context context, JSONObject result)
    {
        values.add(result.toString());
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
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
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
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
            if(sectionNumber==1)
                rootView = inflater.inflate(R.layout.fragment_main, container, false);
            else
            if(sectionNumber==2)
                rootView = inflater.inflate(R.layout.fragment_main, container, false);
            else
            if(sectionNumber==3)
                rootView = inflater.inflate(R.layout.fragment_settings, container, false);
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
