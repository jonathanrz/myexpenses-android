package br.com.jonathanzanella.myexpenses.extensions

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.fromComputationToMainThread(): Observable<T> =
        this.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.fromIoToComputation(): Observable<T> =
        this.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation())
