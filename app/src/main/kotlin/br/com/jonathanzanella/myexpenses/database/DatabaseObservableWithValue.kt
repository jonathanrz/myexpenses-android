package br.com.jonathanzanella.myexpenses.database

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.jetbrains.anko.doAsync

class DatabaseObservableWithValue<in V, T>(private val generateData: (V) -> T) {
    private var bsMap = HashMap<V, BehaviorSubject<T>>()

    fun cache(value: V): Observable<T> {
        var bs = bsMap[value]
        if(bs == null) {
            bs = BehaviorSubject.create()
            bsMap.put(value, bs)
            emit(value)
        }
        return bs!!.replay(1).autoConnect()
    }

    fun emit(value: V) {
        doAsync { bsMap[value]?.onNext(generateData(value)) }
    }

    fun emit() {
        bsMap.entries.forEach { if (it.value.hasObservers()) { emit(it.key) } }
    }
}