package com.example.memoapp.app;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class AddMemoActivity extends ActionBarActivity {
    private EditText memoText;
    private Button saveButton;
    private Button resetButton;
    private Button cancelButton;
    private int id;
    private String userId;
    private String userPassword;
    private InputMethodManager keyboardControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        memoText = (EditText)findViewById(R.id.memo_text);

        saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(new Save());
        resetButton = (Button)findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new Reset());
        cancelButton = (Button)findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new Cancel());

        Intent intent = getIntent();
        id = (Integer)intent.getExtras().get("id");
        userId = intent.getStringExtra("userId");
        userPassword = intent.getStringExtra("userPassword");

        keyboardControl = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        keyboardControl.showSoftInput(memoText, InputMethodManager.SHOW_FORCED);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class Save implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            callMemoAPI();
            onBackPressed();
        }
    }

    private class Reset implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            memoText.setText("");
        }
    }

    private class Cancel implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_memo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void callMemoAPI(){
        final MemoAPI memoAPI = APIHandler.getApiInterface();
        memoAPI.updateMemo(memoText.getText().toString(), id, new Callback<APIHandler.AddData>(){
            @Override
            public void success(APIHandler.AddData addData, Response response) {
                Toast.makeText(getApplicationContext(), "메모가 저장되었습니다", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
    }
}