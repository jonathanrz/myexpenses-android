package br.com.jonathanzanella.myexpenses.database

import br.com.jonathanzanella.myexpenses.extensions.fromIoToComputation
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class DatabaseObservable<T>(private val generateData: () -> T) {
    private var bs: BehaviorSubject<T>? = null

    fun cache(): Observable<T> {
        if(bs == null) {
            bs = BehaviorSubject.create()
            emit()
        }
        return bs!!.replay(1).autoConnect()
    }

    fun emit() {
        Observable
            .fromCallable { generateData() }
            .fromIoToComputation()
            .doOnError { e -> bs?.onError(e) }
            .subscribe { bs?.onNext(it) }
    }
}
