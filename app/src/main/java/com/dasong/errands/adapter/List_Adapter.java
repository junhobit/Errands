package com.dasong.errands.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dasong.errands.BoardDetailActivity;
import com.dasong.errands.Chating;
import com.dasong.errands.List_Activity;
import com.dasong.errands.model.List_Item;
import com.dasong.errands.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class List_Adapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private Activity m_activity;
    private ArrayList<List_Item> arr;
    private String tname;
    private Button btn_ok;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List_Activity list_activity = new List_Activity();

    public List_Adapter(Activity act, ArrayList<List_Item> arr_item) {
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
            res = com.dasong.errands.R.layout.list_item;
            convertView = mInflater.inflate(res, parent, false);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = user.getUid();

        TextView mtitle = (TextView)convertView.findViewById(R.id.item_title);
        TextView mstart = (TextView)convertView.findViewById(R.id.item_start);
        TextView marrive = (TextView)convertView.findViewById(R.id.item_arrive);
        TextView mdate = (TextView)convertView.findViewById(R.id.item_date);
        TextView mwriter = (TextView)convertView.findViewById(R.id.item_writer);
        TextView mdetail = (TextView)convertView.findViewById(R.id.Content);
        TextView mprice = (TextView)convertView.findViewById(R.id.item_price);


        btn_ok= (Button)convertView.findViewById(R.id.item_ok);
//        Button btn_detail = (Button) convertView.findViewById(R.id.item_detail);
        Button btn_delete = (Button) convertView.findViewById(R.id.item_delete);

        mtitle.setText(arr.get(position).Title);
        mstart.setText(arr.get(position).Start);
        marrive.setText(arr.get(position).Arrive);
        mdate.setText(formatTimeString(arr.get(position).Date));
        mwriter.setText(arr.get(position).Writer);
        mprice.setText(arr.get(position).Price);
        //mprice.setText(String.valueOf(arr.get(position).Price));

        /*	버튼에 이벤트처리를 하기위해선 setTag를 이용해서 사용할 수 있습니다.
         *
         * 	Button btn 가 있다면, btn.setTag(position)을 활용해서 각 버튼들의 이벤트처리를 할 수 있습니다.
         */
        btn_ok.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(m_activity, Chating.class);
                intent.putExtra("board_id",arr.get(position).ID);

                Map<String, Object> user = new HashMap<>();
                user.put("board_id", arr.get(position).ID);
                user.put("board_name", arr.get(position).Title);


                db.collection("users").document(user_id).collection("chat_list").document(arr.get(position).ID)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                db.collection("users").document(arr.get(position).ID.substring(0,28)).collection("chat_list").document(arr.get(position).ID)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                db.collection("board").document(arr.get(position).ID).update("enable",false);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                m_activity.startActivity(intent);
            }
        });

        LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.vi_view);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoDetail(position);
            }
        });

        btn_delete.setVisibility(View.INVISIBLE);

        if(user_id.equals(arr.get(position).ID.substring(0,28)))
            btn_delete.setVisibility(View.VISIBLE);

        btn_delete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("주의").setMessage("정말로 삭제하시겠습니까?");
                        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                        db.collection("board").document(user_id + arr.get(position).Count).delete();
                        list_activity.listUpdate();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        return convertView;
    }
    public void GoDetail(int a){
        Intent intent = new Intent(m_activity, BoardDetailActivity.class);
        //putExtra 로 선택한 아이템의 정보를 인텐트로 넘겨 줄 수 있다.
        intent.putExtra("TITLE", arr.get(a).Title);
        intent.putExtra("START", arr.get(a).Start);
        intent.putExtra("ARRIVE", arr.get(a).Arrive);
        intent.putExtra("DATE", arr.get(a).Date);
        intent.putExtra("WRITER", arr.get(a).Writer);
        intent.putExtra("Detail",arr.get(a).Detail);
        System.out.println("GoIntent!!!!!!!!"+arr.get(a).Detail);
        intent.putExtra("PRICE",arr.get(a).Price);
        intent.putExtra("ID",arr.get(a).ID);
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
