package com.snig.waterpurifier;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Calendar;

public class EditInfoActivity extends AppCompatActivity {
    AutoCompleteTextView activitiesDropDown, climatesDropDown, genderDropDown;
    Button saveButton;
    UserInfo userInfo;
    EditText editWeight, editHeight;
    TextView editDOB;
    DatabaseReference reference;
    DatePickerDialog.OnDateSetListener setListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);


        userInfo = new Gson().fromJson(getIntent().getExtras().getString("userInfo"), UserInfo.class);

        reference = FirebaseDatabase.getInstance("https://water-purifier-1c9cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");

        String [] activities = getResources().getStringArray(R.array.activities);
        String [] climates = getResources().getStringArray(R.array.climates);
        String [] gender = getResources().getStringArray(R.array.gender);
        ArrayAdapter<String> activitiesArray = new ArrayAdapter(getApplicationContext(), R.layout.dropdown_activities, activities);
        ArrayAdapter<String> climatesArray = new ArrayAdapter(getApplicationContext(), R.layout.dropdown_activities, climates);
        ArrayAdapter<String> genderArray = new ArrayAdapter(getApplicationContext(), R.layout.dropdown_activities, gender);
        activitiesDropDown = findViewById(R.id.editActivityType);
        activitiesDropDown.setAdapter(activitiesArray);

        climatesDropDown = findViewById(R.id.editClimateType);
        climatesDropDown.setAdapter(climatesArray);

        genderDropDown = findViewById(R.id.editGender);
        genderDropDown.setAdapter(genderArray);

        editDOB = findViewById(R.id.editDOB);
        editHeight = findViewById(R.id.editHeight);
        editWeight = findViewById(R.id.editWeight);
        saveButton = findViewById(R.id.saveButton);

        editDOB.setText(userInfo.dob);

//        if (!userInfo.getGender().equals("")) genderDropDown.setText(userInfo.getGender());
//        if (!userInfo.getActivity().equals("")) activitiesDropDown.setText(userInfo.getActivity());
//        if (!userInfo.getClimate().equals("")) climatesDropDown.setText(userInfo.getClimate());
        if (!userInfo.dob.equals("")) editDOB.setText(userInfo.dob);
        if (userInfo.weight != 0) editWeight.setText(String.valueOf(userInfo.weight));
        if (userInfo.height != 0) editHeight.setText(String.valueOf(userInfo.height));


        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        editDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditInfoActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String dayStr, monthStr;
                if (day < 10) dayStr = "0" + day;
                else dayStr = "" + day;
                if (month < 10) monthStr = "0" + month;
                else monthStr = "" + month;
                String date = year + "/" + monthStr + "/" + dayStr;
                editDOB.setText(date);
            }
        };

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checkActivity = isActivityChanged();
                boolean checkClimate = isClimateChanged();
                boolean checkGender = isGenderChanged();
                boolean checkDOB = isDOBChanged();
                boolean checkHeight = isHeightChanged();
                boolean checkWeight = isWeightChanged();
                if (checkActivity || checkClimate || checkGender || checkDOB || checkHeight || checkWeight){
                    calculateGoal();
                }
                else Toast.makeText(EditInfoActivity.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditInfoActivity.this, MainActivity.class);
                intent.putExtra("userInfo", new Gson().toJson(userInfo));
                intent.putExtra("fromEdit", "true");
                startActivity(intent);
            }
        });
    }

    private boolean isActivityChanged() {
        if (!getString(activitiesDropDown).equals("Activity Type") && !userInfo.activity.equals(getString(activitiesDropDown))){
            userInfo.activity = getString(activitiesDropDown);
            reference.child(String.valueOf(userInfo.id)).child("activity").setValue(userInfo.activity);
            return true;
        }
        else return false;
    }

    private boolean isClimateChanged() {
        if (!getString(climatesDropDown).equals("Climate") && !userInfo.climate.equals(getString(climatesDropDown))){
            userInfo.climate = getString(climatesDropDown);
            reference.child(String.valueOf(userInfo.id)).child("climate").setValue(userInfo.climate);
            return true;
        }
        else return false;
    }
    private boolean isGenderChanged() {
        if (!getString(genderDropDown).equals("Gender") && !userInfo.gender.equals(getString(genderDropDown))){
            userInfo.gender = getString(genderDropDown);
            reference.child(String.valueOf(userInfo.id)).child("gender").setValue(userInfo.gender);
            return true;
        }
        else return false;
    }
    private boolean isDOBChanged() {
        if (!getString(editDOB).equals("") && !userInfo.dob.equals(getString(editDOB))){
            userInfo.dob = getString(editDOB);
            reference.child(String.valueOf(userInfo.id)).child("dob").setValue(userInfo.dob);
            return true;
        }
        else return false;
    }
    private boolean isHeightChanged() {
        if (!isBlank(editHeight) && !String.valueOf(userInfo.height).equals(getString(editHeight))){
            userInfo.height = Integer.parseInt(getString(editHeight));
            reference.child(String.valueOf(userInfo.id)).child("height").setValue(userInfo.height);
            return true;
        }
        else return false;
    }

    private boolean isWeightChanged() {
        if (!isBlank(editHeight) && !String.valueOf(userInfo.weight).equals(getString(editWeight))){
            userInfo.weight = Integer.parseInt(getString(editWeight));
            reference.child(String.valueOf(userInfo.id)).child("weight").setValue(userInfo.weight);
            return true;
        }
        else return false;
    }

    public String getString(EditText editText) {return editText.getText().toString();}
    public String getString(TextView textView) {return textView.getText().toString();}
    public String getString(AutoCompleteTextView autoCompleteTextView) {return autoCompleteTextView.getText().toString();}

    public Boolean isBlank(EditText editText){return getString(editText).equals("");}

    public void calculateGoal(){
        int activityVal = 0, climateVal = 0;
        if (!userInfo.activity.equals("Activity Type") && !userInfo.climate.equals("Climate") && userInfo.weight != 0) {
            switch (userInfo.activity) {
                case "Sedentary/Light Activity":
                    activityVal = 250;
                    break;
                case "Moderately Active":
                    activityVal = 313;
                    break;
                case "Active":
                    activityVal = 375;
                    break;
                case "Very Active":
                    activityVal = 438;
                    break;
                case "Pregnant/Breastfeeding":
                    activityVal = 500;
                    break;
            }
            switch (userInfo.climate) {
                case "Tropical":
                    climateVal = 900;
                    break;
                case "Temperate":
                    climateVal = 600;
                    break;
                case "Cold":
                    climateVal = 300;
                    break;
            }

            userInfo.dailyGoal = userInfo.weight * 30 + activityVal + climateVal;
            reference.child(String.valueOf(userInfo.id)).child("dailyGoal").setValue(userInfo.dailyGoal);
        }
    }
}