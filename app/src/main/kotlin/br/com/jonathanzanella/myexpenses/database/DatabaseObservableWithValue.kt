package br.com.jonathanzanella.myexpenses.database

import br.com.jonathanzanella.myexpenses.extensions.fromIoToComputation
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

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

    private fun emit(value: V) {
        Observable
                .fromCallable { generateData(value) }
                .fromIoToComputation()
                .doOnError { e -> bsMap[value]?.onError(e) }
                .subscribe { bsMap[value]?.onNext(it) }
    }

    fun emit() {
        bsMap.entries.forEach { if (it.value.hasObservers()) { emit(it.key) } }
    }
}