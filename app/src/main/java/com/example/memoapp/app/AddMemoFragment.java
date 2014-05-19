package com.example.memoapp.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class AddMemoFragment extends Fragment {

    private EditText memoText;

    private Button cancelButton;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        memoText = (EditText)getView().findViewById(R.id.memo_text);

        cancelButton = (Button)getView().findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new Cancel());
    }

    private class Cancel implements View.OnClickListener{
        @Override
        public void onClick(View view) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_add_memo_fragment,null,false);
    }
}