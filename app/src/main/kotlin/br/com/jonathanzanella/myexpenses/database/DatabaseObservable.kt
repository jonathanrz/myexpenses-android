package br.com.jonathanzanella.myexpenses.database

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.jetbrains.anko.doAsync

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
        doAsync { bs?.onNext(generateData()) }
    }
}
