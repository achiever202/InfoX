package org.iith.scitech.infero.infox.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.iith.scitech.infero.infox.R;
import org.iith.scitech.infero.infox.util.PrefUtils;

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
                Dialog dialog = new Dialog(getActivity());
                dialog.setTitle("Preferences");
                dialog.setContentView(R.layout.dialog_preferences);
                ListView lw = (ListView) dialog.findViewById(R.id.pref_contentPreferences_listView);
                
            }
        });




    }
}
