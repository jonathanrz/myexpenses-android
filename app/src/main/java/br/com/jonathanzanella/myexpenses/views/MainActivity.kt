package br.com.jonathanzanella.myexpenses.views

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.AccountView
import br.com.jonathanzanella.myexpenses.bill.BillView
import br.com.jonathanzanella.myexpenses.card.CardView
import br.com.jonathanzanella.myexpenses.expense.ExpenseView
import br.com.jonathanzanella.myexpenses.log.LogsView
import br.com.jonathanzanella.myexpenses.overview.OverviewExpensesView
import br.com.jonathanzanella.myexpenses.receipt.ReceiptView
import br.com.jonathanzanella.myexpenses.resume.ResumeView
import br.com.jonathanzanella.myexpenses.source.SourceView
import br.com.jonathanzanella.myexpenses.sync.SyncView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {
    private lateinit var currentView: View
    private var filter = ""
    private var selectedItem = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storeBundle(savedInstanceState)
        storeBundle(intent.extras)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setSupportActionBar(toolbar as Toolbar?)

        val drawerToggle = ActionBarDrawerToggle(
                this, drawer, toolbar as Toolbar?,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        navigationView!!.setNavigationItemSelectedListener(this)

        @Suppress("DEPRECATION")
        drawer!!.setDrawerListener(drawerToggle)
        drawerToggle.syncState()

        if (selectedItem == -1)
            selectMenuItem(R.id.menu_resume)
    }

    fun storeBundle(extras: Bundle?) {
        if (extras != null && extras.containsKey(KEY_SELECTED_ITEM))
            selectMenuItem(extras.getInt(KEY_SELECTED_ITEM))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_ITEM, selectedItem)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        (0..content!!.childCount - 1)
                .map { content!!.getChildAt(it) }
                .forEach { (it as? BaseView)?.onActivityResult(requestCode, resultCode, data) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(this)

        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        filter = newText
        val view = currentView
        if(view is BaseView)
            view.filter(newText)
        return false
    }

    override fun onResume() {
        super.onResume()

        (0..content.childCount - 1)
                .map { content.getChildAt(it) }
                .filterIsInstance<RefreshableView>()
                .forEach { it.refreshData() }
    }

    private fun addViewToContent(child: View) {
        content.removeAllViews()
        child.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        content!!.addView(child)
        if(child is BaseView) {
            child.setTabs(tabs)
            child.filter(filter)
        }
        currentView = child
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return selectMenuItem(item.itemId)
    }

    private fun selectMenuItem(@IdRes itemId: Int): Boolean {
        selectedItem = itemId
        navigationView!!.setCheckedItem(selectedItem)
        when (itemId) {
            R.id.menu_resume -> {
                addViewToContent(ResumeView(this))
                setTitle(R.string.app_name)
                drawer!!.closeDrawers()
                return true
            }
            R.id.menu_accounts -> {
                addViewToContent(AccountView(this))
                setTitle(R.string.accounts)
                drawer!!.closeDrawers()
                return true
            }
            R.id.menu_sources -> {
                addViewToContent(SourceView(this))
                setTitle(R.string.sources)
                drawer!!.closeDrawers()
                return true
            }
            R.id.menu_credit_card -> {
                addViewToContent(CardView(this))
                setTitle(R.string.credit_card)
                drawer!!.closeDrawers()
                return true
            }
            R.id.menu_receipts -> {
                addViewToContent(ReceiptView(this))
                setTitle(R.string.receipts)
                drawer!!.closeDrawers()
                return true
            }
            R.id.menu_expenses -> {
                addViewToContent(ExpenseView(this))
                setTitle(R.string.expenses)
                drawer!!.closeDrawers()
                return true
            }
            R.id.menu_bills -> {
                addViewToContent(BillView(this))
                setTitle(R.string.bills)
                drawer!!.closeDrawers()
                return true
            }
            R.id.menu_overview -> {
                addViewToContent(OverviewExpensesView(this))
                setTitle(R.string.overview)
                drawer!!.closeDrawers()
                return true
            }
            R.id.menu_sync -> {
                addViewToContent(SyncView(this))
                setTitle(R.string.sync)
                drawer!!.closeDrawers()
                return true
            }
            R.id.menu_log -> {
                addViewToContent(LogsView(this))
                setTitle(R.string.log)
                drawer!!.closeDrawers()
                return true
            }
        }
        selectedItem = -1
        navigationView!!.setCheckedItem(selectedItem)
        return false
    }

    companion object {
        private val KEY_SELECTED_ITEM = "KeySelectedItem"
    }
}
