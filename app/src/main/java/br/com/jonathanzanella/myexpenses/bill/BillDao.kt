package br.com.jonathanzanella.myexpenses.bill

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import br.com.jonathanzanella.myexpenses.source.Source
import io.reactivex.Flowable

@Dao
interface BillDao {
    @Query("SELECT * FROM Bill ORDER BY name")
    fun all(): Flowable<List<Bill>>

    @Query("SELECT * FROM Bill WHERE sync = 0 ORDER BY name")
    fun unsync(): Flowable<List<Bill>>

    @Query("SELECT * FROM Bill WHERE uuid = :uuid")
    fun find(uuid: String): Flowable<Bill>

    @Query("SELECT * FROM Bill ORDER BY updatedAt DESC LIMIT 1")
    fun greaterUpdatedAt(): Flowable<Bill>

    @Query("SELECT * FROM Bill WHERE initDate >= :initDate AND endDate <= :endDate")
    fun monthly(initDate: Long, endDate: Long): Flowable<List<Bill>>

    @Query("DELETE FROM Bill")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAtDatabase(bill: Bill): Long
}