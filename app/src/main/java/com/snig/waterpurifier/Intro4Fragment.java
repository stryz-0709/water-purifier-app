package com.snig.waterpurifier;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

public class Intro4Fragment extends Fragment {
    private Button btnEndIntro;
    private View mView;

    IntroActivity onboardingActivity;
    public Intro4Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        onboardingActivity = (IntroActivity) getActivity();
        mView = inflater.inflate(R.layout.fragment_intro4, container, false);
        btnEndIntro = mView.findViewById(R.id.end_intro);
        btnEndIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("userInfo", new Gson().toJson(onboardingActivity.getUserInfo()));
                intent.putExtra("fromEdit", "false");
                getActivity().startActivity(intent);
            }
        });


        return mView;
    }
}