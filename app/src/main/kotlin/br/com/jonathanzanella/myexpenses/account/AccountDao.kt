package br.com.jonathanzanella.myexpenses.account

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface AccountDao {
    @Query("SELECT * FROM Account ORDER BY name")
    fun all(): Flowable<List<Account>>

    @Query("SELECT * FROM Account WHERE sync = 0 ORDER BY name")
    fun unsync(): Flowable<List<Account>>

    @Query("SELECT * FROM Account WHERE showInResume <> 0 ORDER BY name")
    fun showInResume(): Flowable<List<Account>>

    @Query("SELECT * FROM Account WHERE uuid = :uuid")
    fun find(uuid: String): Flowable<List<Account>>

    @Query("SELECT * FROM Account ORDER BY updatedAt DESC LIMIT 1")
    fun greaterUpdatedAt(): Flowable<List<Account>>

    @Query("DELETE FROM Account")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAtDatabase(account: Account): Long
}