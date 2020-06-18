package com.dasong.errands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dasong.errands.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private List_Activity ListActivity = new List_Activity();
    private BlankFragment bFragment = new BlankFragment();


    private Button btn_chat, btn_list;
    private String userid, useremail, usernick, userpoint;
    private User crtuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /////toolbar 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //기본타이틀 안보여줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        btn_chat=(Button)findViewById(R.id.btn_chat);
        btn_list=(Button)findViewById(R.id.btn_list);

        useremail = user.getEmail();

        System.out.println(useremail);

        userid = user.getUid();
        db.collection("users").document(userid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //현재 user 정보 'crtuser'객체에 저장하기
                                //crtuser.setUserName(document.getString("UserName"));
                                //crtuser.setNickName(document.getString("NickName"));
                                //crtuser.setEmail(document.getString("Email"));
                                //crtuser.setPhoneNumber(document.getString("PhoneNumber"));

                                //tcount = Integer.valueOf(document.getString("BoardCount"));
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });



        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("useremail",useremail);
                startActivity(intent);
            }
        });

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, List_Activity.class);
                intent.putExtra("useremail",useremail);
                intent.putExtra("userid",userid);
                startActivity(intent);
            }
        });



        ////Bottom Navigation 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.frameLayout, bFragment).commitAllowingStateLoss();

        //item 선택될때 리스너
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.alertItem:
                        transaction.replace(R.id.frameLayout, bFragment).commitAllowingStateLoss();
                        break;
                    case R.id.homeItem:
                        transaction.replace(R.id.frameLayout, bFragment).commitAllowingStateLoss();
                        break;
                    case R.id.settingItem:
                        transaction.replace(R.id.frameLayout, bFragment).commitAllowingStateLoss();
                        break;
                }
                return true;
            }
        });

    }



}
