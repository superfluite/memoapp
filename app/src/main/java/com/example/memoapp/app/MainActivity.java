package com.example.memoapp.app;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

    public enum Fragmentindex {
        ADD(0);
        private int value;
        private Fragmentindex(int value){
            this.value=value;
        }
    }
    //public Fragmentindex currentFragmentIndex;
    public static class FragController{
        private Fragmentindex currentFragmentIndex;
        public Fragmentindex getCurrentFragmentIndex(){
            return this.currentFragmentIndex;
        }
        public void setCurrentFragmentIndex(Fragmentindex index){
            this.currentFragmentIndex=index;
        }
    }

    public FragController fragController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.new_memo);
        button.setOnClickListener(new buttonClickListener());

        memoList = (ListView)findViewById(R.id.memo_list);
        listViewAdapter = new ListViewAdapter(this);
        memoList.setAdapter(listViewAdapter);

        fragController = new FragController();

        callMemoAPI();
    }

    private class buttonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            fragController.setCurrentFragmentIndex(Fragmentindex.ADD);
            changeFrag(fragController.getCurrentFragmentIndex());
            View main=findViewById(R.id.memos);
            main.setVisibility(View.GONE);
        }
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
                Toast.makeText(getApplicationContext(), "SUCCEED!", Toast.LENGTH_LONG).show();
                for(int i=0;i<memoData.size();i++){
                    APIHandler.MemoData data=memoData.get(i);
                    listViewAdapter.addData(data.getText(),data.getWritetime());
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

    private class ViewHolder {
        public TextView memotext;
        public TextView memodate;
    }

    private class ListViewAdapter extends BaseAdapter {
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

        public void addData(String text, String date){
            ListData data = new ListData();
            data.memoText = text;
            data.memoDate = date;

            listData.add(data);
        }
        /*
        public void addData(List<APIHandler.MemoData> data){
            data.
        }
        */
        public void deleteData(int position){
            listData.remove(position);
            listViewAdapter.notifyDataSetChanged();
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
            case ADD:
                frag=new AddMemoFragment();
            default:
                break;
        }
        return frag;
    }
}