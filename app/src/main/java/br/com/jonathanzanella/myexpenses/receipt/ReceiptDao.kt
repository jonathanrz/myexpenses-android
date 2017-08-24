package br.com.jonathanzanella.myexpenses.receipt

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM Receipt ORDER BY name")
    fun all(): Flowable<List<Receipt>>

    @Query("SELECT * FROM Receipt WHERE date >= :initDate AND date <= :endDate AND isRemoved = 0 ORDER BY name")
    fun monthly(initDate: Long, endDate: Long): Flowable<List<Receipt>>

    @Query("SELECT * FROM Receipt WHERE date >= :initDate AND date <= :endDate AND accountUuid = :accountUuid AND isRemoved = 0 ORDER BY name")
    fun monthly(initDate: Long, endDate: Long, accountUuid: String): Flowable<List<Receipt>>

    @Query("SELECT * FROM Receipt WHERE date >= :initDate AND date <= :endDate AND isRemoved = 0 AND isIgnoreInResume = 0 ORDER BY name")
    fun resume(initDate: Long, endDate: Long): Flowable<List<Receipt>>

    @Query("SELECT * FROM Receipt WHERE sync = 0 ORDER BY name")
    fun unsync(): Flowable<List<Receipt>>

    @Query("SELECT * FROM Receipt WHERE uuid = :uuid")
    fun find(uuid: String): Flowable<Receipt>

    @Query("SELECT * FROM Receipt ORDER BY updatedAt DESC LIMIT 1")
    fun greaterUpdatedAt(): Flowable<Receipt>

    @Query("DELETE FROM Receipt")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAtDatabase(receipt: Receipt): Long
}