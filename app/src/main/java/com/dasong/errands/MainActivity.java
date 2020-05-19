package com.dasong.errands;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btn_chat, btn_list;
    private String useremail, userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_chat=(Button)findViewById(R.id.btn_chat);
        btn_list=(Button)findViewById(R.id.btn_list);

        Bundle b = getIntent().getExtras();
        useremail = b.getString("useremail");
        userid = b.getString("userid");
        System.out.println(useremail);

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
                //intent.putExtra("useremail",useremail);
                //intent.putExtra("userid",userid);
                startActivity(intent);
            }
        });
    }
}
