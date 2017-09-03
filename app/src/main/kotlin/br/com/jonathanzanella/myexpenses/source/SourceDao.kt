package br.com.jonathanzanella.myexpenses.source

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface SourceDao {
    @Query("SELECT * FROM Source order by name")
    fun all(): Flowable<List<Source>>

    @Query("SELECT * FROM Source where sync = 0 order by name")
    fun unsync(): Flowable<List<Source>>

    @Query("SELECT * FROM Source where uuid = :uuid")
    fun find(uuid: String): Flowable<List<Source>>

    @Query("SELECT * FROM Source order by updatedAt DESC limit 1")
    fun greaterUpdatedAt(): Flowable<List<Source>>

    @Query("DELETE FROM Source")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAtDatabase(source: Source): Long
}
