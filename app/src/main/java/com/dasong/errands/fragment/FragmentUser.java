package com.dasong.errands.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dasong.errands.MainActivity;
import com.dasong.errands.R;
import com.dasong.errands.WebViewActivity;

public class FragmentUser extends Fragment {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_user, container, false);
        Button cash = (Button) v.findViewById(R.id.btn_cash);
        EditText editText = (EditText) v.findViewById(R.id.editMoney);
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("money", Integer.valueOf(editText.getText().toString()));
                startActivity(intent);
            }
        });
        return v;
    }
}
