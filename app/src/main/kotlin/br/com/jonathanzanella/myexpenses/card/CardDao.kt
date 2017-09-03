package br.com.jonathanzanella.myexpenses.card

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface CardDao {
    @Query("SELECT * FROM Card ORDER BY name")
    fun all(): Flowable<List<Card>>

    @Query("SELECT * FROM Card WHERE sync = 0 ORDER BY name")
    fun unsync(): Flowable<List<Card>>

    @Query("SELECT * FROM Card WHERE type = :cardType ORDER BY name")
    fun cards(cardType: String): Flowable<List<Card>>

    @Query("SELECT * FROM Card WHERE type = :cardType AND accountUuid = :accountUuid ORDER BY name LIMIT 1")
    fun accountCard(cardType: String, accountUuid: String): Flowable<List<Card>>

    @Query("SELECT * FROM Card WHERE uuid = :uuid")
    fun find(uuid: String): Flowable<List<Card>>

    @Query("SELECT * FROM Card ORDER BY updatedAt DESC LIMIT 1")
    fun greaterUpdatedAt(): Flowable<List<Card>>

    @Query("DELETE FROM Card")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAtDatabase(card: Card): Long
}
