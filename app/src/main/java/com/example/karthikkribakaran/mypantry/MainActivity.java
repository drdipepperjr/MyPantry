package com.example.karthikkribakaran.mypantry;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v4.app.Fragment;

public class MainActivity extends AppCompatActivity
        implements MainMenu.OnFragmentInteractionListener,
                    MetricsMainMenu.OnFragmentInteractionListener,
                    MyGroceries.OnFragmentInteractionListener,
                    AddGrocery.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment menu = new MainMenu();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, menu).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}
