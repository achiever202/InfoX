package org.iith.scitech.infero.infox.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.data.ContentListDatabase;
import org.iith.scitech.infero.infox.data.ContentListProvider;

/**
 * Created by shashank on 23/1/15.
 */
public class ContentListFrag extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTENT_LIST_LOADER = 0x01;

    private SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] uiBindFrom = { ContentListDatabase.COL_TITLE };
        int[] uiBindTo = { R.id.title };

        getLoaderManager().initLoader(CONTENT_LIST_LOADER, null, this);

        adapter = new SimpleCursorAdapter(
                getActivity().getApplicationContext(), R.layout.content_tile_weather,
                null, uiBindFrom, uiBindTo,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        setListAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { ContentListDatabase.ID, ContentListDatabase.COL_TITLE };

        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                ContentListProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
