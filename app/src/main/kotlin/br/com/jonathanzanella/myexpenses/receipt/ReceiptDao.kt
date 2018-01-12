package br.com.jonathanzanella.myexpenses.receipt

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM Receipt WHERE removed = 0 ORDER BY date")
    fun all(): Flowable<List<Receipt>>

    @Query("SELECT * FROM Receipt WHERE date >= :initDate AND date <= :endDate AND removed = 0 ORDER BY date")
    fun monthly(initDate: Long, endDate: Long): Flowable<List<Receipt>>

    @Query("SELECT * FROM Receipt WHERE date >= :initDate AND date <= :endDate AND accountUuid = :accountUuid AND removed = 0 ORDER BY date")
    fun monthly(initDate: Long, endDate: Long, accountUuid: String): Single<List<Receipt>>

    @Query("SELECT * FROM Receipt WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND ignoreInResume = 0 ORDER BY date")
    fun resume(initDate: Long, endDate: Long): Flowable<List<Receipt>>

    @Query("SELECT * FROM Receipt WHERE sync = 0 ORDER BY date")
    fun unsync(): Flowable<List<Receipt>>

    @Query("SELECT * FROM Receipt WHERE uuid = :uuid")
    fun find(uuid: String): Flowable<List<Receipt>>

    @Query("SELECT * FROM Receipt ORDER BY updatedAt DESC LIMIT 1")
    fun greaterUpdatedAt(): Flowable<List<Receipt>>

    @Query("DELETE FROM Receipt")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAtDatabase(receipt: Receipt): Long
}
