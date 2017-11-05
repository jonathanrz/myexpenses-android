package br.com.jonathanzanella.myexpenses.account

import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import br.com.jonathanzanella.TestApp
import br.com.jonathanzanella.myexpenses.App
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@SmallTest
class AccountRepositoryTest {
    @Inject
    lateinit var dataSource: AccountDataSource

    @Before
    @Throws(Exception::class)
    fun setUp() {
        TestApp.getTestComponent().inject(this)
        App.resetDatabase()
    }

    @Test
    @Throws(Exception::class)
    fun can_save_account() {
        val account = Account()
        account.name = "test"
        dataSource.save(account).subscribe {
            assertThat(account.id, `is`(not(0L)))
            assertThat<String>(account.uuid, `is`(not("")))
        }
    }

    @Test
    @Throws(Exception::class)
    fun can_load_saved_account() {
        val account = Account()
        account.name = "test"
        dataSource.save(account).subscribe {
            dataSource.find(account.uuid!!).subscribe {
                assertThat<String>(it.uuid, `is`<String>(account.uuid))
            }
        }
    }
}