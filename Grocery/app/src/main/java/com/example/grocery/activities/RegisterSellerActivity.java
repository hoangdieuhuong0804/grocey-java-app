package com.example.grocery.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grocery.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterSellerActivity extends AppCompatActivity  {
    private ImageButton backBtn ;
    private EditText shopNameEt;
    private EditText nameEt, phoneEt, deliveryEt, countryEt, stateEt, cityEt, addressEt, emailEt, passwordEt, cPasswordEt;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
  private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seller);
        inits();

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng đợi...");
        progressDialog.setCanceledOnTouchOutside(false);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dang ky
                inputData();

            }
        });

    }
    private String fullName;
    private String shopName;
    private String phoneNumber;
    private String deliveryFree;
    private String country;
    private String state;
    private String city;
    private String address;
    private String email;
    private String password;
    private String congirmPassword;
    private void inputData() {
        //input data
        fullName=nameEt.getText().toString().trim();
        shopName=shopNameEt.getText().toString().trim();
        phoneNumber=phoneEt.getText().toString().trim();
        deliveryFree=deliveryEt.getText().toString().trim();
        country=countryEt.getText().toString().trim();
        state=stateEt.getText().toString().trim();
        city=cityEt.getText().toString().trim();
        address=addressEt.getText().toString().trim();
        email=emailEt.getText().toString().trim();
        password=passwordEt.getText().toString().trim();
        congirmPassword=cPasswordEt.getText().toString().trim();

        //validate data
        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this,"Vui lòng nhập tên...",Toast.LENGTH_SHORT).show();
            return;

        }
        if(TextUtils.isEmpty(shopName)){
            Toast.makeText(this,"Vui lòng nhập tên shop...",Toast.LENGTH_SHORT).show();
            return;

        }
        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this,"Vui lòng nhập số điện thoại...",Toast.LENGTH_SHORT).show();
            return;

        }
        if(TextUtils.isEmpty(deliveryFree)){
            Toast.makeText(this,"Vui lòng nhập đơn vị vận chuyển...",Toast.LENGTH_SHORT).show();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Email không đúng ...",Toast.LENGTH_SHORT).show();
            return;

        }
        if(password.length()<6){
            Toast.makeText(this,"Mật khẩu phải có ít nhất 6 ký tự...",Toast.LENGTH_SHORT).show();
            return;

        }
        if(!password.equals(congirmPassword)){
            Toast.makeText(this,"Mật khẩu không đúng...",Toast.LENGTH_SHORT).show();

            return;

        }
        createAccount();
    }

    private void createAccount() {
        progressDialog.setMessage("Đang tạo tài khoản...");
        progressDialog.show();

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveFirebaseData();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterSellerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void saveFirebaseData() {
        progressDialog.setMessage("Đang lưu tài khoản...");
        String timestamp = "" + System.currentTimeMillis();
        //setup data to save
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("email", "" + email);
        hashMap.put("name", ""+fullName);
        hashMap.put("shopName",""+shopName);
        hashMap.put("phone",""+phoneNumber);
        hashMap.put("deliveryFree",""+deliveryFree);
        hashMap.put("country",""+country);
        hashMap.put("state",""+state);
        hashMap.put("city",""+city);
        hashMap.put("address",""+address);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("accountType","Seller");
        hashMap.put("online","true");
        hashMap.put("shopOpen","true");

        //Save to db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("User");
        ref.child(firebaseAuth.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //db update
                        progressDialog.dismiss();
                        startActivity(new Intent(RegisterSellerActivity.this, MainSellerActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    //faled updating db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterSellerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

        @SuppressLint("WrongViewCast")
    private void inits() {
        backBtn = findViewById(R.id.backBtn);
        shopNameEt = findViewById(R.id.shopNameEt);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        countryEt = findViewById(R.id.countryEt);
        stateEt = findViewById(R.id.stateEt);
        cityEt = findViewById(R.id.cityEt);
        addressEt = findViewById(R.id.addressEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        cPasswordEt = findViewById(R.id.cPasswordEt);
        registerBtn = findViewById(R.id.registerBtn);
        deliveryEt = findViewById(R.id.deliveryEt);


    }

}