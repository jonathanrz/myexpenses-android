package br.com.jonathanzanella.myexpenses.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.anko.TemplateToolbar
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import br.com.jonathanzanella.myexpenses.views.anko.toolbarTemplate
import org.jetbrains.anko.*
import org.joda.time.DateTime

class ListAccountActivity : AppCompatActivity(), AccountAdapterCallback {
    private val ui = ListAccountActivityUi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui.setContentView(this)

        ui.toolbar.title = getString(R.string.select_account_title)
        ui.toolbar.setup(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val adapter = AccountAdapter(DateTime.now())
        adapter.setCallback(this)
        adapter.setFormat(AccountAdapter.Format.LIST)

        ui.accounts.adapter = adapter
        ui.accounts.setHasFixedSize(true)
        ui.accounts.layoutManager = GridLayoutManager(this, 2)
        ui.accounts.itemAnimator = DefaultItemAnimator()
    }

    override fun onAccountSelected(account: Account) {
        val i = Intent()
        i.putExtra(KEY_ACCOUNT_SELECTED_UUID, account.uuid)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    companion object {
        val KEY_ACCOUNT_SELECTED_UUID = "KeyAccountSelectUuid"
    }
}

private class ListAccountActivityUi : AnkoComponent<ListAccountActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var accounts: RecyclerView

    override fun createView(ui: AnkoContext<ListAccountActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}
            accounts = recyclerView { id = R.id.act_account_list }
        }
    }.applyRecursively(::applyTemplateViewStyles)
}
