package com.dasong.errands.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.dasong.errands.LoginActivity;
import com.dasong.errands.MainActivity;
import com.dasong.errands.R;
import com.dasong.errands.WebViewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class FragmentUser extends Fragment {
    SharedPreferences auto;
    SharedPreferences.Editor autoLogin;
    SharedPreferences.Editor editor;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    Intent intent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_user, container, false);

        /* 사용자 정보 가져오기 */
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String user_id = user.getUid();

        final TextView txtNick = (TextView)v.findViewById(R.id.textNick);
        final TextView txtEmail = (TextView)v.findViewById(R.id.textEmail);
        final TextView txtPoint = (TextView)v.findViewById(R.id.textPoint);

        db.collection("users").document(user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //nickname, email, point 가져오기
                                txtNick.setText(document.getString("NickName"));
                                txtEmail.setText(document.getString("Email"));
                                txtPoint.setText(document.getString("Point"));
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


        //잔액충전 버튼 이벤트
        Button btnCash = (Button) v.findViewById(R.id.btn_point);

        btnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cfAlertDialog();
                /*Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("money", Integer.valueOf(editText.getText().toString()));
                startActivity(intent);*/
            }
        });

        //로그아웃 버튼 이벤트
        Button btnLogout = (Button) v.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Logout current user
                mAuth.signOut();
                //startActivity(new Intent(, LoginActivity.class));

            }
        });


        return v;
    }


/*
    public void onLogoutBtnClick(View view)
    {
        intent = new Intent(this.getActivity() , LoginActivity.class);
        //auto = getSharedPreferences("auto", MODE_PRIVATE);

        //editor = auto.edit();
        //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지운다
        //editor.clear();
        //editor.commit();
        //this.getActivity();
        startActivity(intent);
        this.getActivity().finish();
        Toast.makeText(this.getContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

    }
*/
    public void alertDialog()
    {
        final EditText editText = new EditText(getContext());

        AlertDialog.Builder alt_builder = new AlertDialog.Builder(getContext());
        alt_builder.setTitle("직접입력");
        alt_builder.setMessage("충전 할 금액을 입력하세요");
        alt_builder.setView(editText);
        alt_builder.setPositiveButton("충전", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int id) {
                String value = editText.getText().toString();
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("money", Integer.valueOf(value));
                startActivity(intent);
                dialogInterface.dismiss();
            }
        });
        alt_builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alt_builder.show();
    }

    public void cfAlertDialog(){
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this.getContext());
        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
        builder.setTitle("충전금액");
        builder.setItems(new String[]{"1000", "2000", "3000", "4000", "5000", "직접입력"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {

                if(index == 5){
                    alertDialog();

                }else{
                    Intent intent = new Intent(getContext(), WebViewActivity.class);
                    intent.putExtra("money", (index+1)*1000);
                    startActivity(intent);
                }
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}