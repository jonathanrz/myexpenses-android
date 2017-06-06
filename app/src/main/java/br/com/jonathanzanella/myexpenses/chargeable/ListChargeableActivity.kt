package br.com.jonathanzanella.myexpenses.chargeable

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountAdapter
import br.com.jonathanzanella.myexpenses.account.AccountAdapterCallback
import br.com.jonathanzanella.myexpenses.card.Card
import br.com.jonathanzanella.myexpenses.card.CardAdapter
import br.com.jonathanzanella.myexpenses.card.CardAdapterCallback
import br.com.jonathanzanella.myexpenses.views.anko.*
import org.jetbrains.anko.*

class ListChargeableActivity : AppCompatActivity(), AccountAdapterCallback, CardAdapterCallback {
    private val ui = ListChargeableActivityUi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        ui.toolbar.title = getString(R.string.select_chargeable_title)
        ui.toolbar.setup(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        initAccounts()
        initCreditCards()
    }

    private fun initAccounts() {
        val adapter = AccountAdapter()
        adapter.setCallback(this)
        adapter.setFormat(AccountAdapter.Format.LIST)

        ui.accounts.adapter = adapter
        ui.accounts.setHasFixedSize(true)
        ui.accounts.layoutManager = GridLayoutManager(this, 2)
        ui.accounts.itemAnimator = DefaultItemAnimator()
    }

    private fun initCreditCards() {
        val adapter = CardAdapter()
        adapter.setCallback(this)
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                adapter.loadData()
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                ui.cards.adapter = adapter
                ui.cards.setHasFixedSize(true)
                ui.cards.layoutManager = GridLayoutManager(this@ListChargeableActivity, 2)
                ui.cards.itemAnimator = DefaultItemAnimator()
            }
        }.execute()
    }

    override fun onAccountSelected(account: Account) {
        val i = Intent()
        i.putExtra(KEY_CHARGEABLE_SELECTED_UUID, account.uuid)
        i.putExtra(KEY_CHARGEABLE_SELECTED_TYPE, account.chargeableType)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    override fun onCard(card: Card) {
        val i = Intent()
        i.putExtra(KEY_CHARGEABLE_SELECTED_UUID, card.uuid)
        i.putExtra(KEY_CHARGEABLE_SELECTED_TYPE, card.chargeableType)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    companion object {
        val KEY_CHARGEABLE_SELECTED_UUID = "KeyChargeableSelectUuid"
        val KEY_CHARGEABLE_SELECTED_TYPE = "KeyChargeableSelectType"
    }
}

private class ListChargeableActivityUi : AnkoComponent<ListChargeableActivity> {
    lateinit var toolbar : TemplateToolbar
    lateinit var accounts: RecyclerView
    lateinit var cards: RecyclerView

    override fun createView(ui: AnkoContext<ListChargeableActivity>) = with(ui) {
        verticalLayout {
            toolbar = toolbarTemplate {}

            headerTitle { text = resources.getString(R.string.accounts) }
            accounts = recyclerView { id = R.id.act_chargeable_list_accounts }.lparams(height = 0, weight = 1f)

            headerTitle { text = resources.getString(R.string.cards) }.lparams { topMargin = resources.getDimensionPixelSize(R.dimen.default_spacing) }
            cards = recyclerView { id = R.id.act_chargeable_list_cards }.lparams(height = 0, weight = 1f)
        }
    }.applyRecursively(::applyTemplateViewStyles)
}