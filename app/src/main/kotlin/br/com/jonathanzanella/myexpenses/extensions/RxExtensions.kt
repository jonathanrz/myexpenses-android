package br.com.jonathanzanella.myexpenses.extensions

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.fromComputationToMainThread(): Observable<T> =
        this.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.fromIoToComputation(): Observable<T> =
        this.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation())

fun <T> Flowable<T>.fromIoToMainThread(): Flowable<T> =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<List<T>>.toSingleList(): Single<List<T>> =
        this
        .toList()
        .map {
            val newList = ArrayList<T>()
            it.forEach { newList.addAll(it) }
            newList
        }