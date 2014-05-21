package com.example.memoapp.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ShowMemoActivity extends ActionBarActivity {

    private TextView showMemo;
    private TextView showDate;
    private Button editButton;
    private Button deleteButton;
    private Button cancelButton;
    private int memoId;
    private String memoText;
    private String memoDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_memo);

        showMemo = (TextView)findViewById(R.id.show_memo_text);
        showDate = (TextView)findViewById(R.id.show_memo_date);
        editButton = (Button)findViewById(R.id.edit_button);
        editButton.setOnClickListener(new Edit());
        deleteButton = (Button)findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new Delete());
        cancelButton = (Button)findViewById(R.id.cancel_button_in_show);
        cancelButton.setOnClickListener(new Cancel());

        Intent intent = getIntent();
        memoId = (Integer)intent.getExtras().get("id");
        memoText = intent.getExtras().get("text").toString();
        memoDate = intent.getExtras().get("date").toString();
        showMemo.setText(memoText);
        showDate.setText(memoDate);
    }

    private class Edit implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent editActivity = new Intent(ShowMemoActivity.this,EditMemoActivity.class);
            editActivity.putExtra("togoMain", false);
            editActivity.putExtra("id", memoId);
            editActivity.putExtra("text", memoText);
            editActivity.putExtra("date", memoDate);
            finish();
            startActivity(editActivity);
        }
    }

    private class Delete implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            DeleteDialog(memoId);
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
        getMenuInflater().inflate(R.menu.show_memo, menu);
        return true;
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

    private void DeleteDialog(int id){
        AlertDialog.Builder checkDialog = new AlertDialog.Builder(ShowMemoActivity.this);
        checkDialog.setMessage("삭제하시겠습니까?");
        checkDialog.setNegativeButton("취소", null);
        checkDialog.setPositiveButton("삭제", new DeleteMemo(id));
        checkDialog.show();
    }

    private class DeleteMemo implements DialogInterface.OnClickListener{
        int id;
        DeleteMemo(int id){
            this.id = id;
        }
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            callMemoDeleteAPI(id);
            onBackPressed();
        }
    }

    public void callMemoDeleteAPI(int id){
        final MemoAPI memoAPI = APIHandler.getApiInterface();
        memoAPI.deleteMemo(id, new Callback<APIHandler.AddData>() {
            @Override
            public void success(APIHandler.AddData addData, Response response) {
                Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
    }
}
