package com.snig.waterpurifier;

import static java.lang.Math.floorDiv;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    View mView;
    MainActivity mainActivity;
    Button addInfoMore, shareInfoMore, addReminderMore, graphMore;
    TextView currentDate, currentDay;
    CardView addInfoCard;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    int firstDateOfTheWeek;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (MainActivity) getActivity();

        progressDialog = new ProgressDialog(getContext());
        builder = new AlertDialog.Builder(getContext());

        addInfoMore = mView.findViewById(R.id.add_info_more);
        shareInfoMore = mView.findViewById(R.id.share_info_more);
        graphMore = mView.findViewById(R.id.graph_more);
        addReminderMore = mView.findViewById(R.id.add_reminder_more);
        currentDay = mView.findViewById(R.id.current_day);
        currentDate = mView.findViewById(R.id.current_date);
        addInfoCard = mView.findViewById(R.id.card_add_info);
        reference = FirebaseDatabase.getInstance("https://water-purifier-1c9cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("water");

        currentDay.setText(new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date()));
        currentDate.setText(new SimpleDateFormat("d MMM", Locale.getDefault()).format(new Date()));

        if (!getUserInfo().dob.equals("") &&
            !getUserInfo().gender.equals("Gender") &&
            !getUserInfo().activity.equals("Activity Type") &&
            !getUserInfo().climate.equals("Climate") &&
            getUserInfo().height !=   0) addInfoCard.setVisibility(View.GONE);

        if (mainActivity.fromEdit.equals("false")){
            mainActivity.fromEdit = "true";
            long date = floorDiv(System.currentTimeMillis()+25200000,86400000L);
            for (int i = 0; i < 7; i++){
                if (((int) date-i)% 7 == 4) {
                    firstDateOfTheWeek = i;
                    break;
                }
            }
            graphCalculation(date);

        }

        addInfoMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditInfoActivity.class);
                intent.putExtra("userInfo", new Gson().toJson(mainActivity.userInfo));
                startActivity(intent);
            }
        });

        addReminderMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_reminder);
                Fragment fragment = new RemindFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_layout, fragment);
                transaction.commit();
            }
        });

        graphMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_graph);
                Fragment fragment = new GraphFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_layout, fragment);
                transaction.commit();
            }
        });
        return mView;
    }

    public UserInfo getUserInfo(){return mainActivity.userInfo;}

    private void graphCalculation(long date) {
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Query query = reference.orderByChild("update_time").endAt(floorDiv(System.currentTimeMillis(), 1000L)).limitToLast(500);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int tol_tds_1 = 0;
                int tol_tds_2 = 0;
                int c1 = 0, c2 = 0, c3 = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        tol_tds_1 += ds.child("tds_1").getValue(Integer.class);
                        tol_tds_2 += ds.child("tds_2").getValue(Integer.class);
                        c1++;
                        if (c1 == 5) {
                            if (c2 == 100) break;
                            mainActivity.waterInfo.tds_1[c2] = tol_tds_1 / 10;
                            mainActivity.waterInfo.tds_2[c2] = tol_tds_2 / 10;
                            mainActivity.waterInfo.pH[c2] = ds.child("pH").getValue(Double.class);
                            c1 = 0;
                            c2++;
                            tol_tds_1 = 0;
                            tol_tds_2 = 0;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        query = reference.orderByChild("id").equalTo(mainActivity.userInfo.id);
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
                             ConstraintLayout constraintLayout = mView.findViewById(R.id.dialogSuccess);
                             View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_success, constraintLayout);
                             Button closeButton = view.findViewById(R.id.close_button);
                             AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                             builder.setView(view);
                             AlertDialog alertDialog = builder.create();
                             closeButton.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     alertDialog.dismiss();
                                 }
                             });
                             if (alertDialog.getWindow() != null) {
                                 alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                             }
                             alertDialog.show();
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {
                         }
                     });
                 }
             }
             @Override
             public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}