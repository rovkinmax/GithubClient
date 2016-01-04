package com.github.rovkinmax.githubclient.rx;

import android.app.LoaderManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.observers.Observers;

/**
 * @author Rovkin Max
 */
public class RxLoader<T> {

    private final Context mContext;

    private final Reference<LoaderManager> mLmRef;

    private final int mLoaderId;

    private final Observable<T> mObservable;

    private Uri mContentUri;

    private RxLoader(@NonNull Context context, @NonNull LoaderManager lm, int loaderId,
                     @NonNull Observable<T> observable) {
        mContext = context;
        mLmRef = new WeakReference<>(lm);
        mLoaderId = loaderId;
        mObservable = observable;
    }

    @NonNull
    public static <T> RxLoader<T> create(@NonNull Context context, @NonNull LoaderManager lm, int loaderId,
                                         @NonNull Observable<T> observable) {
        return new RxLoader<>(context.getApplicationContext(), lm, loaderId, observable);
    }

    @NonNull
    public RxLoader<T> withContentUri(@NonNull Uri contentUri) {
        mContentUri = contentUri;
        return this;
    }

    public void init() {
        init(Observers.<T>empty());
    }

    public void init(@NonNull Action1<T> onNext) {
        init(Observers.create(onNext));
    }

    public void init(@NonNull Action1<T> onNext, @NonNull Action1<Throwable> onError) {
        init(Observers.create(onNext, onError));
    }

    public void init(@NonNull Action1<T> onNext, @NonNull Action1<Throwable> onError, @NonNull Action0 onComplete) {
        init(Observers.create(onNext, onError, onComplete));
    }

    public void init(@NonNull Observer<T> observer) {
        final LoaderManager lm = mLmRef.get();
        if (lm != null) {
            synchronized (LoaderManager.class) {
                lm.initLoader(mLoaderId, Bundle.EMPTY, new RxLcImpl<>(mContext, mObservable, observer, mContentUri));
            }
        }
    }

    public void restart() {
        restart(Observers.<T>empty());
    }

    public void restart(@NonNull Action1<T> onNext) {
        restart(Observers.create(onNext));
    }

    public void restart(@NonNull Action1<T> onNext, @NonNull Action1<Throwable> onError) {
        restart(Observers.create(onNext, onError));
    }

    public void restart(@NonNull Action1<T> onNext, @NonNull Action1<Throwable> onError, @NonNull Action0 onComplete) {
        restart(Observers.create(onNext, onError, onComplete));
    }

    public void restart(@NonNull Observer<T> observer) {
        final LoaderManager lm = mLmRef.get();
        if (lm != null) {
            synchronized (LoaderManager.class) {
                lm.restartLoader(mLoaderId, Bundle.EMPTY, new RxLcImpl<>(mContext, mObservable, observer, mContentUri));
            }
        }
    }

    @NonNull
    public static <T> Observable.Transformer<T, T> buildLoaderTransformer(final Context context, final LoaderManager loaderManager, final int loaderId) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(final Observable<T> sourceObservable) {
                return Observable.create(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        WeakSubscriber<T> weakSubscriber = new WeakSubscriber<>(subscriber);
                        RxLoader.create(context, loaderManager, loaderId, sourceObservable).restart(weakSubscriber);
                    }
                });
            }
        };
    }

}
