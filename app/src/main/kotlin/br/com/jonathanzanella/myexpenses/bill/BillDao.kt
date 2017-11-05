package br.com.jonathanzanella.myexpenses.bill

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface BillDao {
    @Query("SELECT * FROM Bill ORDER BY name")
    fun all(): List<Bill>

    @Query("SELECT * FROM Bill WHERE sync = 0 ORDER BY name")
    fun unsync(): List<Bill>

    @Query("SELECT * FROM Bill WHERE uuid = :uuid")
    fun find(uuid: String): Bill?

    @Query("SELECT * FROM Bill ORDER BY updatedAt DESC LIMIT 1")
    fun greaterUpdatedAt(): List<Bill>

    @Query("SELECT * FROM Bill WHERE initDate <= :date AND endDate >= :date")
    fun monthly(date: Long): List<Bill>

    @Query("DELETE FROM Bill")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAtDatabase(bill: Bill): Long
}
