package org.iith.scitech.infero.infox.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.iith.scitech.infero.infox.R;
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
public class SettingsFragment extends Fragment {

    public static final String ARG_POSITION = "section_number";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CheckBox swipeToDelete = (CheckBox) getActivity().findViewById(R.id.pref_swipeToDelete);
        swipeToDelete.setChecked(PrefUtils.canSwipeToDelete(getActivity()));
        swipeToDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    PrefUtils.setSwipeToDelete(getActivity(), true);
                else
                    PrefUtils.setSwipeToDelete(getActivity(), false);
            }
        });

        CheckBox autoSync = (CheckBox) getActivity().findViewById(R.id.pref_autoSync);
        autoSync.setChecked(PrefUtils.canAutoSync(getActivity()));
        autoSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    PrefUtils.setAutoSync(getActivity(), true);
                else
                    PrefUtils.setAutoSync(getActivity(), false);
            }
        });

        final EditText serverAddress = (EditText) getActivity().findViewById(R.id.pref_serverAddress);
        serverAddress.setText(PrefUtils.getServerIP(getActivity()));
        serverAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    PrefUtils.setServerIP(getActivity(), serverAddress.getText().toString());
            }
        });

        final TextView prefTextView = (TextView) getActivity().findViewById(R.id.pref_contentPreferences);
        prefTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Dialog dialog = new Dialog(getActivity());
                dialog.setTitle("Preferences");
                dialog.setContentView(R.layout.dialog_preferences);
                ListView lw = (ListView) dialog.findViewById(R.id.pref_contentPreferences_listView);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.pref_contentTypes_entries, android.R.layout.simple_list_item_multiple_choice);
                lw.setAdapter(adapter);
                dialog.show();*/

                final List<Integer> mSelectedItems = new ArrayList();  // Where we track the selected items
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Preferences")
                        // Specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive callbacks when items are selected
                        .setMultiChoiceItems(R.array.pref_contentTypes_entries, null,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which,
                                                        boolean isChecked) {
                                        if (isChecked) {
                                            // If the user checked the item, add it to the selected items
                                            mSelectedItems.add(which);
                                        } else if (mSelectedItems.contains(which)) {
                                            // Else, if the item is already in the array, remove it
                                            mSelectedItems.remove(Integer.valueOf(which));
                                        }
                                    }
                                })
                                // Set the action buttons
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK, so save the mSelectedItems results somewhere
                                // or return them to the component that opened the dialog
                                JSONArray jsonArray = new JSONArray();
                                for (int i = 0; i < mSelectedItems.size(); i++) {
                                    jsonArray.put(getResources().getStringArray(R.array.pref_contentTypes_values)[mSelectedItems.get(i)]);
                                }
                                PrefUtils.setContentPreferences(getActivity(), jsonArray.toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                builder.create();
                builder.show();
            }
        });




    }
}
