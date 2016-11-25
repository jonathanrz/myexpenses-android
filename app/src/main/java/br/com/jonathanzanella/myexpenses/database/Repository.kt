package br.com.jonathanzanella.myexpenses.database

import android.content.Context
import android.database.SQLException
import android.util.Log
import br.com.jonathanzanella.myexpenses.Environment
import br.com.jonathanzanella.myexpenses.log.Log.warning
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import lombok.Getter
import java.util.*

class Repository<T : UnsyncModel>(ctx: Context) {
    @Getter
    val databaseHelper: DatabaseHelper

    init {
        this.databaseHelper = DatabaseHelper(ctx)
    }

    fun find(table: Table<T>, uuid: String?): T? {
        if (uuid == null)
            return null
        val db = databaseHelper.readableDatabase
        val select = Where(Fields.UUID).eq(uuid).query()
        db.query(
                table.name,
                table.projection,
                select.where,
                select.parameters,
                null,
                null,
                null
        ).use { c ->
            if (c.count == 0)
                return null
            c.moveToFirst()
            return table.fill(c)
        }
    }

    fun query(table: Table<T>, where: Where): List<T> {
        val db = databaseHelper.readableDatabase
        val select = where.query()
        db.query(
                table.name,
                table.projection,
                select.where,
                select.parameters,
                null,
                null,
                Fields.NAME.toString()
        ).use { c ->
            val sources = ArrayList<T>()
            c.moveToFirst()
            while (!c.isAfterLast) {
                sources.add(table.fill(c))
                c.moveToNext()
            }
            return sources
        }
    }

    fun userData(table: Table<T>): List<T> {
        return query(table, Where(Fields.USER_UUID).eq(Environment.CURRENT_USER_UUID))
    }

    fun unsync(table: Table<T>): List<T> {
        val db = databaseHelper.readableDatabase
        val select = Where(Fields.SYNC).eq(false).query()
        db.query(
                table.name,
                table.projection,
                select.where,
                select.parameters,
                null,
                null,
                Fields.NAME.toString()
        ).use { c ->
            val sources = ArrayList<T>()
            c.moveToFirst()
            for (i in 0..c.count - 1) {
                c.move(i)
                sources.add(table.fill(c))
            }
            return sources
        }
    }

    fun greaterUpdatedAt(table: Table<T>): Long {
        val db = databaseHelper.readableDatabase
        db.query(
                table.name,
                table.projection,
                null,
                null,
                null,
                null,
                Fields.UPDATED_AT.toString() + " DESC",
                "1"
        ).use { c ->
            c.moveToFirst()
            return table.fill(c).updatedAt
        }
    }

    fun saveAtDatabase(table: Table<T>, data: T) {
        val db = databaseHelper.writableDatabase
        if (data.id == 0L) {
            try {
                val newId = db.insertOrThrow(table.name, null, table.fillContentValues(data))
                data.id = newId
            } catch (e: SQLException) {
                Log.e("Repository", "error inserting the record into the database, error=" + e.message)
                throw e
            }

        } else {
            val select = Where(Fields.ID).eq(data.id).query()
            db.update(table.name, table.fillContentValues(data), select.where, select.parameters)
        }
    }

    fun syncAndSave(table: Table<T>, unsyncModel: T) {
        val unsyncSource = find(table, unsyncModel.uuid)
        if (unsyncSource != null && unsyncSource.id != unsyncModel.id) {
            if (unsyncSource.updatedAt != unsyncModel.updatedAt)
                warning("Source overwritten", unsyncModel.data)
            unsyncModel.id = unsyncSource.id
        }

        unsyncModel.serverId = unsyncModel.serverId
        unsyncModel.createdAt = unsyncModel.createdAt
        unsyncModel.updatedAt = unsyncModel.updatedAt
        unsyncModel.setSync(true)
        saveAtDatabase(table, unsyncModel)
    }
}
