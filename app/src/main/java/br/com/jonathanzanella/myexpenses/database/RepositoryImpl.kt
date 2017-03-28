package br.com.jonathanzanella.myexpenses.database

import android.content.Context
import android.database.SQLException
import android.util.Log
import br.com.jonathanzanella.myexpenses.Environment
import br.com.jonathanzanella.myexpenses.log.Log.warning
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import java.util.*

class RepositoryImpl<T : UnsyncModel>(ctx: Context) : Repository<T> {
    val databaseHelper: DatabaseHelper

    init {
        this.databaseHelper = DatabaseHelper(ctx)
    }

    override fun find(table: Table<T>, uuid: String?): T? {
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
            var data: T? = null
            Log.i("teste", "found " + c.count + " for " + uuid)
            if (c.count > 0) {
                c.moveToFirst()
                data = table.fill(c)
            }
            db.close()
            return data
        }
    }

    override fun querySingle(table: Table<T>, where: Where?): T? {
        return query(table, where, true).singleOrNull()
    }

    override fun query(table: Table<T>, where: Where?): List<T> {
        return query(table, where, false)
    }

    override fun query(table: Table<T>, where: Where?, single: Boolean): List<T> {
        val db = databaseHelper.readableDatabase
        val select = where?.query()
        db.query(
                table.name,
                table.projection,
                select?.where,
                select?.parameters,
                null,
                null,
                where?.orderBy()?.toString()
        ).use { c ->
            val sources = ArrayList<T>()
            c.moveToFirst()
            while (!c.isAfterLast) {
                sources.add(table.fill(c))
                if(single)
                    break
                c.moveToNext()
            }
            db.close()
            return sources
        }
    }

    override fun userData(table: Table<T>): List<T> {
        return query(table, Where(Fields.USER_UUID).eq(Environment.CURRENT_USER_UUID))
    }

    override fun unsync(table: Table<T>): List<T> {
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
            while (!c.isAfterLast) {
                sources.add(table.fill(c))
                c.moveToNext()
            }
            db.close()
            return sources
        }
    }

    override fun greaterUpdatedAt(table: Table<T>): Long {
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
            var data: Long = 0
            if(c.count > 0) {
                c.moveToFirst()
                data = table.fill(c).updatedAt
            }
            db.close()
            return data
        }
    }

    override fun saveAtDatabase(table: Table<T>, data: T) {
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

    override fun syncAndSave(table: Table<T>, unsyncModel: T) {
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
