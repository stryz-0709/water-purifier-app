package com.snig.waterpurifier;

import static java.lang.Math.floorDiv;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class phGraphFragment extends Fragment {
    public phGraphFragment() {
        // Required empty public constructor
    }

    View mView;
    MainActivity mainActivity;
    DatabaseReference reference;
    Button refreshButton;
    LineChart lineChart;
    WaterInfo waterInfo;
    //    XYPlot xyPlot;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_ph_graph, container, false);
        waterInfo = new WaterInfo();
        mainActivity = (MainActivity) getActivity();
        refreshButton = mView.findViewById(R.id.refreshButton);
        lineChart = mView.findViewById(R.id.ph_graph);
//        xyPlot = mView.findViewById(R.id.quality_graph);

        progressDialog = new ProgressDialog(getContext());

        reference = FirebaseDatabase.getInstance("https://water-purifier-1c9cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("water");

        long date = floorDiv(System.currentTimeMillis(),86400000L);

        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<Entry> entries2 = new ArrayList<Entry>();
        for (int i = 0; i < 50; i++){
            entries.add(new Entry(i-1, (float) mainActivity.waterInfo.pH[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "pH Level");
        dataSet.setDrawFilled(true);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.gradient_blue);
        dataSet.setFillDrawable(drawable);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(3);
        dataSet.setColor(Color.BLUE);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        Description description = new Description();
        description.setText("");
        description.setTextColor(Color.rgb(43,101,236));
        description.setTextSize(25);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.setDescription(description);
        lineChart.setDrawGridBackground(false);
        lineChart.setDragEnabled(false);
        lineChart.invalidate();

//        Description description = new Description();
//        description.setText("Quality");
//        description.setPosition(150f,15f);
//        lineChart.setDescription(description);
//        lineChart.getAxisRight().setDrawLabels(false);
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//
//        YAxis yAxis = lineChart.getAxisLeft();
//        yAxis.setAxisMinimum(0f);
//        yAxis.setAxisMaximum(100f);
//        yAxis.setAxisLineWidth(2f);
//        yAxis.setAxisLineColor(Color.BLUE);
//        yAxis.setLabelCount(10);


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
        Query query = reference.orderByChild("update_time").endAt(floorDiv(System.currentTimeMillis(),1000L)).limitToLast(200);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int c1 = 0, c2 = 0;
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        c1++;
                        if (c1 == 2) {
                            if (c2 == 100) break;
                            waterInfo.pH[c2] = ds.child("pH").getValue(Double.class);
                            c1 = 0;
                            c2++;
                        }
                    }
//                    Toast.makeText(mainActivity, String.valueOf(waterInfo.tds_1[50]), Toast.LENGTH_SHORT).show();

                }
                progressDialog.dismiss();
                ArrayList<Entry> entries = new ArrayList<Entry>();
                ArrayList<Entry> entries2 = new ArrayList<Entry>();
                for (int i = 0; i < 100; i++){
                    entries.add(new Entry(i-1, (float) waterInfo.pH[i]));
                }

                LineDataSet dataSet = new LineDataSet(entries, "pH Level");
                dataSet.setDrawFilled(true);
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.gradient_blue);
                dataSet.setFillDrawable(drawable);
                dataSet.setDrawCircles(false);
                dataSet.setLineWidth(3);
                dataSet.setColor(Color.BLUE);
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(dataSet);

                Description description = new Description();
                description.setText("");
                description.setTextColor(Color.rgb(43,101,236));
                description.setTextSize(25);

                LineData data = new LineData(dataSets);
                lineChart.setData(data);
                lineChart.setDescription(description);
                lineChart.setDrawGridBackground(false);
                lineChart.setDragEnabled(false);
                lineChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
}