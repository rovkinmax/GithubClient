package com.github.rovkinmax.githubclient.util;

import com.pixplicity.easyprefs.library.Prefs;

/**
 * @author Rovkin Max
 */
public class PrefUtil {
    private static final String KEY_USER_NAME = "user_name";

    private PrefUtil() {
    }

    public static void setUserName(String userName) {
        Prefs.putString(KEY_USER_NAME, userName);
    }

    public static String getUserName() {
        return Prefs.getString(KEY_USER_NAME, "");
    }
}
