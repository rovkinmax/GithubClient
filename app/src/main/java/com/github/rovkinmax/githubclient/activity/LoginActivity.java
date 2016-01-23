package com.github.rovkinmax.githubclient.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.github.rovkinmax.githubclient.R;
import com.github.rovkinmax.githubclient.api.AuthApi;
import com.github.rovkinmax.githubclient.model.api.Authorization;
import com.github.rovkinmax.githubclient.util.AuthUtil;
import com.github.rovkinmax.rxretain.EmptySubscriber;
import com.github.rovkinmax.rxretain.RetainFactory;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * @author Rovkin Max
 */
public class LoginActivity extends MyAccountAuthenticatorActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginContentView;
    private Button mEmailSignInButton;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.et_login);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginContentView = findViewById(R.id.login_content);
        mProgressView = findViewById(R.id.login_progress);

        setupBindings();
        showProgressIfLoading();
        RxView.clicks(mEmailSignInButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        attemptLogin();
                    }
                });
    }

    private void setupBindings() {
        Observable.combineLatest(buildEmailObservable(), buildPassObservable(), new Func2<Boolean, Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean isValidEmail, Boolean isValidPass) {
                return isValidEmail && isValidPass;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isValidCredentials) {
                mEmailSignInButton.setEnabled(isValidCredentials);
            }
        });
    }

    private void showProgressIfLoading() {
        RetainFactory.create(getFragmentManager(), createAuthObservable(), new EmptySubscriber<Authorization>() {
            @Override
            public void onStart() {
                showProgress(true);
            }
        });
    }


    private Observable<Boolean> buildEmailObservable() {
        return RxTextView.textChanges(mEmailView)
                .concatMap(new Func1<CharSequence, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(CharSequence charSequence) {
                        return Observable.just(isEmailValid(charSequence.toString()));
                    }
                });
    }

    @NonNull
    private Observable<Boolean> buildPassObservable() {
        return RxTextView.textChanges(mPasswordView)
                .concatMap(new Func1<CharSequence, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(CharSequence charSequence) {
                        return Observable.just(isPasswordValid(charSequence.toString()));
                    }
                });
    }

    private Observable<Authorization> createAuthObservable() {
        return AuthApi.auth(this, mEmailView.getText().toString(), mPasswordView.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void attemptLogin() {
        RetainFactory.restart(getFragmentManager(), createAuthObservable())
                .subscribe(new EmptySubscriber<Authorization>() {
                    @Override
                    public void onStart() {
                        showProgress(true);
                    }

                    @Override
                    public void onNext(Authorization authorization) {
                        showProgress(false);
                        saveAccount();
                        startMainActivity();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        showProgress(false);
                    }
                });
    }

    private void saveAccount() {
        AccountManager accountManager = AccountManager.get(this);
        Account account = AuthUtil.getUserAccount(this);
        if (account != null) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, accountManager.peekAuthToken(account, account.type));

            setAccountAuthenticatorResult(result);
            setResult(RESULT_OK);
        }
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return !TextUtils.isEmpty(email);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void showProgress(final boolean show) {
        mLoginContentView.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}

