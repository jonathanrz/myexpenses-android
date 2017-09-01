package br.com.jonathanzanella.myexpenses.expense

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM Expense ORDER BY date")
    fun all(): Flowable<List<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 ORDER BY date")
    fun monthly(initDate: Long, endDate: Long): Flowable<List<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND chargedNextMonth = 0 ORDER BY date")
    fun currentMonth(initDate: Long, endDate: Long): Flowable<MutableList<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND chargedNextMonth = 0 AND chargeableUuid = :chargeableUuid ORDER BY date")
    fun currentMonth(initDate: Long, endDate: Long, chargeableUuid: String): Flowable<MutableList<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND chargedNextMonth = 0 AND ignoreInOverview = 0 ORDER BY date")
    fun overviewCurrentMonth(initDate: Long, endDate: Long): Flowable<MutableList<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND chargedNextMonth = 0 AND ignoreInOverview = 0 AND chargeableUuid = :chargeableUuid ORDER BY date")
    fun overviewCurrentMonth(initDate: Long, endDate: Long, chargeableUuid: String): Flowable<MutableList<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND chargedNextMonth = 0 AND ignoreInResume = 0 AND chargeableType <> :chargeableToIgnore ORDER BY date")
    fun resumeCurrentMonth(initDate: Long, endDate: Long, chargeableToIgnore: String): Flowable<MutableList<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND chargedNextMonth = 0 AND charged = 0 AND chargeableUuid = :chargeableUuid ORDER BY date")
    fun unchargedCurrentMonth(initDate: Long, endDate: Long, chargeableUuid: String): Flowable<MutableList<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND chargedNextMonth <> 0 ORDER BY date")
    fun nextMonth(initDate: Long, endDate: Long): Flowable<MutableList<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND chargedNextMonth <> 0 AND chargeableUuid = :chargeableUuid ORDER BY date")
    fun nextMonth(initDate: Long, endDate: Long, chargeableUuid: String): Flowable<MutableList<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND chargedNextMonth <> 0 AND ignoreInOverview = 0 ORDER BY date")
    fun overviewNextMonth(initDate: Long, endDate: Long): Flowable<MutableList<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND chargedNextMonth <> 0 AND ignoreInOverview = 0 AND chargeableUuid = :chargeableUuid ORDER BY date")
    fun overviewNextMonth(initDate: Long, endDate: Long, chargeableUuid: String): Flowable<MutableList<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND chargedNextMonth <> 0 AND ignoreInResume = 0 AND chargeableType <> :chargeableToIgnore ORDER BY date")
    fun resumeNextMonth(initDate: Long, endDate: Long, chargeableToIgnore: String): Flowable<MutableList<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND chargedNextMonth <> 0 AND charged = 0 AND chargeableUuid = :chargeableUuid ORDER BY date")
    fun unchargedNextMonth(initDate: Long, endDate: Long, chargeableUuid: String): Flowable<MutableList<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND chargeableUuid = :chargeableUuid AND removed = 0 ORDER BY date")
    fun monthly(initDate: Long, endDate: Long, chargeableUuid: String): Flowable<List<Expense>>

    @Query("SELECT * FROM Expense WHERE date >= :initDate AND date <= :endDate AND removed = 0 AND ignoreInResume = 0 ORDER BY date")
    fun resume(initDate: Long, endDate: Long): Flowable<List<Expense>>

    @Query("SELECT * FROM Expense WHERE sync = 0 ORDER BY date")
    fun unsync(): Flowable<List<Expense>>

    @Query("SELECT * FROM Expense WHERE uuid = :uuid")
    fun find(uuid: String): Flowable<List<Expense>>

    @Query("SELECT * FROM Expense ORDER BY updatedAt DESC LIMIT 1")
    fun greaterUpdatedAt(): Flowable<List<Expense>>

    @Query("DELETE FROM Expense")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAtDatabase(expense: Expense): Long
}