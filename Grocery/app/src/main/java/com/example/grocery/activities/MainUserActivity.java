package com.example.grocery.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocery.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {
    private TextView nameTv,phoneTv,tabShopTV,tabOrdersTV;
    private RelativeLayout shopRL,ordersRL;
    private ImageButton logoutBtn;
    private ImageView profileTv;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        inits();
        progressDialog.setTitle("Vui lòng đợi");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();

        //at start show shop ui
        showShopUI();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              makeOffLine();
            }
        });
        tabShopTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderUI();
            }
        });
        tabOrdersTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderUI();
            }
        });
    }

    private void showOrderUI() {
        ordersRL.setVisibility(View.VISIBLE);
        shopRL.setVisibility(View.GONE);

        tabOrdersTV.setTextColor(getResources().getColor(R.color.black));
        tabOrdersTV.setBackgroundResource(R.drawable.shape_rect04);

        tabShopTV.setTextColor(getResources().getColor(R.color.white));
        tabShopTV.setBackgroundResource(getResources().getColor(android.R.color.transparent));
    }

    private void showShopUI() {
        //show shops ui, hide orders ui
        shopRL.setVisibility(View.VISIBLE);
        ordersRL.setVisibility(View.GONE);

        tabShopTV.setTextColor(getResources().getColor(R.color.black));
        tabShopTV.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdersTV.setTextColor(getResources().getColor(R.color.white));
        tabOrdersTV.setBackgroundResource(getResources().getColor(android.R.color.transparent));
    }

    private void inits() {
        nameTv=findViewById(R.id.nameTv);
        logoutBtn=findViewById(R.id.logoutBtn);
        phoneTv=findViewById(R.id.phoneTv);
        tabShopTV=findViewById(R.id.tabShopTV);
        tabOrdersTV=findViewById(R.id.tabOrdersTV);
        profileTv=findViewById(R.id.profileTv);
        shopRL=findViewById(R.id.shopRL);
        ordersRL=findViewById(R.id.ordersRL);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
    }

    private void makeOffLine() {
        //after logging in,make user online
        progressDialog.setMessage("Đăng xuất...");
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("online","false");

        //update value to db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("User");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.signOut();
                        checkUser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        }else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("User");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String name=""+ds.child("name").getValue();
                            String accountType=""+ds.child("accountType").getValue();
                            nameTv.setText(name+" ("+accountType+")");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}