package br.com.jonathanzanella.myexpenses.account

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.FilterableView
import br.com.jonathanzanella.myexpenses.views.TabableView
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton
import org.joda.time.DateTime

class AccountView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), FilterableView, TabableView {
    override var filter: String = ""

    private val ui = AccountViewUI()
    private var adapter = AccountAdapter(DateTime.now())

    init {
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        ui.accounts.adapter = adapter
        ui.accounts.layoutManager = GridLayoutManager(context, 2)
        ui.accounts.itemAnimator = DefaultItemAnimator()
        adapter.setFormat(AccountAdapter.Format.LIST)
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
