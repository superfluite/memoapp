package com.example.memoapp.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private enum Fragmentindex {
        ADD(0);
        private int value;
        private Fragmentindex(int value){
            this.value=value;
        }
    }
    Fragmentindex currentFragmentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentFragmentIndex=Fragmentindex.ADD;
        changeFrag(currentFragmentIndex);
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
