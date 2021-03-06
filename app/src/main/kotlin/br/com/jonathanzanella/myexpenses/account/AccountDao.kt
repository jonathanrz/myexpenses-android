package br.com.jonathanzanella.myexpenses.account

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface AccountDao {
    @Query("SELECT * FROM Account WHERE removed = 0 ORDER BY name")
    fun all(): List<Account>

    @Query("SELECT * FROM Account WHERE sync = 0 ORDER BY name")
    fun unsync(): List<Account>

    @Query("SELECT * FROM Account WHERE removed = 0 AND showInResume <> 0 ORDER BY name")
    fun showInResume(): List<Account>

    @Query("SELECT * FROM Account WHERE uuid = :uuid")
    fun find(uuid: String): List<Account>

    @Query("SELECT * FROM Account ORDER BY updatedAt DESC LIMIT 1")
    fun greaterUpdatedAt(): List<Account>

    @Query("DELETE FROM Account")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAtDatabase(account: Account): Long
}
