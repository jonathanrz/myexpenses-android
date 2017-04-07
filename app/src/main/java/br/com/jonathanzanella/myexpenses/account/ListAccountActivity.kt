package br.com.jonathanzanella.myexpenses.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.BaseActivity
import butterknife.Bind

class ListAccountActivity : BaseActivity(), AccountAdapterCallback {

    @Bind(R.id.act_account_list)
    lateinit var accounts: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_account)
        setTitle(R.string.select_account_title)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val adapter = AccountAdapter()
        adapter.setCallback(this)
        adapter.setFormat(AccountAdapter.Format.LIST)

        accounts.adapter = adapter
        accounts.setHasFixedSize(true)
        accounts.layoutManager = GridLayoutManager(this, 2)
        accounts.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?
    }

    override fun onAccountSelected(account: Account) {
        val i = Intent()
        i.putExtra(KEY_ACCOUNT_SELECTED_UUID, account.getUuid())
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    companion object {
        val KEY_ACCOUNT_SELECTED_UUID = "KeyAccountSelectUuid"
    }
}
