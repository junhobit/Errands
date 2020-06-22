package com.dasong.errands.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dasong.errands.Chat_Item;
import com.dasong.errands.R;
import com.dasong.errands.adapter.Chat_Adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragmentChat extends Fragment {
    private Activity activity;
    public static Context context;
    private ArrayList<Chat_Item> m_arr;
    private Chat_Adapter adapter;
    private View v;

    SwipeRefreshLayout swipeLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListView list;
    Button btn;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String user_id = user.getUid();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.activity_chatroom, container, false);
        init();
        return v;
    }
    public void init(){
        list=(ListView) v.findViewById(R.id.chat_list);
        setList();
    }

    private void setList(){
        m_arr = new ArrayList<Chat_Item>();
        ListView lv = (ListView)v.findViewById(R.id.listView);

        db.collection("users").document(user_id).collection("chat_list")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        m_arr = new ArrayList<Chat_Item>();
                        ListView lv = (ListView)v.findViewById(R.id.chat_list);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                m_arr.add(new Chat_Item(document.getId(),document.getString("board_name")));
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                        adapter = new Chat_Adapter(activity, m_arr);
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
