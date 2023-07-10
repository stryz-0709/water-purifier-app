package com.snig.waterpurifier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class EditProfileActivity extends AppCompatActivity {
    UserInfo userInfo;
    EditText editName, editEmail, editUsername, editOldPassword, editPassword, editRePassword;
    Button saveButton;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userInfo = new Gson().fromJson(getIntent().getExtras().getString("userInfo"), UserInfo.class);

        reference = FirebaseDatabase.getInstance("https://water-purifier-1c9cd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editUsername = findViewById(R.id.editUsername);
        editOldPassword = findViewById(R.id.oldPassword);
        editRePassword = findViewById(R.id.editRetypePassword);
        editPassword = findViewById(R.id.editPassword);

        editName.setText(userInfo.name);
        editEmail.setText(userInfo.email);
        editUsername.setText(userInfo.username);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetError();
                if (!isBlank(editOldPassword) && !userInfo.password.equals(MD5Class.encode(getString(editOldPassword)))) editOldPassword.setError("The password does not match");
                else if (!isBlank(editPassword) && isBlank(editOldPassword)) editOldPassword.setError("Please enter old password");
                else if (isBlank(editRePassword) && !isBlank(editPassword)) editRePassword.setError("Please retype password");
                else if (!getString(editPassword).equals(getString(editRePassword))) editPassword.setError("The password does not match");
                else checkUser();
            }
        });
    }

    public void checkUser(){
        Query query = reference.orderByChild("email").equalTo(editEmail.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || getString(editEmail).equals(userInfo.email)){
                    Query newQuery = reference.orderByChild("username").equalTo(editUsername.getText().toString());
                    newQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot newSnapshot) {
                            if (!newSnapshot.exists() || getString(editUsername).equals(userInfo.username)){
                                resetError();
                                boolean checkName = isNameChanged();
                                boolean checkPassword = isPasswordChanged();
                                boolean checkEmail = isEmailChanged();
                                boolean checkUsername = isUsernameChanged();
                                if (checkName || checkPassword || checkEmail || checkUsername) Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                                else Toast.makeText(EditProfileActivity.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                intent.putExtra("userInfo", new Gson().toJson(userInfo));
                                intent.putExtra("fromEdit", "true");
                                startActivity(intent);
                            }
                            else{
                                editUsername.setError("Username already exist");
                                editUsername.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
                else{
                    editEmail.setError("Email already exist");
                    editEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private boolean isNameChanged() {
        if (!userInfo.name.equals(getString(editName))){
            userInfo.name = getString(editName);
            reference.child(String.valueOf(userInfo.id)).child("name").setValue(userInfo.name);
            return true;
        }
        else return false;
    }

    private boolean isEmailChanged() {
        if (!userInfo.email.equals(getString(editEmail))){
            userInfo.email = getString(editEmail);
            reference.child(String.valueOf(userInfo.id)).child("email").setValue(userInfo.email);
            return true;
        }
        else return false;
    }

    private boolean isUsernameChanged() {
        if (!userInfo.username.equals(getString(editUsername))){
            userInfo.username = getString(editUsername);
            reference.child(String.valueOf(userInfo.id)).child("username").setValue(userInfo.username);
            return true;
        }
        else return false;
    }

    private boolean isPasswordChanged() {
        if (!isBlank(editPassword) && !userInfo.password.equals(MD5Class.encode(getString(editPassword)))){
            userInfo.password = MD5Class.encode(getString(editPassword));
            reference.child(String.valueOf(userInfo.id)).child("password").setValue(userInfo.password);
            return true;
        }
        else return false;
    }

    public String getString(EditText editText) {return editText.getText().toString();}

    public Boolean isBlank(EditText editText){return getString(editText).equals("");}

    public void resetError(){
        editName.setError(null);
        editEmail.setError(null);
        editUsername.setError(null);
        editPassword.setError(null);
        editRePassword.setError(null);
        editOldPassword.setError(null);
    }

}