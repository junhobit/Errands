package com.dasong.errands;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 10;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    EditText userId, userPwd;
    Button loginBtn;
    TextView signUp;
    String loginid, loginpwd;
    private int tcount;
    private static final String TAG = "EmailLogin";

    // Cloud Firestore 인스턴스를 초기화합니다.
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    SharedPreferences auto;
    SharedPreferences.Editor autoLogin;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userId = findViewById(R.id.LoginId);
        userPwd = findViewById(R.id.LoginPw);
        loginBtn = findViewById(R.id.login);
        signUp = findViewById(R.id.signup);

        // 로그인 작업의 onCreate 메서드에서 FirebaseAuth 객체의 공유 인스턴스를 가져옵니다.
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton btn_google_login = findViewById(R.id.btn_google_login);
        btn_google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        auto = getSharedPreferences("auto", MODE_PRIVATE);

        loginid = auto.getString("inputId",null);
        loginpwd = auto.getString("inputPwd",null);

        if(loginid !=null && loginpwd != null) {

            startLogin(loginid,loginpwd);

            Toast.makeText(getApplicationContext(), loginid +"님 자동로그인 입니다.", Toast.LENGTH_SHORT).show();


        }


    }

    private void signIn() {
        // GCP Console에서 사용자 인증 정보 페이지를 엽니다.
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // 웹 애플리케이션 유형의 클라이언트 ID가 백엔드 서버의 OAuth 2.0 클라이언트 ID입니다.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    // 활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인합니다.
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // 로그인이 되어있을 시 취할 코드 삽입
    }
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                loginid = userId.getText().toString();
                loginpwd = userPwd.getText().toString();
                startLogin(loginid,loginpwd);
                break;

            case R.id.signup : // 회원가입
                Intent intent = new Intent(this, JoinActivity.class);
                startActivity(intent);
                break;

        }
    }
    //form형식 로그인인증
    private void startLogin(String loginid, String loginpwd) {
        mAuth.signInWithEmailAndPassword(loginid, loginpwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            //updateUI(user);

                            auto = getSharedPreferences("auto", MODE_PRIVATE);
                            editor = auto.edit();
                            //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지운다
                            editor.clear();
                            editor.commit();


                            auto = getSharedPreferences("auto", MODE_PRIVATE);
                            autoLogin = auto.edit();
                            autoLogin.putString("inputId", loginid);
                            autoLogin.putString("inputPwd", loginpwd);
                            //꼭 commit()을 해줘야 값이 저장됨
                            autoLogin.commit();
                            Toast.makeText(getApplicationContext(), loginid+"님 환영합니다.", Toast.LENGTH_SHORT).show();
                            gotonext();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    // 사용자가 정상적으로 로그인하면 GoogleSignInAccount 객체에서 ID 토큰을 가져와서
    // Firebase 사용자 인증 정보로 교환하고 해당 정보를 사용해 Firebase 인증을 받습니다.
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "구글 로그인 성공", Toast.LENGTH_SHORT);
                            // User DB 생성
                            setDocument();
                            // 로그인한 사용자를 받아옴
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "구글 로그인 실패", Toast.LENGTH_SHORT);
                        }

                    }
                });
    }

    // 파이어스토어 데이터베이스 생성
    public void setDocument() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        assert mUser != null;
        String userUID = mUser.getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("Email", mUser.getEmail());
        user.put("UserName", mUser.getDisplayName());
        user.put("NickName", mUser.getDisplayName());
        user.put("PhoneNumber", mUser.getPhoneNumber());
        user.put("BoardCount", "0");
        db.collection("users").document(userUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //닉네임 받아오기
                                tcount = Integer.valueOf(document.getString("BoardCount"));
                            } else {
                                db.collection("users")
                                        .document(userUID)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG", "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Error adding document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

    }
    //Main으로 전해주는 정보 (id, email, phonenum, nickname)
    public void gotonext(){
        user = mAuth.getCurrentUser();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}
