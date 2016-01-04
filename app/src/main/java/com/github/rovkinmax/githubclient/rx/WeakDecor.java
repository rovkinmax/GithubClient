package com.github.rovkinmax.githubclient.rx;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Rovkin Max
 */
public class WeakDecor<T> implements Observable.Transformer<T, T> {

    private WeakDecor() {
    }

    public static <T> WeakDecor<T> create() {
        return new WeakDecor<>();
    }

    @Override
    public Observable<T> call(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .lift(new Observable.Operator<T, T>() {
                    @Override
                    public Subscriber<? super T> call(Subscriber<? super T> subscriber) {
                        return new WeakSubscriber<>(subscriber);
                    }
                });
    }

}
