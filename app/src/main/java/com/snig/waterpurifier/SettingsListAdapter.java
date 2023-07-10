package com.snig.waterpurifier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SettingsListAdapter extends ArrayAdapter<SettingsListData> {
    public SettingsListAdapter(@NonNull Context context, ArrayList<SettingsListData> dataArrayList) {
        super(context, R.layout.setting_items, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        SettingsListData settingsListData = getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.setting_items,parent,false);
        }

        ImageView settingsListImage = view.findViewById(R.id.settings_list_image);
        TextView settingsListText = view.findViewById(R.id.settings_list_text);

        settingsListImage.setImageResource(settingsListData.image);
        settingsListText.setText(settingsListData.text);

        return view;
    }
}
