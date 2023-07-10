package com.snig.waterpurifier;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

public class GraphFragment extends Fragment {

    View mView;
    MainActivity mainActivity;
    CardView usageGraph, addInfoCard, qualityGraph, phGraph;
    Button addInfoMore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_graph, container, false);

        mainActivity = (MainActivity) getActivity();
        usageGraph = mView.findViewById(R.id.water_usage_card);
        addInfoCard = mView.findViewById(R.id.card_add_info);
        qualityGraph = mView.findViewById(R.id.water_quality_card);
        phGraph = mView.findViewById(R.id.ph_card);
        addInfoMore = mView.findViewById(R.id.add_info_more);


        if (!mainActivity.userInfo.dob.equals("") &&
            !mainActivity.userInfo.gender.equals("Gender") &&
            !mainActivity.userInfo.activity.equals("Activity Type") &&
            !mainActivity.userInfo.climate.equals("Climate") &&
            mainActivity.userInfo.height != 0 &&
            mainActivity.userInfo.weight != 0) addInfoCard.setVisibility(View.GONE);

        usageGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new UsageGraphFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_layout, fragment);
                transaction.commit();
            }
        });

        phGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new phGraphFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_layout, fragment);
                transaction.commit();
            }
        });

        qualityGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new QualityGraphFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_layout, fragment);
                transaction.commit();
            }
        });

        addInfoMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditInfoActivity.class);
                intent.putExtra("userInfo", new Gson().toJson(mainActivity.userInfo));
                startActivity(intent);
            }
        });

        return mView;
    }
}