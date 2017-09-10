package br.com.jonathanzanella.myexpenses.account

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.AccountAdapter.Format.NORMAL
import br.com.jonathanzanella.myexpenses.helpers.AdapterColorHelper
import br.com.jonathanzanella.myexpenses.helpers.toCurrencyFormatted
import br.com.jonathanzanella.myexpenses.views.anko.applyTemplateViewStyles
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import timber.log.Timber
import javax.inject.Inject

class AccountAdapter @Inject constructor(val repository: AccountRepository) : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {
    private var format = NORMAL
    private var callback: AccountAdapterCallback? = null
    private var accounts: List<Account> = ArrayList()
    private var dbQueryDisposable: Disposable? = null

    enum class Format {
        NORMAL,
        RESUME,
        LIST
    }

    inner class ViewHolder(itemView: View, val name: TextView, val balance: TextView, val accountToPayCreditCard: TextView?) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val adapterColorHelper: AdapterColorHelper

        init {
            val oddColor = ContextCompat.getColor(itemView.context, R.color.color_list_odd)
            val evenColor = ContextCompat.getColor(itemView.context, R.color.color_list_even)
            adapterColorHelper = AdapterColorHelper(oddColor, evenColor)

            itemView.setOnClickListener(this)
        }

        fun setData(acc: Account) {
            itemView.tag = acc.uuid
            if (format != Format.RESUME)
                itemView.setBackgroundColor(adapterColorHelper.getColorForGridWithTwoColumns(adapterPosition))

            name.text = acc.name
            balance.text = acc.balance.toCurrencyFormatted()
            if (accountToPayCreditCard != null)
                accountToPayCreditCard.visibility = if (acc.accountToPayCreditCard) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View) {
            val acc = getAccount(adapterPosition)
            if (callback == null) {
                val i = Intent(itemView.context, ShowAccountActivity::class.java)
                i.putExtra(ShowAccountActivity.KEY_ACCOUNT_UUID, acc.uuid)
                itemView.context.startActivity(i)
            } else {
                callback!!.onAccountSelected(acc)
            }
        }
    }

    fun onDestroy() {
        dbQueryDisposable?.dispose()
    }

    private fun loadData() {
        dbQueryDisposable?.dispose()

        dbQueryDisposable = when {
            format === AccountAdapter.Format.RESUME -> repository.forResumeScreen()
            else -> repository.all()
        }
                .doOnNext { accounts = it }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notifyDataSetChanged() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (format) {
            NORMAL -> {
                val ui = NormalViewUI()
                ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui.accountName, ui.accountBalance, ui.accountToPayCreditCard)
            }
            else -> {
                val ui = SimplifiedViewUI(format)
                ViewHolder(ui.createView(AnkoContext.create(parent.context, parent)), ui.accountName, ui.accountBalance, null)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(getAccount(position))
    }

    override fun getItemCount(): Int {
        return accounts.size
    }

    fun getAccount(position: Int): Account {
        return accounts[position]
    }

    fun setCallback(callback: AccountAdapterCallback) {
        this.callback = callback
    }

    fun setFormat(format: Format) {
        this.format = format
        loadData()
    }
}

private class SimplifiedViewUI(val format: AccountAdapter.Format): AnkoComponent<ViewGroup> {
    lateinit var accountName : TextView
    lateinit var accountBalance : TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        var height = wrapContent
        when(format) {
            AccountAdapter.Format.RESUME -> height = resources.getDimensionPixelSize(R.dimen.single_row_height)
            AccountAdapter.Format.LIST -> height = resources.getDimensionPixelSize(R.dimen.resume_row_height)
            else -> Timber.e("unmapped format: $format")
        }

        verticalLayout {
            linearLayout {
                when(format) {
                    AccountAdapter.Format.RESUME -> {
                        leftPadding = resources.getDimensionPixelSize(R.dimen.min_spacing)
                        rightPadding = resources.getDimensionPixelSize(R.dimen.min_spacing)
                    }
                    AccountAdapter.Format.LIST -> padding = resources.getDimensionPixelSize(R.dimen.row_spacing)
                    else -> Timber.e("unmapped format: $format")
                }

                accountName = textView {
                    id = R.id.row_account_name
                    ellipsize = TextUtils.TruncateAt.END
                }.lparams(width = 0, weight = 1f) {
                    marginEnd = dip(5)
                }
                accountBalance = textView {
                    id = R.id.row_account_balance
                    textColor = ResourcesCompat.getColor(resources, R.color.color_primary, null)
                }
            }.lparams(height = height, width = matchParent)
        }.applyRecursively(::applyTemplateViewStyles)
    }
}

private class NormalViewUI: AnkoComponent<ViewGroup> {
    lateinit var accountName : TextView
    lateinit var accountBalance : TextView
    lateinit var accountToPayCreditCard : TextView

    override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
        relativeLayout {
            padding = view.resources.getDimensionPixelSize(R.dimen.default_spacing)

            accountName = textView {
                id = R.id.row_account_name
                textColor = R.color.color_primary_text
                ellipsize = TextUtils.TruncateAt.END
            }.lparams {
                alignParentStart()
                alignStart(R.id.row_account_balance)
            }

            accountBalance = textView {
                id = R.id.row_account_balance
                textColor = R.color.color_primary
            }.lparams {
                alignParentEnd()
            }

            accountToPayCreditCard = textView {
                id = R.id.row_account_to_pay_credit_card
                text = resources.getString(R.string.account_to_pay_credit_card)
                textColor = R.color.color_secondary_text
                textSize = 10.0f
                visibility = View.GONE
            }.lparams {
                alignParentEnd()
                below(R.id.row_account_name)
            }
        }.applyRecursively(::applyTemplateViewStyles)
    }
}
