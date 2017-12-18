package com.example.karthikkribakaran.mypantry;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements MainMenu.OnFragmentInteractionListener,
                    MetricsMainMenu.OnFragmentInteractionListener,
                    MyGroceries.OnFragmentInteractionListener,
                    AddGrocery.OnFragmentInteractionListener{

    DBHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);

        // create sample values for testing
        //db.generateSampleItems();
        //db.generateSampleYear();
        //db.generateSampleUsedItems();

        Fragment menu = new MyGroceries();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, menu).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.metrics_button:
                Fragment menu = new MetricsMainMenu();
                getSupportFragmentManager().beginTransaction().replace(android.R.id.content, menu).addToBackStack(null).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
