package br.com.jonathanzanella.myexpenses.ui.account

import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AccountRepositoryTest {
    lateinit var dataSource: AccountDataSource

    @Before
    @Throws(Exception::class)
    fun setUp() {
        App.resetDatabase()

        dataSource = App.getApp().appComponent.accountDataSource()
    }

    @Test
    @Throws(Exception::class)
    fun can_save_account() {
        val account = Account()
        account.name = "test"
        var asserted = false
        val disposable = dataSource.save(account).subscribe {
            assertThat(account.id, `is`(not(0L)))
            assertThat<String>(account.uuid, `is`(not("")))

            asserted = true
        }

        Thread.sleep(500)

        disposable.dispose()
        assertTrue(asserted)
    }

    @Test
    @Throws(Exception::class)
    fun can_load_saved_account() {
        val account = Account()
        account.name = "test"
        assertTrue(dataSource.save(account).blockingFirst().isValid)

        val loadedAccount = dataSource.find(account.uuid!!).blockingFirst()
        assertThat<String>(loadedAccount.uuid, `is`<String>(account.uuid))
    }
}