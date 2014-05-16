package com.example.memoapp.app;

import android.app.ActionBar;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity {

    private ListView memoList;
    private ListViewAdapter listViewAdapter;

    private enum Fragmentindex {
        ADD(0);
        private int value;
        private Fragmentindex(int value){
            this.value=value;
        }
    }
    private Fragmentindex currentFragmentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //currentFragmentIndex = Fragmentindex.ADD;
        //changeFrag(currentFragmentIndex);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new buttonClickListener());

        memoList = (ListView)findViewById(R.id.memo_list);
        listViewAdapter = new ListViewAdapter(this);
        memoList.setAdapter(listViewAdapter);
    }

    private class buttonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            callMemoAPI();
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
        MemoAPI memoAPI = APIHandler.getApiInterface();
        memoAPI.getMemo(null,new Callback<APIHandler.MemoData>() {

            @Override
            public void success(APIHandler.MemoData memoData, Response response) {
                Toast.makeText(getApplicationContext(),"SUCCEED!",Toast.LENGTH_LONG).show();
                listViewAdapter.addData(memoData.text,memoData.writetime);
                listViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                Toast.makeText(getApplicationContext(),"Falied",Toast.LENGTH_LONG).show();
            }
        });
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

        public void deleteData(int position){
            listData.remove(position);
            listViewAdapter.notifyDataSetChanged();
        }
    }
}
