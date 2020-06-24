package com.dasong.errands.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dasong.errands.ChatActivity;
import com.dasong.errands.model.Chat_Item;
import com.dasong.errands.Chating;
import com.dasong.errands.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Chat_Adapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private Activity m_activity;
    private ArrayList<Chat_Item> arr;
    private String tname;
    private Button btn_ok;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ChatActivity chat_activity = new ChatActivity();

    public Chat_Adapter(Activity act, ArrayList<Chat_Item> arr_item) {
        this.m_activity = act;
        arr = arr_item;
        mInflater = (LayoutInflater)m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return arr.size();
    }
    @Override
    public Object getItem(int position) {
        return arr.get(position);
    }
    public long getItemId(int position){
        return position;
    }
    //position은 arraylist에 들어갈 아이템의 위치
    //
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            int res = 0;
            res = R.layout.item_chatroom;
            convertView = mInflater.inflate(res, parent, false);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = user.getUid();

        TextView mtitle = (TextView)convertView.findViewById(R.id.room_title);

        LinearLayout layout_view =  (LinearLayout)convertView.findViewById(R.id.chat_view);



        mtitle.setText(arr.get(position).Title);

        //mprice.setText(String.valueOf(arr.get(position).Price));

        /*	버튼에 이벤트처리를 하기위해선 setTag를 이용해서 사용할 수 있습니다.
         *
         * 	Button btn 가 있다면, btn.setTag(position)을 활용해서 각 버튼들의 이벤트처리를 할 수 있습니다.
         */

        RelativeLayout layout = (RelativeLayout)convertView.findViewById(R.id.chat_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoIntent(position);
            }
        });

        return convertView;
    }
    public void GoIntent(int a){
        Intent intent = new Intent(m_activity, Chating.class);
        //putExtra 로 선택한 아이템의 정보를 인텐트로 넘겨 줄 수 있다.
        intent.putExtra("board_id",arr.get(a).ID);
        intent.putExtra("board_title",arr.get(a).Title);
        intent.putExtra("ok_name",arr.get(a).Okname);
        intent.putExtra("point",arr.get(a).Point);
        System.out.println(arr.get(a).ID+arr.get(a).Title+arr.get(a).Okname+arr.get(a).Point);
        m_activity.startActivity(intent);
    }

    public static String formatTimeString(long regTime) {
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        String msg = null;

        if (diffTime < 60) {
            msg = "방금";
        } else if ((diffTime /= 60) < 60) {
            msg = diffTime + "분전";
        } else if ((diffTime /= 60) < 24) {
            msg = (diffTime) + "시간전";
        } else if ((diffTime /= 24) < 30) {
            msg = (diffTime) + "일전";
        } else if ((diffTime /= 30) < 12) {
            msg = (diffTime) + "달전";
        } else {
            msg = (diffTime) + "년전";
        }
        return msg;
    }
}
