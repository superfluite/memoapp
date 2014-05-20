package com.example.memoapp.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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
    private ListViewAdapter listViewAdapter;

    public enum Fragmentindex {;
        private int value;
        private Fragmentindex(int value){
            this.value=value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.new_memo);
        button.setOnClickListener(new buttonClickListener());

        memoList = (ListView)findViewById(R.id.memo_list);
        listViewAdapter = new ListViewAdapter(this);
        memoList.setAdapter(listViewAdapter);
        memoList.setOnItemLongClickListener(new MemoLongClick());
        memoList.setOnItemClickListener(new MemoClick());
    }

    private class buttonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent addActivity = new Intent(MainActivity.this, AddMemoActivity.class);
            startActivity(addActivity);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        listViewAdapter.listData.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        callMemoAPI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void callMemoAPI(){
        final MemoAPI memoAPI = APIHandler.getApiInterface();
        memoAPI.getMemo(null, new Callback<List<APIHandler.MemoData>>() {

            @Override
            public void success(List<APIHandler.MemoData> memoData, Response response) {
                //Toast.makeText(getApplicationContext(), "SUCCEED!", Toast.LENGTH_LONG).show();
                for(int i=0;i<memoData.size();i++){
                    APIHandler.MemoData data=memoData.get(i);
                    listViewAdapter.addData(data.getId(), data.getText(), data.getWritetime());
                }
                //listViewAdapter.addData(memoData.getText(), memoData.getWriteTime());
                listViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void callMemoDeleteAPI(int id){
        final MemoAPI memoAPI = APIHandler.getApiInterface();
        memoAPI.deleteMemo(id, new Callback<APIHandler.AddData>() {
            @Override
            public void success(APIHandler.AddData addData, Response response) {
                Toast.makeText(getApplicationContext(), "Memo Deleted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
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
            ListData memoData = listViewAdapter.getItem(i);
            Intent showActivity = new Intent(MainActivity.this, ShowMemoActivity.class);
            showActivity.putExtra("id", memoData.memoId);
            showActivity.putExtra("text", memoData.memoText);
            showActivity.putExtra("date", memoData.memoDate);
            startActivity(showActivity);
        }
    }

    private void MemoDialog(int pos){
        final ListData memoData = listViewAdapter.getItem(pos);
        final String items[] = {"내용 보기", "편집", "삭제", "취소"};
        final int SHOW=0;
        final int EDIT=1;
        final int DELETE=2;
        final int CANCEL=3;
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
                        editActivity.putExtra("id", memoData.memoId);
                        editActivity.putExtra("text", memoData.memoText);
                        editActivity.putExtra("date", memoData.memoDate);
                        startActivity(editActivity);
                        break;
                    case DELETE:
                        DeleteDialog(memoData.memoId);
                        break;
                    case CANCEL:
                        break;
                    default:
                }
            }
        });
        dialog.show();
    }

    private void DeleteDialog(int id){
        AlertDialog.Builder checkDialog = new AlertDialog.Builder(MainActivity.this);
        checkDialog.setMessage("삭제하시겠습니까?");
        checkDialog.setNegativeButton("취소", null);
        checkDialog.setPositiveButton("삭제",new Delete(id));
        checkDialog.show();
    }

    private class Delete implements DialogInterface.OnClickListener{
        int id;
        Delete(int id){
            this.id = id;
        }
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            callMemoDeleteAPI(id);
            Intent intent = getIntent();
            finish();
            startActivity(intent);
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

    public void changeFrag(Fragmentindex index){
        Fragment frag = null;
        frag = getFrag(index);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frag_container,frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public Fragment getFrag(Fragmentindex index){
        Fragment frag = null;
        switch (index) {
            default:
                break;
        }
        return frag;
    }

    public static class FragController{
        private Fragmentindex currentFragmentIndex;
        public Fragmentindex getCurrentFragmentIndex(){
            return this.currentFragmentIndex;
        }
        public void setCurrentFragmentIndex(Fragmentindex index){
            this.currentFragmentIndex=index;
        }
    }
}