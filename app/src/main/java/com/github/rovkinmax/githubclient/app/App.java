package com.github.rovkinmax.githubclient.app;

import android.app.Application;
import android.content.ContextWrapper;

import com.github.rovkinmax.githubclient.model.AuthData;
import com.pixplicity.easyprefs.library.Prefs;

/**
 * @author Rovkin Max
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        AuthData.setup(this);
    }
}
