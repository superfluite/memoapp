package com.example.memoapp.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity {

    private ListView memoList;
    private ListViewAdapter memoDataList;
    private ListViewAdapter searchList;
    private UserData currentUser = new UserData();
    private Button newMemoButton;
    private Button loginButton;
    private Button signUpButton;
    private EditText idText;
    private EditText passwordText;
    private EditText searchText;
    private MemoAPI memoAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newMemoButton = (Button)findViewById(R.id.new_memo);
        newMemoButton.setOnClickListener(new NewMemo());
        loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new Login());
        signUpButton = (Button)findViewById(R.id.to_sign_up_button);
        signUpButton.setOnClickListener(new SignUp());
        idText = (EditText)findViewById(R.id.id_text);
        passwordText = (EditText)findViewById(R.id.password_text);
        searchText = (EditText)findViewById(R.id.search_memo);
        searchText.addTextChangedListener(new Searching());
        searchList = new ListViewAdapter(this);

        memoList = (ListView)findViewById(R.id.memo_list);
        memoDataList = new ListViewAdapter(this);
        memoList.setAdapter(memoDataList);
        memoList.setOnItemLongClickListener(new MemoLongClick());
        memoList.setOnItemClickListener(new MemoClick());

        memoAPI = APIHandler.getApiInterface();

        Bundle check = getIntent().getExtras();
        if (check != null) {
            currentUser.id = check.getInt("id");
            currentUser.userId = check.getString("userId");
            currentUser.userPassword = check.getString("userPassword");
        }
    }

    private class NewMemo implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent addActivity = new Intent(MainActivity.this, AddMemoActivity.class);
            addActivity.putExtra("id", currentUser.id);
            startActivity(addActivity);
        }
    }

    private class Login implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (idText.getText().toString().matches("")) {
                Toast.makeText(getApplicationContext(), "아이디를 입력하세요", Toast.LENGTH_LONG).show();
            } else if(passwordText.getText().toString().matches("")) {
                Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_LONG).show();
            } else {
                callLoginAPI();
            }
        }
    }

    private class SignUp implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent signUpActivity = new Intent(MainActivity.this,SignUpActivity.class);
            startActivity(signUpActivity);
        }
    }

    private class Searching implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!(searchText.getText().toString().matches(""))) {
                if (searchText.isFocusable()) {
                    searchList.listData.clear();
                    String searchWord = searchText.getText().toString();
                    memoList.setAdapter(searchList);
                    for (int i=0;i<memoDataList.getCount();i++) {
                        ListData data = memoDataList.getItem(i);
                        if (data.memoText.contains(searchWord)) {
                            searchList.addData(data.memoId, data.memoText, data.memoDate);
                        }
                    }
                    searchList.notifyDataSetChanged();
                }
            } else {
                memoList.setAdapter(memoDataList);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        memoDataList.listData.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser.userId == null) {
            changeViewGone(R.id.memos);
            changeViewVisible(R.id.login);
        } else {
            searchText.setText("");
            changeViewGone(R.id.login);
            changeViewVisible(R.id.memos);
            callMemoAPI();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (currentUser.userId != null) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.logout :
                LogoutDialog();
                break;
            case R.id.delete_account :
                DeleteAccountDialog();
                break;
            default :
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MemoLongClick implements AdapterView.OnItemLongClickListener{
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            MemoDialog(i);
            return false;
        }
    }

    private class MemoClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ListData memoData = memoDataList.getItem(i);
            Intent showActivity = new Intent(MainActivity.this, ShowMemoActivity.class);
            showActivity.putExtra("id", memoData.memoId);
            showActivity.putExtra("text", memoData.memoText);
            showActivity.putExtra("date", memoData.memoDate);
            startActivity(showActivity);
        }
    }

    private void MemoDialog(int pos){
        final ListData memoData = memoDataList.getItem(pos);
        final int position = pos;
        final String items[] = {"내용 보기", "수정", "삭제", "취소"};
        final int SHOW = 0;
        final int EDIT = 1;
        final int DELETE = 2;
        final int CANCEL = 3;
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch(i){
                    case SHOW:
                        Intent showActivity = new Intent(MainActivity.this, ShowMemoActivity.class);
                        showActivity.putExtra("id", memoData.memoId);
                        showActivity.putExtra("text", memoData.memoText);
                        showActivity.putExtra("date", memoData.memoDate);
                        startActivity(showActivity);
                        break;
                    case EDIT:
                        Intent editActivity = new Intent(MainActivity.this, EditMemoActivity.class);
                        editActivity.putExtra("togoMain", true);
                        editActivity.putExtra("id", memoData.memoId);
                        editActivity.putExtra("text", memoData.memoText);
                        editActivity.putExtra("date", memoData.memoDate);
                        startActivity(editActivity);
                        break;
                    case DELETE:
                        DeleteDialog(memoData.memoId, position);
                        break;
                    case CANCEL:
                        break;
                    default:
                }
            }
        });
        dialog.show();
    }

    private void DeleteDialog(int id, int position){
        AlertDialog.Builder checkDialog = new AlertDialog.Builder(MainActivity.this);
        checkDialog.setMessage("삭제하시겠습니까?");
        checkDialog.setNegativeButton("취소", null);
        checkDialog.setPositiveButton("삭제", new Delete(id, position));
        checkDialog.show();
    }

    private class Delete implements DialogInterface.OnClickListener{
        int id;
        int position;
        Delete(int id, int position){
            this.id = id;
            this.position = position;
        }
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            callMemoDeleteAPI(id);
            memoDataList.deleteData(position);
            memoDataList.notifyDataSetChanged();
        }
    }

    private void LogoutDialog(){
        AlertDialog.Builder checkDialog = new AlertDialog.Builder(MainActivity.this);
        checkDialog.setMessage("로그아웃하시겠습니까?");
        checkDialog.setNegativeButton("취소", null);
        checkDialog.setPositiveButton("로그아웃", new Logout());
        checkDialog.show();
    }

    private class Logout implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            currentUser = new UserData();
            Intent refresh = new Intent(MainActivity.this, MainActivity.class);
            finish();
            startActivity(refresh);
        }
    }

    private void DeleteAccountDialog(){
        AlertDialog.Builder checkoutDialog = new AlertDialog.Builder(MainActivity.this);
        checkoutDialog.setMessage("탈퇴하시겠습니까?\n탈퇴시 작성했던 모든 메모가 삭제됩니다");
        checkoutDialog.setNegativeButton("취소", null);
        checkoutDialog.setPositiveButton("탈퇴", new DeleteAccount());
        checkoutDialog.show();
    }

    private class DeleteAccount implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            callDeleteAccountAPI();
        }
    }

    public static class ViewHolder {
        public TextView memotext;
        public TextView memodate;
    }

    public static class ListViewAdapter extends BaseAdapter {
        private Context context=null;
        private ArrayList<ListData> listData = new ArrayList<ListData>();

        public ListViewAdapter(Context context){
            super();
            this.context=context;
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public ListData getItem(int i) {
            return listData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listview_custom, null);

                holder.memotext = (TextView)view.findViewById(R.id.text);
                holder.memodate = (TextView)view.findViewById(R.id.date);

                view.setTag(holder);
            } else {
                holder = (ViewHolder)view.getTag();
            }

            ListData data = listData.get(i);
            holder.memotext.setText(data.memoText);
            holder.memodate.setText(data.memoDate);

            return view;
        }

        public void addData(int id, String text, String date){
            ListData data = new ListData();
            data.memoId = id;
            data.memoText = text;
            data.memoDate = date;

            listData.add(data);
        }

        public void deleteData(int position){
            listData.remove(position);
        }
    }

    public void callMemoAPI(){
        //final MemoAPI memoAPI = APIHandler.getApiInterface();
        memoAPI.getMemo(currentUser.id, new Callback<List<APIHandler.MemoData>>() {

            @Override
            public void success(List<APIHandler.MemoData> memoData, Response response) {
                for (int i = 0; i < memoData.size(); i++) {
                    APIHandler.MemoData data = memoData.get(i);
                    memoDataList.addData(data.getId(), data.getText(), data.getWritetime());
                }
                memoDataList.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                Toast.makeText(getApplicationContext(), "불러오기 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void callMemoDeleteAPI(int id){
        //final MemoAPI memoAPI = APIHandler.getApiInterface();
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

    public void callLoginAPI(){
        //final MemoAPI memoAPI = APIHandler.getApiInterface();
        memoAPI.login(idText.getText().toString(), passwordText.getText().toString(), new Callback<APIHandler.User>() {
            @Override
            public void success(APIHandler.User user, Response response) {
                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
                Intent mainActivity = new Intent(MainActivity.this, MainActivity.class);
                mainActivity.putExtra("id", user.getId());
                mainActivity.putExtra("userId", user.getUserId());
                mainActivity.putExtra("userPassword", user.getUserPassword());
                finish();
                startActivity(mainActivity);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_LONG).show();
                retrofitError.printStackTrace();
            }
        });
    }

    public void callDeleteAccountAPI(){
        //final MemoAPI memoAPI = APIHandler.getApiInterface();
        memoAPI.deleteAccount(currentUser.id, new Callback<APIHandler.User>() {
            @Override
            public void success(APIHandler.User user, Response response) {
                Toast.makeText(getApplicationContext(), "탈퇴하였습니다", Toast.LENGTH_LONG).show();
                Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                finish();
                startActivity(refresh);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                Toast.makeText(getApplicationContext(), "탈퇴 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changeViewVisible(int id){
        View Target = findViewById(id);
        Target.setVisibility(View.VISIBLE);
    }

    private void changeViewGone(int id){
        View Target = findViewById(id);
        Target.setVisibility(View.GONE);
    }
}