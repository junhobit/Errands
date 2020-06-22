package com.dasong.errands;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.textclassifier.TextLinks;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SendNotification {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    final String user_id = user.getUid();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void sendNotification(String regToken, String title, String messsage){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... parms) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("body", messsage);
                    dataJson.put("title", title);
                    json.put("notification", dataJson);
                    json.put("to", regToken);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + "AAAAPltKOMg:APA91bFqawTgY26wcrcEkVOykVSxFgBYFmvX4kbWt5xTCtnK15Go5fA1nnsN2WGtbuMGr62nvIg6l2c7CIYq_pVtrXpZderRvjsh_KUTqHAn1f-07wLglRflt9-3aB7pAEq9u4yv3hPu")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    System.out.println("sended");
                }catch (Exception e){
                    Log.d("error", e+"");
                    System.out.println("error");
                }
                return  null;
            }
        }.execute();
    }
    private void sendGson() {
        reference.child("Chat_rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String,String> map= (Map<String, String>) dataSnapshot.getValue(); //상대유저의 토큰
                String mPushToken = map.get("text");


                Log.d(TAG, "상대방의 토큰 : " + mPushToken);
                reference.child("7FO3rt3veUZdGaS14gEL4zV8AQh17").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ContactsContract.Profile profile = dataSnapshot.getValue(ContactsContract.Profile.class);
                        SendNotification.sendNotification(mPushToken, "Test", "Test");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
