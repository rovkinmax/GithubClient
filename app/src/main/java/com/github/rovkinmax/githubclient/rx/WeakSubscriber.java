package com.github.rovkinmax.githubclient.rx;

import android.support.annotation.NonNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import rx.Subscriber;

/**
 * @author Rovkin Max
 */
class WeakSubscriber<T> extends Subscriber<T> {

    private final Reference<Subscriber<? super T>> mSubscriberRef;

    public WeakSubscriber(@NonNull Subscriber<? super T> subscriber) {
        mSubscriberRef = new WeakReference<Subscriber<? super T>>(subscriber);
    }

    @Override
    public void onNext(T o) {
        final Subscriber<? super T> subscriber = mSubscriberRef.get();
        if (subscriber != null && !subscriber.isUnsubscribed()) {
            subscriber.onNext(o);
        }
    }

    @Override
    public void onError(Throwable e) {
        final Subscriber<? super T> subscriber = mSubscriberRef.get();
        if (subscriber != null && !subscriber.isUnsubscribed()) {
            subscriber.onError(e);
        }
    }

    @Override
    public void onCompleted() {
        final Subscriber<? super T> subscriber = mSubscriberRef.get();
        if (subscriber != null && !subscriber.isUnsubscribed()) {
            subscriber.onCompleted();
        }
    }

}
