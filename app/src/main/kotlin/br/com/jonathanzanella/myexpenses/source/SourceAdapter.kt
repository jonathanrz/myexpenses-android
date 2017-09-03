package br.com.jonathanzanella.myexpenses.source

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.helpers.AdapterColorHelper
import org.jetbrains.anko.*

internal class SourceAdapter : RecyclerView.Adapter<SourceAdapter.ViewHolder>() {
    private val presenter = SourceAdapterPresenter(SourceRepository())
    private var sources = presenter.getSources(false)
    private var callback: SourceAdapterCallback? = null

    inner class ViewHolder(itemView: View, val ui: SourceAdapterViewUI) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val adapterColorHelper: AdapterColorHelper

        init {
            val oddColor = ContextCompat.getColor(itemView.context, R.color.color_list_odd)
            val evenColor = ContextCompat.getColor(itemView.context, R.color.color_list_even)
            adapterColorHelper = AdapterColorHelper(oddColor, evenColor)

            itemView.setOnClickListener(this)
        }

        fun setData(source: Source) {
            itemView.setBackgroundColor(adapterColorHelper.getColorForGridWithTwoColumns(adapterPosition))
            ui.sourceName.text = source.name
        }

        override fun onClick(v: View) {
            val source = getSource(adapterPosition)
            if (callback != null) {
                callback!!.onSourceSelected(source)
            } else {
                val i = Intent(itemView.context, ShowSourceActivity::class.java)
                i.putExtra(ShowSourceActivity.KEY_SOURCE_UUID, source.uuid)
                itemView.context.startActivity(i)
            }
        }
    }

    fun refreshData() {
        sources = presenter.getSources(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ui = SourceAdapterViewUI()
        return ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(sources[position])
    }

    override fun getItemCount(): Int {
        return sources.size
    }

    private fun getSource(position: Int): Source {
        return sources[position]
    }

    fun setCallback(callback: SourceAdapterCallback) {
        this.callback = callback
    }
}

class SourceAdapterViewUI: AnkoComponent<ViewGroup> {
    lateinit var sourceName : TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        verticalLayout {
            padding = context.resources.getDimensionPixelSize(R.dimen.default_spacing)

            sourceName = textView {
                id = R.id.row_source_name
                textColor = R.color.color_primary_text
            }
        }
    }
}
