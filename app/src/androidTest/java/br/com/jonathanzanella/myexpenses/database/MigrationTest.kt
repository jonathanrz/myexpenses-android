package br.com.jonathanzanella.myexpenses.database

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory
import android.arch.persistence.room.testing.MigrationTestHelper
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@Suppress("IllegalIdentifier")
class MigrationTest {
    @Rule
    @JvmField
    val testHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                                            MyDatabase::class.java.canonicalName,
                                            FrameworkSQLiteOpenHelperFactory())

    @Test
    fun `migration from version 1 to 2 and check if contains the correct data`() {
        val db = testHelper.createDatabase(DB_NAME, 1)

        val values = ContentValues()
        values.put("id", 1)
        values.put("name", "test")
        values.put("uuid", "uuid")
        values.put("balance", 0)
        values.put("sync", false)
        values.put("accountToPayCreditCard", false)
        values.put("accountToPayBills", false)
        values.put("showInResume", false)
        values.put("serverId", "")
        values.put("createdAt", 0L)
        values.put("updatedAt", 0L)

        db.insert("Account", SQLiteDatabase.CONFLICT_ABORT, values)

        testHelper.runMigrationsAndValidate(DB_NAME, 2, true, MyDatabase.MIGRATION_1_2)

        val accountDao = MyDatabase.buildDatabase(InstrumentationRegistry.getInstrumentation().targetContext).accountDao()

        val accounts = accountDao.find("uuid")

        assertThat(accounts.size, `is`(1))
        assertThat(accounts[0].id, `is`(1L))
        assertThat(accounts[0].name, `is`("test"))
        assertThat(accounts[0].uuid, `is`("uuid"))
        assertThat(accounts[0].removed, `is`(false))
    }
}