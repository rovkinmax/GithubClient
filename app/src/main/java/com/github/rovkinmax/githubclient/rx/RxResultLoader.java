package com.github.rovkinmax.githubclient.rx;

import android.content.Context;
import android.content.Loader;
import android.database.ContentObserver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * @author Rovkin Max
 */
class RxResultLoader<T> extends Loader<RxResult<T>> {

    private final ContentObserver mContentObserver = new ForceLoadContentObserver();

    private final Observable<T> mObservable;

    private final Uri mContentUri;

    private boolean mOnNextCalled;

    private T mResult;

    private Subscription mSubscription;

    public RxResultLoader(@NonNull Context context, @NonNull Observable<T> observable, @Nullable Uri contentUri) {
        super(context);
        mObservable = observable;
        mContentUri = contentUri;
    }

    @Override
    public void deliverResult(RxResult<T> data) {
        if (isReset()) {
            return;
        }
        if (data != null) {
            mResult = data.getResult();
        }
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mContentUri != null) {
            getContext().getContentResolver().registerContentObserver(mContentUri, true, mContentObserver);
        }
        if (takeContentChanged() || mResult == null) {
            forceLoad();
        }
    }

    @Override
    protected void onForceLoad() {
        mSubscription = mObservable.subscribe(new Action1<T>() {
            @Override
            public void call(T t) {
                mOnNextCalled = true;
                deliverResult(RxResult.onNext(t));
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                deliverResult(RxResult.<T>onError(throwable));
            }
        }, new Action0() {
            @Override
            public void call() {
                if (!mOnNextCalled) {
                    deliverResult(null);
                }
            }
        });
    }

    @Override
    protected void onReset() {
        if (mContentUri != null) {
            getContext().getContentResolver().unregisterContentObserver(mContentObserver);
        }
        if (mSubscription != null && mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        super.onReset();
    }

}
