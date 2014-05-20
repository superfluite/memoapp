package com.example.memoapp.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class EditMemoActivity extends ActionBarActivity {
    private EditText memoText;
    private Button editButton;
    private Button resetButton;
    private Button cancelButton;
    private int memoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);
        memoText = (EditText)findViewById(R.id.memo_text_edit);

        editButton = (Button)findViewById(R.id.edit_button);
        editButton.setOnClickListener(new Edit());
        resetButton = (Button)findViewById(R.id.reset_button_in_edit);
        resetButton.setOnClickListener(new Reset());
        cancelButton = (Button)findViewById(R.id.cancel_button_in_edit);
        cancelButton.setOnClickListener(new Cancel());

        Intent intent = getIntent();
        memoId = (Integer)intent.getExtras().get("id");
        memoText.setText(intent.getExtras().get("text").toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_memo, menu);
        return true;
    }


    private class Edit implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            callMemoAPI();
            Intent mainActivity = new Intent(EditMemoActivity.this, MainActivity.class);
            startActivity(mainActivity);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void callMemoAPI(){
        final MemoAPI memoAPI = APIHandler.getApiInterface();
        memoAPI.editMemo(memoId, memoText.getText().toString(), 1, new Callback<APIHandler.AddData>() {
            @Override
            public void success(APIHandler.AddData addData, Response response) {
                Toast.makeText(getApplicationContext(), "Memo Edited!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
    }
}