package br.com.jonathanzanella.myexpenses.card

import android.content.Intent
import android.os.AsyncTask
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.helpers.AdapterColorHelper
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import br.com.jonathanzanella.myexpenses.views.anko.static
import br.com.jonathanzanella.myexpenses.views.anko.staticWithData
import org.jetbrains.anko.*

class CardAdapter : RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    private var cards: List<Card> = ArrayList()
    private var callback: CardAdapterCallback? = null

    private val cardRepository: CardRepository
    private val expenseRepository = ExpenseRepository(RepositoryImpl<Expense>(MyApplication.getContext()))

    inner class ViewHolder(itemView: View, val ui: CardAdapterViewUI) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val adapterColorHelper: AdapterColorHelper
        private val credit: String = itemView.context.getString(R.string.credit)
        private val debit: String = itemView.context.getString(R.string.debit)

        init {
            val oddColor = ContextCompat.getColor(itemView.context, R.color.color_list_odd)
            val evenColor = ContextCompat.getColor(itemView.context, R.color.color_list_even)
            adapterColorHelper = AdapterColorHelper(oddColor, evenColor)

            itemView.setOnClickListener(this)
        }

        @UiThread
        fun setData(card: Card) {
            itemView.setBackgroundColor(adapterColorHelper.getColorForLinearLayout(adapterPosition))
            object : AsyncTask<Void, Void, Account>() {

                override fun doInBackground(vararg voids: Void): Account {
                    return card.account!!
                }

                override fun onPostExecute(a: Account) {
                    super.onPostExecute(a)
                    ui.cardAccount.text = a.name
                }
            }.execute()

            var cardName = card.name + " - "

            when (card.type) {
                CardType.CREDIT -> cardName += credit
                CardType.DEBIT -> cardName += debit
            }

            ui.cardName.text = cardName
        }

        override fun onClick(v: View) {
            val card = getCreditCard(adapterPosition)
            if (callback != null) {
                callback!!.onCard(card)
            } else {
                val i = Intent(itemView.context, ShowCardActivity::class.java)
                i.putExtra(ShowCardActivity.KEY_CREDIT_CARD_UUID, card.uuid)
                itemView.context.startActivity(i)
            }
        }
    }

    init {
        cardRepository = CardRepository(expenseRepository)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ui = CardAdapterViewUI()
        return ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(cards[position])
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    @WorkerThread
    fun loadData() {
        cards = cardRepository.all()
    }

    private fun getCreditCard(position: Int): Card {
        return cards[position]
    }

    fun setCallback(callback: CardAdapterCallback) {
        this.callback = callback
    }
}

class CardAdapterViewUI: AnkoComponent<ViewGroup> {
    lateinit var cardName: TextView
    lateinit var cardAccount: TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        tableLayout {
            padding = resources.getDimensionPixelSize(R.dimen.default_spacing)
            orientation = TableLayout.HORIZONTAL

            tableRow {
                static { text = resources.getString(R.string.name) }
                cardName = staticWithData { id = R.id.row_card_name }
            }
            tableRow {
                static { text = resources.getString(R.string.account) }
                cardAccount = staticWithData { id = R.id.row_card_account }
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
