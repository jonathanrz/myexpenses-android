package br.com.jonathanzanella.myexpenses.database

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.jetbrains.anko.doAsync

class DatabaseObjectObservable<in Index, Object>(private val loadObject: (Index) -> Object) {
    private var bsMap: HashMap<Index, BehaviorSubject<Object>> = HashMap()

    fun cache(index: Index): Observable<Object> {
        var bs = bsMap[index]
        if(bs == null) {
            bs = BehaviorSubject.create()
            bsMap.put(index, bs)
            emit(index)
        }
        return bs!!.replay(1).autoConnect()
    }

    fun emit(index: Index) {
        doAsync {
            bsMap[index]?.let {
                try {
                    it.onNext(loadObject(index))
                } catch (e: Exception) {
                    it.onError(e)
                }
            }
        }
    }

    fun clear() {
        bsMap.clear()
    }
}