package org.iith.scitech.infero.infox.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Toast;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.swipetodismiss.SwipeDismissListViewTouchListener;
import org.iith.scitech.infero.infox.widget.EducationWidget;
import org.iith.scitech.infero.infox.widget.MusicWidget;
import org.iith.scitech.infero.infox.widget.VideoWidget;
import org.iith.scitech.infero.infox.widget.WeatherWidget;

import android.net.Uri;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by shashank on 17/1/15.
 */
public class BrowseActivity extends ActionBarActivity
        implements BrowseFragment.NavigationDrawerCallbacks
{
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private BrowseFragment mBrowseFragment;

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

    LinearLayout tileViewGroup;
    View tileView;

    PullToRefreshListView mListView;
    ListView actualListView;
    List<String> values;
    ContentListAdapter adapter;
    Boolean isRefreshing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);


        mBrowseFragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mBrowseFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout));


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
                //String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                //refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                // Do work to refresh the list here.
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
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mListView.getRefreshableView().setOnScrollListener(touchListener.makeScrollListener());

        mListView.getRefreshableView().setDividerHeight(0);

        /*mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(BrowseActivity.this, values.get(position).split(";")[0], Toast.LENGTH_SHORT).show();
                switch (values.get(position).split(";")[0])
                {
                    case BrowseActivity.TILE_EDUCATION:
                        break;

                    case BrowseActivity.TILE_WEATHER:
                        break;

                    case BrowseActivity.TILE_MUSIC:
                        break;

                    case BrowseActivity.TILE_VIDEO:
                    {
                        final Dialog dialog = new Dialog(BrowseActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.content_dialog_video);
                        dialog.setCanceledOnTouchOutside(true);
                        final VideoView videoView = (VideoView) dialog.findViewById(R.id.content_tile_video_videoView);
                        videoView.setVideoPath(values.get(position).split(";")[1]);
                        MediaController mediaController = new MediaController(dialog.getContext());
                        mediaController.setAnchorView(videoView);
                        videoView.setMediaController(mediaController);
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                            //Log.i("Video", "Duration = " + videoView.getDuration());
                                Toast.makeText(dialog.getContext(), "Video prepared: Click again to play", Toast.LENGTH_SHORT).show();
                                //videoView.start();
                            }
                        });

                        videoView.start();
                        dialog.show();
                    }
                        break;
                }
            }
        });*/

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



    /*public void addProgressBar()
    {
        progressViewGroup = (LinearLayout) findViewById(R.id.contentLinearLayout);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        progressView = layoutInflater.inflate(R.layout.content_progress_bar, null);
        progressViewGroup.addView(progressView,0, prms);
    }

    public void removeProgressBar()
    {
        progressViewGroup.removeView(progressView);
    }*/

    private class GetNetworkDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Perform data fetching here
            try
            {
                Thread.sleep(4000);
            }
            catch (InterruptedException e) {
            }
            return new String[]
                    {
                            TILE_EDUCATION+";EDU;In 1879, Maxwell published a paper on the viscous stresses arising in rarefied gases. At the time, a reviewer commented that it also might be useful if Maxwell could use his theoretical findings to derive a velocity boundary condition for rarefied gas flows at solid surfaces. Consequently, in an appendix to the paper, Maxwell proposed his now-famous velocity slip boundary condition.",
                    };
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Do some UI related stuff here
            for(int i=0;i<result.length;i++) {
                addTile(BrowseActivity.this, result[i]);
            }

            mListView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }

    private class GetLocalDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
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
        protected void onPostExecute(String[] result) {
            // Do some UI related stuff here
            for(int i=0;i<result.length;i++) {
                addTile(BrowseActivity.this, result[i]);
            }

            if(isRefreshing) {
                removeProgressBar();
                isRefreshing = false;
            }

            mListView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }

    private void addTile(Context context, String result)
    {
        values.add(result);
        adapter.notifyDataSetChanged();
    }


    /*private class GetNetworkDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Perform data fetching here
            try
            {
                Thread.sleep(4000);
                /*

                String url = "url you want to download";
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription("Some descrition");
                request.setTitle("Some title");
                // in order for this if to run, you must use the android 3.2 to compile your app
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "name-of-the-file.ext");

                // get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

                * */
            //}
       /*     catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Do some UI related stuff here
            addTile(BrowseActivity.this, prms, tileCount, TILE_EDUCATION, "EDU;In 1879, Maxwell published a paper on the viscous stresses arising in rarefied gases. At the time, a reviewer commented that it also might be useful if Maxwell could use his theoretical findings to derive a velocity boundary condition for rarefied gas flows at solid surfaces. Consequently, in an appendix to the paper, Maxwell proposed his now-famous velocity slip boundary condition.");
            tileCount++;

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshScrollView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }



    private class GetLocalDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Perform data fetching here
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Do some UI related stuff here

            addTile(BrowseActivity.this, prms, tileCount, TILE_EDUCATION, "EDU;In 1879, Maxwell published a paper on the viscous stresses arising in rarefied gases. At the time, a reviewer commented that it also might be useful if Maxwell could use his theoretical findings to derive a velocity boundary condition for rarefied gas flows at solid surfaces. Consequently, in an appendix to the paper, Maxwell proposed his now-famous velocity slip boundary condition.");
            addTile(BrowseActivity.this, prms, ++tileCount, TILE_WEATHER, "24;05:00 PM;PS");
            addTile(BrowseActivity.this, prms, ++tileCount, TILE_MUSIC, "http://media.djmazadownload.com/music/320/indian_movies/Khamoshiyan%20(2015)/03%20-%20Khamoshiyan%20-%20Baatein%20Ye%20Kabhi%20Na%20(Male)%20%5BDJMaza.Info%5D.mp3");
            addTile(BrowseActivity.this, prms, ++tileCount, TILE_VIDEO, "http://www.ebookfrenzy.com/android_book/movie.mp4");
            tileCount++;

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshScrollView.onRefreshComplete();

            //removeProgressBar();

            super.onPostExecute(result);
        }
    }



    private void addTile(final Activity context, LinearLayout.LayoutParams params, int i, final String tileType, final String data)
    {
        //tileViewGroup = (LinearLayout) context.findViewById(R.id.contentLinearLayout);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (tileType)
        {
            case TILE_EDUCATION:
                tileView = layoutInflater.inflate(R.layout.content_tile_education, null);
                EducationWidget ew = new EducationWidget(tileView, BrowseActivity.this, data);
                break;

            case TILE_WEATHER:
                tileView = layoutInflater.inflate(R.layout.content_tile_weather, null);
                WeatherWidget ww = new WeatherWidget(tileView,BrowseActivity.this,data);
                break;

            case TILE_MUSIC:
                tileView = layoutInflater.inflate(R.layout.content_tile_music, null);
                MusicWidget mw = new MusicWidget(tileView, BrowseActivity.this, data);
                break;

            case TILE_VIDEO:
                tileView = layoutInflater.inflate(R.layout.content_tile_video, null);
                VideoWidget vw = new VideoWidget(tileView,BrowseActivity.this, data);
                break;

            default:
                tileView = layoutInflater.inflate(R.layout.content_tile_education, null);
                break;
        }
        tileView.findViewById(R.id.tile_popup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                Point point = new Point();
                point.x = location[0];
                point.y = location[1];
                showStatusPopup(BrowseActivity.this, point, tileType);
            }
        });

        tileViewGroup.addView(tileView, i, params);
    }


    private void removeTile(final Activity context, int i)
    {
        tileViewGroup.removeViewAt(i);
    }



    private void showStatusPopup(final Activity context, Point p, String tileType) {

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
                Log.d("Delete Post Id: ", Integer.toString(v.getRootView().getRootView().getId()));
                Toast.makeText(getApplicationContext(),"Delete Post: "+v.getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

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
        if (!mBrowseFragment.isDrawerOpen()) {
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
