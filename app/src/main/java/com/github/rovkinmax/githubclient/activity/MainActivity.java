package com.github.rovkinmax.githubclient.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.rovkinmax.githubclient.R;

public class MainActivity extends BaseAuthActivity {

    static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
    }
}
