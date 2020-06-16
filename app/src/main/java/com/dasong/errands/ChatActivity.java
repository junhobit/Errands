package com.dasong.errands;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ChatActivity extends Activity{
    private Activity activity;
    public static Context context;
    private ArrayList<Chat_Item> m_arr;
    private Chat_Adapter adapter;

    SwipeRefreshLayout swipeLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListView list;
    Button btn;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String user_id = user.getUid();

    //private ServiceApi service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        init();
    }

    public void init(){
        list=(ListView)findViewById(R.id.chat_list);
        setList();
    }

    private void setList(){
        m_arr = new ArrayList<Chat_Item>();
        ListView lv = (ListView)findViewById(R.id.listView);

        db.collection("users").document(user_id).collection("chat_list")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        m_arr = new ArrayList<Chat_Item>();
                        ListView lv = (ListView)findViewById(R.id.chat_list);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d(TAG, document.getId() + " => " + document.getData());
                                m_arr.add(new Chat_Item(document.getId(),document.getString("board_name")));
                                System.out.println(m_arr);

                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                        adapter = new Chat_Adapter(ChatActivity.this, m_arr);
                        lv.setAdapter(adapter);
                        //lv.setDivider(null); 구분선을 없에고 싶으면 null 값을 set합니다.
                        lv.setDividerHeight(5);// 구분선의 굵기를 좀 더 크게 하고싶으면 숫자로 높이 지정가능.*/
                        // Add a new document with a generated ID
                    }
                });
    }

    public void listUpdate(){
        this.setList();
    }
}
