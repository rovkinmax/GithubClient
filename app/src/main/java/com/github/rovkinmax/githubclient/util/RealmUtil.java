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

    public static void safeTransaction(@NonNull Context context, @NonNull TransactionListener listener) {
        Realm realm = Realm.getInstance(context);
        //noinspection TryFinallyCanBeTryWithResources
        try {
            safeTransaction(realm, listener);
        } finally {
            realm.close();
        }
    }

    public static void safeTransaction(@NonNull Realm realm,
                                       @NonNull TransactionListener listener) {
        try {
            realm.beginTransaction();
            listener.makeTransaction(realm);
            realm.commitTransaction();
        } catch (Throwable e) {
            realm.cancelTransaction();
        }
    }

    public interface TransactionListener {
        void makeTransaction(Realm realm);
    }
}
