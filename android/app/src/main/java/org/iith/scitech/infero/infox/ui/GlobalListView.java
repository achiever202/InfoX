package org.iith.scitech.infero.infox.ui;

import android.widget.ListView;

import org.iith.scitech.infero.infox.data.ContentListProvider;
import org.iith.scitech.infero.infox.ui.AllJoyn.ChatApplication;

import java.util.List;

/**
 * Created by shashank on 28/1/15.
 */
public class GlobalListView {
    public static List<String> mList;
    public static ContentListAdapter adapter;

    public GlobalListView(List<String> mList, ContentListAdapter adapter) {
        this.adapter = adapter;
        this.mList = mList;
    }


}
