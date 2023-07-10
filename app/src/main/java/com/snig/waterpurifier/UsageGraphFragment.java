package com.snig.waterpurifier;

import static java.lang.Math.floorDiv;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UsageGraphFragment extends Fragment {
    public UsageGraphFragment() {
        // Required empty public constructor
    }

    View mView;
    MainActivity mainActivity;
    TextView todayGoal, todayCurrent;
    DatabaseReference reference;
    Button refreshButton;
    ProgressDialog progressDialog;
    RelativeLayout todayProgress;
    RelativeLayout mon, tue, wed, thu, fri, sat, sun;
    int firstDateOfTheWeek;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_usage_graph, container, false);

        reference = FirebaseDatabase.getInstance("https://water-purifier-1c9cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("water");
        long date = floorDiv(System.currentTimeMillis()+25200000,86400000L);

        mainActivity = (MainActivity) getActivity();
        todayGoal = mView.findViewById(R.id.today_goal_text);
        todayCurrent = mView.findViewById(R.id.today_current_text);
        refreshButton = mView.findViewById(R.id.refreshButton);
        todayProgress = mView.findViewById(R.id.waveAmount);
        mon = mView.findViewById(R.id.height_mon);
        tue = mView.findViewById(R.id.height_tue);
        wed = mView.findViewById(R.id.height_wed);
        thu = mView.findViewById(R.id.height_thu);
        fri = mView.findViewById(R.id.height_fri);
        sat = mView.findViewById(R.id.height_sat);
        sun = mView.findViewById(R.id.height_sun);

        for (int i = 0; i < 7; i++){
            if (((int) date-i)% 7 == 4) {
                firstDateOfTheWeek = i;
                break;
            }
        }

        progressDialog = new ProgressDialog(getContext());

        todayGoal.setText(String.valueOf(mainActivity.userInfo.dailyGoal));
        todayCurrent.setText(String.valueOf(mainActivity.userInfo.currentGoal));
        float percentage = ((float) mainActivity.userInfo.currentGoal/(float) mainActivity.userInfo.dailyGoal)*100f;
        float progress = 9.6f*percentage+50f;
        if (progress < 50) progress = 50;
        else if (progress > 1100) progress = 1100;
        int [] allProgresses = {0,0,0,0,0,0,0};
        for (int i = 0; i < 7; i++){
            float pastPercentage = ((float) (float) mainActivity.waterInfo.water_usage[i]/(float) mainActivity.userInfo.dailyGoal)*100f;
            float pastProgress = 2.25f*pastPercentage+10f;
            if (pastProgress < 10) pastProgress = 10;
            else if (pastProgress > 235) pastProgress = 235;
            allProgresses[i] = (int) pastProgress;
        }
        todayProgress.getLayoutParams().height = (int) progress;
        todayProgress.requestLayout();
        mon.getLayoutParams().height = allProgresses[4];
        tue.getLayoutParams().height = allProgresses[5];
        wed.getLayoutParams().height = allProgresses[6];
        thu.getLayoutParams().height = allProgresses[0];
        fri.getLayoutParams().height = allProgresses[1];
        sat.getLayoutParams().height = allProgresses[2];
        sun.getLayoutParams().height = allProgresses[3];
        mon.requestLayout();tue.requestLayout();wed.requestLayout();thu.requestLayout();
        fri.requestLayout();sat.requestLayout();sun.requestLayout();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graphCalculation(date);
            }
        });

        return mView;
    }

    private void graphCalculation(long date){
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Query query = reference.orderByChild("id").equalTo(mainActivity.userInfo.id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (int i = 1; i <= firstDateOfTheWeek; i++) {
                        long para = date - i;
                        Query someOtherQuery = reference.orderByChild("update_time").startAt((para) * 86400).endBefore((para + 1) * 86400);
                        someOtherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot newSnapshot) {
                                int flow = 0;
                                int max = 0;
                                if (newSnapshot.exists()) {
                                    for (DataSnapshot ds : newSnapshot.getChildren()) {
                                        if (ds.child("flow").getValue(Integer.class) == 0) {
                                            flow += max;
                                            max = 0;
                                        } else
                                            max = ds.child("flow").getValue(Integer.class);
                                    }
                                    flow += max;
                                    //                                 Toast.makeText(mainActivity, String.valueOf(flow), Toast.LENGTH_SHORT).show();
                                    mainActivity.waterInfo.water_usage[((int) para) % 7] = flow;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                    Query newQuery = reference.orderByChild("update_time").startAt(date*86400).endBefore((date+1)*86400);
                    newQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot newSnapshot) {
                            int flow = 0;
                            int max = 0;
                            if (newSnapshot.exists()) {
                                for (DataSnapshot ds : newSnapshot.getChildren()) {
                                    if (ds.child("flow").getValue(Integer.class) == 0) {
                                        flow += max;
                                        max = 0;
                                    } else
                                        max = ds.child("flow").getValue(Integer.class);
                                }
                                flow += max;
                                mainActivity.userInfo.currentGoal = flow;
                                mainActivity.waterInfo.water_usage[((int) date) % 7] = flow;
                            }
                            progressDialog.dismiss();
                            todayGoal.setText(String.valueOf(mainActivity.userInfo.dailyGoal));
                            todayCurrent.setText(String.valueOf(mainActivity.userInfo.currentGoal));
                            float percentage = ((float) mainActivity.userInfo.currentGoal/(float) mainActivity.userInfo.dailyGoal)*100f;
                            float progress = 9.6f*percentage+50f;
                            if (progress < 50) progress = 50;
                            else if (progress > 1100) progress = 1100;
                            int [] allProgresses = {0,0,0,0,0,0,0};
                            for (int i = 0; i < 7; i++){
                                float pastPercentage = ((float) (float) mainActivity.waterInfo.water_usage[i]/(float) mainActivity.userInfo.dailyGoal)*100f;
                                float pastProgress = 2.25f*pastPercentage+10f;
                                if (pastProgress < 10) pastProgress = 10;
                                else if (pastProgress > 235) pastProgress = 235;
                                allProgresses[i] = (int) pastProgress;
                            }
                            todayProgress.getLayoutParams().height = (int) progress;
                            todayProgress.requestLayout();
                            mon.getLayoutParams().height = allProgresses[4];
                            tue.getLayoutParams().height = allProgresses[5];
                            wed.getLayoutParams().height = allProgresses[6];
                            thu.getLayoutParams().height = allProgresses[0];
                            fri.getLayoutParams().height = allProgresses[1];
                            sat.getLayoutParams().height = allProgresses[2];
                            sun.getLayoutParams().height = allProgresses[3];
                            mon.requestLayout();tue.requestLayout();wed.requestLayout();thu.requestLayout();
                            fri.requestLayout();sat.requestLayout();sun.requestLayout();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


    }
}