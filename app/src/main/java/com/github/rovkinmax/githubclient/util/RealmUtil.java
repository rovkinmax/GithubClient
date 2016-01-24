package com.github.rovkinmax.githubclient.util;

import android.content.Context;
import android.support.annotation.NonNull;

import io.realm.Realm;

/**
 * @author Rovkin Max
 */
public final class RealmUtil {
    private RealmUtil() {
    }

    public static void safeTransaction(@NonNull Context context, @NonNull Realm.Transaction listener) {
        Realm realm = Realm.getInstance(context);
        //noinspection TryFinallyCanBeTryWithResources
        try {
            safeTransaction(realm, listener);
        } finally {
            realm.close();
        }
    }

    public static void safeTransaction(@NonNull Realm realm,
                                       @NonNull Realm.Transaction listener) {
        try {
            realm.executeTransaction(listener);
        } catch (Throwable ignored) {
        }
    }
}
