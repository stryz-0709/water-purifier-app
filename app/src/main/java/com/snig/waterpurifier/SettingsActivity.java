package com.snig.waterpurifier;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.snig.waterpurifier.databinding.ActivitySettingsBinding;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    SettingsListAdapter settingsListAdapter;
    ArrayList<SettingsListData> dataArrayList = new ArrayList<>();
    SettingsListData settingsListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int[] imageList = {};
        String[] textList = {};

        for (int i = 0; i < imageList.length; i++){
            settingsListData = new SettingsListData(textList[i], imageList[i]);
            dataArrayList.add(settingsListData);
        }

        settingsListAdapter = new SettingsListAdapter(SettingsActivity.this, dataArrayList);
        binding.settingsListView.setAdapter(settingsListAdapter);
        binding.settingsListView.setClickable(true);

        binding.settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }
}