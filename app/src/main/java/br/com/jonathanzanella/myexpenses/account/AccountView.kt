package br.com.jonathanzanella.myexpenses.account

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.BaseView
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton

class AccountView : BaseView {

    private val ui = AccountViewUI()
    private var adapter = AccountAdapter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        ui.accounts.adapter = adapter
        ui.accounts.layoutManager = GridLayoutManager(context, 2)
        ui.accounts.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?
        adapter.setFormat(AccountAdapter.Format.LIST)
    }

    override fun init() {
        //TODO: remove when convert BaseView to interface
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ADD_ACCOUNT -> if (resultCode == Activity.RESULT_OK)
                adapter.refreshData()
        }
    }

    override fun refreshData() {
        super.refreshData()

        adapter.refreshData()
        adapter.notifyDataSetChanged()
    }

    companion object {
        val REQUEST_ADD_ACCOUNT = 1003
    }
}

class AccountViewUI: AnkoComponent<AccountView> {
    lateinit var accounts : RecyclerView

    override fun createView(ui: AnkoContext<AccountView>) = with(ui) {
        frameLayout {
            accounts = recyclerView { id = R.id.view_accounts_list }
            floatingActionButton {
                id = R.id.view_accounts_fab
                onClick {
                    val i = Intent(context, EditAccountActivity::class.java)
                    if (ctx is Activity) {
                        (ctx as Activity).startActivityForResult(i, AccountView.REQUEST_ADD_ACCOUNT)
                    } else {
                        ctx.startActivity(i)
                    }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
