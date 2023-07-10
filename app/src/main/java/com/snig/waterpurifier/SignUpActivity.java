package com.snig.waterpurifier;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class SignUpActivity extends AppCompatActivity {
    /////OnScreen
    EditText signupName, signupUsername, signupEmail, signupPassword, signupRetypePassword;
    TextView loginRedirectText;
    Button signupButton;

    /////USERINFO
    UserInfo userInfo;
    long maxid = 0;


    /////ProgressDialog&AlertDialog
    ProgressDialog dialog;
    AlertDialog.Builder builder;

    ////MQTT
    MqttClient client;

    /////FIREBASE
    DatabaseReference reference;

    Boolean bool;

    /////POSTGRES
    String JDBC_DRIVER = "org.postgresql.Driver";
    String DB_URL = "jdbc:postgresql://apps-postgresql-single-303dca0d-f929-4a7f-b48e-f096b149ff28-pub.education.wise-paas.com:5432/3909196f-74bb-4325-8a0e-a6f70a22c691";

    //  Database credentials
    String USER = "32ca54d0-2eee-4dfe-bbff-eb690084356f";
    String PASS = "t2hUx2xPp2CZWBTigPC8RzW2a";


    String message = null;
    String conmsg = null;

    Connection conn;
    Statement st;
    ResultSet rs;
    ResultSetMetaData rsmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /////Start up
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        /////Set new user
        userInfo = new UserInfo();


        /////MQTT
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient("tcp://rabbitmq-pub.education.wise-paas.com:1883", "4f1718f2-742e-4bdf-a9f2-54eb9212736a", persistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setUserName("EoqrxoPnEPQv:8xQnpnbJOMqD");
            connectOptions.setPassword("4duJ8w9xlyD8xDemE9uN".toCharArray());
            connectOptions.setCleanSession(true);
            client.connect(connectOptions);
        } catch (MqttException e) {
            Toast.makeText(SignUpActivity.this, "no signal", Toast.LENGTH_SHORT).show();
            Log.d("MQTT", "EXCEPTION FAILURE");
            e.printStackTrace();
        }

        /////ProgressDialog&AlertDialog
        dialog = new ProgressDialog(this);
        builder = new AlertDialog.Builder(this);

        /////OnScreen
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupRetypePassword = findViewById(R.id.signup_retype_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        /////FIREBASE
        reference = FirebaseDatabase.getInstance("https://water-purifier-1c9cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
        //Find the maxID in the firebase
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) maxid = (snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        /////If Sign up button is pressed
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if valid
                resetError();
                if (isBlank(signupName)) signupName.setError("Please enter fullname");
                else if (isBlank(signupEmail)) signupEmail.setError("Please enter email");
                else if (isBlank(signupUsername)) signupUsername.setError("Please enter username");
                else if (isBlank(signupPassword)) signupPassword.setError("Please enter password");
                else if (isBlank(signupRetypePassword))
                    signupRetypePassword.setError("Please retype password");
                else if (!getString(signupPassword).equals(getString(signupRetypePassword)))
                    signupPassword.setError("The password does not match");
                else checkUser();
            }
        });

        /////If Login button is pressed
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void checkUser(){
        Query query = reference.orderByChild("email").equalTo(signupEmail.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    Query newQuery = reference.orderByChild("username").equalTo(signupUsername.getText().toString());
                    newQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot newSnapshot) {
                            if (!newSnapshot.exists()){
                                resetError();
                                userInfo.setInfo((int) maxid + 1, signupName.getText().toString(),
                                        signupEmail.getText().toString(), signupUsername.getText().toString(),
                                        MD5Class.encode(signupPassword.getText().toString()), "", "", "", "", 0, 0, 0, 0);
                                reference.child(String.valueOf(maxid + 1)).setValue(userInfo);
                                addFingerprint(userInfo.id);
                            }
                            else {
                                signupUsername.setError("Username already exist");
                                signupUsername.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
                else{
                    signupEmail.setError("Email already exist");
                    signupEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void addFingerprint(long maxid) {
        dialog.setMessage("Please place your finger on the fingerprint scanner");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        try {
            MqttMessage mqttMessage = new MqttMessage(String.valueOf(maxid).getBytes());
            client.publish("user/register/start", mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        try {
            client.subscribe("user/register/done", 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (message.toString().equals("256")) {
                    dialog.dismiss();
                    //Send userInfo to next activities
                    Intent intent = new Intent(SignUpActivity.this, IntroActivity.class);
                    intent.putExtra("userInfo", new Gson().toJson(userInfo));
                    startActivity(intent);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    public String getString(EditText editText) {
        return editText.getText().toString();
    }

    public Boolean isBlank(EditText editText) {
        return getString(editText).equals("");
    }

    public void resetError() {
        signupName.setError(null);
        signupEmail.setError(null);
        signupUsername.setError(null);
        signupPassword.setError(null);
        signupRetypePassword.setError(null);
    }

}