package br.com.jonathanzanella.myexpenses.card

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.annotation.UiThread
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.views.FilterableView
import br.com.jonathanzanella.myexpenses.views.RefreshableView
import br.com.jonathanzanella.myexpenses.views.ResultableView
import br.com.jonathanzanella.myexpenses.views.TabableView
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.recyclerView
import org.jetbrains.anko.*
import org.jetbrains.anko.design.floatingActionButton

@UiThread
class CardView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), RefreshableView, ResultableView, FilterableView, TabableView {
    override var filter = ""
    private var ui = CardViewUI()
    private var adapter = CardAdapter()

    init {
        addView(ui.createView(AnkoContext.Companion.create(context, this)))

        ui.cards.adapter = adapter
        ui.cards.layoutManager = GridLayoutManager(context, 1)
        ui.cards.itemAnimator = DefaultItemAnimator()

        refreshData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ADD_CARD -> if (resultCode == Activity.RESULT_OK)
                doAsync {
                    adapter.loadData()

                    uiThread { adapter.notifyDataSetChanged() }
                }
        }
    }

    override fun refreshData() {
        doAsync {
            adapter.loadData()

            uiThread { adapter.notifyDataSetChanged() }
        }
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
