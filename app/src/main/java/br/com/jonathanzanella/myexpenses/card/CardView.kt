package br.com.jonathanzanella.myexpenses.card

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.annotation.UiThread
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

@UiThread
class CardView : BaseView {

    private var ui = CardViewUI()
    private var adapter = CardAdapter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onAttachedToWindow() {
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        ui.cards.adapter = adapter
        ui.cards.layoutManager = GridLayoutManager(context, 1)
        ui.cards.itemAnimator = DefaultItemAnimator()

        refreshData()
    }

    override fun init() {
        //TODO: remove when convert BaseView to interface
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ADD_CARD -> if (resultCode == Activity.RESULT_OK)
                object : AsyncTask<Void, Void, Void>() {

                    override fun doInBackground(vararg voids: Void): Void? {
                        adapter.loadData()
                        return null
                    }

                    override fun onPostExecute(aVoid: Void?) {
                        super.onPostExecute(aVoid)
                        adapter.notifyDataSetChanged()
                    }
                }.execute()
        }
    }

    override fun refreshData() {
        super.refreshData()

        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                adapter.loadData()
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
                adapter.notifyDataSetChanged()
            }
        }.execute()
    }

    companion object {
        val REQUEST_ADD_CARD = 1005
    }
}

class CardViewUI: AnkoComponent<CardView> {
    lateinit var cards : RecyclerView

    override fun createView(ui: AnkoContext<CardView>) = with(ui) {
        frameLayout {
            cards = recyclerView { id = R.id.view_card_list}
            floatingActionButton {
                id = R.id.view_card_fab
                onClick {
                    val ctx = context
                    val i = Intent(context, EditCardActivity::class.java)
                    if (ctx is Activity) {
                        ctx.startActivityForResult(i, CardView.REQUEST_ADD_CARD)
                    } else {
                        ctx.startActivity(i)
                    }
                }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
