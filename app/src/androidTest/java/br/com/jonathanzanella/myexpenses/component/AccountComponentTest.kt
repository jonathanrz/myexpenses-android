package br.com.jonathanzanella.myexpenses.component

import android.arch.persistence.room.Room
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import br.com.jonathanzanella.myexpenses.account.*
import br.com.jonathanzanella.myexpenses.database.MyDatabase
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import junit.framework.Assert.assertTrue
import junit.framework.Assert.fail
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountComponentTest {
    private lateinit var db: MyDatabase
    private lateinit var dataSource: AccountDataSource
    private lateinit var presenter: AccountPresenter

    private val view = object: AccountContract.EditView {
        override val context: Context
            get() = InstrumentationRegistry.getTargetContext()

        override fun setTitle(string: Int) {}
        override fun setTitle(string: String) {}
        override fun showAccount(account: Account) {}
        override fun fillAccount(account: Account): Account = account
        override fun finishView() {}
        override fun showError(error: ValidationError) {}
    }

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(context, MyDatabase::class.java).build()
        dataSource = AccountRepository(db.accountDao())
        presenter = AccountPresenter(dataSource)
        presenter.attachView(view)
    }

    @Test
    fun deletedAccountIsNotReturnedInAllQuery() {
        Log.i("logDoTeste", "init")
        val account = AccountBuilder().build()
        assertTrue(dataSource.save(account).blockingFirst().isValid)

        Log.i("logDoTeste", "saved")

        Thread.sleep(500)

        var subscribeCalls = 0
        val disposable = dataSource.all().subscribe {
            subscribeCalls++

            Log.i("logDoTeste", "subscription called time ${subscribeCalls} size ${it.size}")

            when(subscribeCalls) {
                1 -> assertThat(it.size, Matchers.`is`(1))
                2 -> assertThat(it.size, Matchers.`is`(0))
                else -> fail()
            }
        }

        Log.i("logDoTeste", "subscribed")
        presenter.loadAccount(account.uuid!!).blockingFirst()
        Log.i("logDoTeste", "loaded")
        assertTrue(presenter.delete().blockingFirst().isValid)
        Log.i("logDoTeste", "deleted")

        Thread.sleep(500)

        Log.i("logDoTeste", "waited")

        assertThat(subscribeCalls, `is`(2))

        Log.i("logDoTeste", "validated")

        disposable.dispose()
    }

    @After
    fun closeDb() {
        db.close()
    }
}