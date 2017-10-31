package br.com.jonathanzanella.myexpenses.extensions

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Flowable<T>.fromIOToMainThread(): Flowable<T> =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.fromIOToMainThread(): Observable<T> =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
