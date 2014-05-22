package com.example.memoapp.app;

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

public class SignUpActivity extends ActionBarActivity {

    private EditText idText;
    private EditText passwordText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        idText = (EditText)findViewById(R.id.id_text_in_sign);
        passwordText = (EditText)findViewById(R.id.password_text_in_sign);
        signUpButton = (Button)findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new SignUp());

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class SignUp implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if (idText.getText().toString().matches("")) {
                Toast.makeText(getApplicationContext(), "아이디를 입력하세요", Toast.LENGTH_LONG).show();
            } else if (passwordText.getText().toString().matches("")) {
                Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_LONG).show();
            } else {
                callSignUpAPI();
            }
        }
    }

    public void callSignUpAPI(){
        final MemoAPI memoAPI = APIHandler.getApiInterface();
        memoAPI.signup(idText.getText().toString(), passwordText.getText().toString(), new Callback<APIHandler.User>() {
            @Override
            public void success(APIHandler.User user, Response response) {
                Toast.makeText(getApplicationContext(), "회원가입 되었습니다\n로그인 해주세요", Toast.LENGTH_LONG).show();
                onBackPressed();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
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

}
