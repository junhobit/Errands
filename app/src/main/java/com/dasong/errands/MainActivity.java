package com.dasong.errands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dasong.errands.fragment.FragmentCall;
import com.dasong.errands.fragment.FragmentCamera;
import com.dasong.errands.fragment.FragmentSearch;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Button btn_chat, btn_list,btn_map;
    private String useremail, userid;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentSearch fragmentSearch = new FragmentSearch();
    private FragmentCamera fragmentCamera = new FragmentCamera();
    private FragmentCall fragmentCall = new FragmentCall();
    private UserData userDATA;
    private View navheadermain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*btn_chat=(Button)findViewById(R.id.btn_chat);
        btn_list=(Button)findViewById(R.id.btn_list);
        btn_map=(Button)findViewById(R.id.btn_map);*/


        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        //기본타이틀 안보여줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //////Navigation Drawer 설정
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        //user 정보 가져오기
        Intent intent = getIntent();

        //사용자 정보 바꾸기
        //navheadermain = navigationView.getHeaderView(0); //nav_header_main 레이아웃 가져오기

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentSearch).commitAllowingStateLoss();
        //item 선택될때 리스너
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.alertItem:
                        transaction.replace(R.id.frameLayout, fragmentSearch).commitAllowingStateLoss();
                        break;
                    case R.id.homeItem:
                        transaction.replace(R.id.frameLayout, fragmentCamera).commitAllowingStateLoss();
                        break;
                    case R.id.settingItem:
                        transaction.replace(R.id.frameLayout, fragmentCall).commitAllowingStateLoss();
                        break;
                }
                return true;
            }
        });

        Bundle b = getIntent().getExtras();
        useremail = b.getString("useremail");
        userid = b.getString("userid");
        System.out.println(useremail);

       /* btn_chat.setOnClickListener(new View.OnClickListener() {
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
                //intent.putExtra("useremail",useremail);
                //intent.putExtra("userid",userid);
                startActivity(intent);
            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, MapActivity2.class);
                //intent.putExtra("useremail",useremail);
                //intent.putExtra("userid",userid);
                startActivity(intent);
            }
        });*/

        getHashKey();
    }


    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) { //menu select

        int id = item.getItemId();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (id == R.id.nav_profilechg) {
            transaction.replace(R.id.frameLayout, fragmentSearch).commitAllowingStateLoss();
        } else if (id == R.id.nav_simburm) {
            transaction.replace(R.id.frameLayout, fragmentCamera).commitAllowingStateLoss();
        }
        else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

        }
        //네비게이션 드로어 닫기 : 왼쪽
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Toast.makeText(MainActivity.this, "userDATA.nickname", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
