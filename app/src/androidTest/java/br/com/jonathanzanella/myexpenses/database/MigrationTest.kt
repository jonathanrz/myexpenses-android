package br.com.jonathanzanella.myexpenses.database

import android.support.test.runner.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@Suppress("IllegalIdentifier")
class MigrationTest {
//    @Rule @JvmField
//    val testHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
//                                            MyDatabase::class.java.canonicalName,
//                                            FrameworkSQLiteOpenHelperFactory())
//
//    @Test
//    fun `migration from version 1 to 2 and check if contains the correct data`() {
//        val db = testHelper.createDatabase(DB_NAME, 1)
//
//        val values = ContentValues()
//        values.put("id", 1)
//        values.put("name", "test")
//        values.put("uuid", "uuid")
//
//        db.insert("Account", SQLiteDatabase.CONFLICT_ABORT, values)
//
//        testHelper.runMigrationsAndValidate(DB_NAME, 2, true, MyDatabase.MIGRATION_1_2)
//
//        val accountDao = MyDatabase.buildDatabase(InstrumentationRegistry.getInstrumentation().targetContext).accountDao()
//
//        val accounts = accountDao.find("uuid")
//
//        assertThat(accounts.size, `is`(1))
//        assertThat(accounts[0].id, `is`(1L))
//        assertThat(accounts[0].name, `is`("test"))
//        assertThat(accounts[0].uuid, `is`("uuid"))
//        assertThat(accounts[0].removed, `is`(false))
//    }
}