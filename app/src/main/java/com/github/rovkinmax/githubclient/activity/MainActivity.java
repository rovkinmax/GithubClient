package com.github.rovkinmax.githubclient.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.github.rovkinmax.githubclient.R;

public class MainActivity extends BaseAuthActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
