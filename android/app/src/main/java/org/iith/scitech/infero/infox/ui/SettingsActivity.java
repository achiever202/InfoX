package org.iith.scitech.infero.infox.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import org.iith.scitech.infero.infox.R;

/**
 * Created by shashank on 17/1/15.
 */
public class SettingsActivity extends ActionBarActivity
        implements BrowseFragment.NavigationDrawerCallbacks
{
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private SettingsFragment mSettingsFragment;

    //static int i = 0;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);



    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, BrowseActivity.PlaceholderFragment.newInstance(position + 1))
                .commit();
    }
}
