package com.dasong.errands;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dasong.errands.adapter.LocationAdapter;
import com.dasong.errands.api.ApiClient;
import com.dasong.errands.api.ApiInterface;
import com.dasong.errands.fragment.FragmentBoard;
import com.dasong.errands.model.category_search.CategoryResult;
import com.dasong.errands.model.category_search.Document;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class BoardListWrite extends AppCompatActivity implements View.OnClickListener {
    EditText mstart, marrive, mprice, mcontent, mnum, mtitle;
    String tstart, tarrive, tcontent, tprice, ttitle, tname;
    int tcount, curmoney;

    FragmentBoard fb = new FragmentBoard();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String user_id = user.getUid();

    //private ServiceApi service;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference reference = database.getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    LocationAdapter locationAdapter;
    LocationAdapter locationAdapter2;
    ArrayList<Document> documentArrayList = new ArrayList<>(); //지역명 검색 결과 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardwrite);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mtitle = (EditText) findViewById((R.id.InputTitle));
        mstart = (EditText) findViewById(R.id.map_start_search);
        marrive = (EditText) findViewById(R.id.map_arrive_search);
        mprice = (EditText) findViewById(R.id.InputCash);
        mcontent = (EditText) findViewById(R.id.InputContent);

        recyclerView = findViewById(R.id.map_recyclerview);
        recyclerView2 = findViewById(R.id.map_recyclerview2);
//         mMapViewContainer.addView(recyclerView);
        locationAdapter = new LocationAdapter(BoardListWrite.this, documentArrayList, getApplicationContext(), mstart, recyclerView ,"mstart");
        locationAdapter2 = new LocationAdapter(BoardListWrite.this, documentArrayList, getApplicationContext(), marrive, recyclerView2,"marrive");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //레이아웃매니저 생성
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(locationAdapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); //레이아웃매니저 생성
        recyclerView2.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL)); //아래구분선 세팅
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setAdapter(locationAdapter2);

        Button write = (Button) findViewById(R.id.Writebtn); // 회원가입
        write.setOnClickListener(this);

        db.collection("users").document(user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //닉네임 받아오기
                                tname = document.getString("NickName");
                                tcount = Integer.valueOf(document.getString("BoardCount"));
                                curmoney = Integer.valueOf(document.getString("Point"));
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
        // start editText 검색 텍스처이벤트
        mstart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    // if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {

                    documentArrayList.clear();
                    locationAdapter.clear();
                    locationAdapter.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation(getString(R.string.restapi_key), charSequence.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter.addItem(document);
                                }
                                locationAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });
                    //}
                    //mLastClickTime = SystemClock.elapsedRealtime();
                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때

            }


        });

        mstart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    recyclerView2.setVisibility(View.GONE);
                }
            }
        });
        mstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "검색리스트에서 장소를 선택해주세요", Toast.LENGTH_SHORT).show();
            }
        });
//
        // arrive editText 검색 텍스처이벤트
        marrive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에
                recyclerView2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    // if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {

                    documentArrayList.clear();
                    locationAdapter2.clear();
                    locationAdapter2.notifyDataSetChanged();
                    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<CategoryResult> call = apiInterface.getSearchLocation(getString(R.string.restapi_key), charSequence.toString(), 15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter2.addItem(document);
                                }
                                locationAdapter2.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {

                        }
                    });
                    //}
                    //mLastClickTime = SystemClock.elapsedRealtime();
                } else {
                    if (charSequence.length() <= 0) {
                        recyclerView2.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때

            }
        });

        marrive.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    recyclerView2.setVisibility(View.GONE);
                }
            }
        });
        marrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "검색리스트에서 장소를 선택해주세요", Toast.LENGTH_SHORT).show();
            }
        });

    } //onCreate() 종료

    @Override
    public void onClick(View v) {
        long curtime = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.Writebtn: // 글 작성
                ttitle = mtitle.getText().toString();
                tstart = mstart.getText().toString();
                tarrive = marrive.getText().toString();
                tprice = mprice.getText().toString();
                tcontent = mcontent.getText().toString();

                if (tprice.trim().length() == 0 || tcontent.trim().length() == 0) {
                    Toast.makeText(this, "내용과 가격은 빠짐없이 입력하세요!", Toast.LENGTH_SHORT).show();
                    Log.d("minsu", "공백 발생");
                    return;
                }
                else if(curmoney - Integer.valueOf(tprice) < 0){
                    Toast.makeText(this, "포인트가 부족합니다. 포인트를 충전하세요!", Toast.LENGTH_SHORT).show();
                    Log.d("jumi", "돈이 없음");
                    return;
                }

                else {
                    Map<String, Object> user = new HashMap<>();
                    user.put("ttitle", ttitle);
                    user.put("tname", tname);
                    user.put("tstart", tstart);
                    user.put("tarrive", tarrive);
                    user.put("tprice", tprice);
                    user.put("tdate", curtime);
                    user.put("tcontent", tcontent);
                    user.put("enable", true);
                    user.put("count",Integer.toString(tcount));
                    user.put("ok",0);
                    user.put("ok_name",null);

                    // Add a new document with a generated ID
                    db.collection("board").document(user_id + tcount)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    db.collection("users").document(user_id).update("BoardCount",Integer.toString(++tcount));
                                    curmoney -= Integer.valueOf(tprice);
                                    db.collection("users").document(user_id).update("Point",Integer.toString(curmoney));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                    //Intent intent = new Intent(this, MainActivity.class);
                    //startActivity(intent);

                    finish();
                }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item ){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
