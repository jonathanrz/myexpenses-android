package br.com.jonathanzanella.myexpenses.account;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
import br.com.jonathanzanella.myexpenses.transaction.TransactionAdapter;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jzanella on 7/9/16.
 */
public class MonthTransactionsView extends BaseView {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("MMMM/yy", Locale.getDefault());

	@Bind(R.id.view_month_transactions_list)
	RecyclerView list;
	@Bind(R.id.view_month_transactions_header)
	TextView header;
	@Bind(R.id.view_month_transactions_balance)
	TextView balance;

	@BindString(R.string.month_transactions)
	String monthTransactionsTemplate;
	@BindDimen(R.dimen.single_row_height)
	int singleRowHeight;

	public MonthTransactionsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MonthTransactionsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_account_month_transactions, this);
		ButterKnife.bind(this);
	}

	int showBalance(Account account, DateTime month, int balance) {
		header.setText(monthTransactionsTemplate.concat(" ").concat(sdf.format(month.toDate())));
		final TransactionAdapter adapter = new TransactionAdapter();
		adapter.addTransactions(Receipt.monthly(month, account));
		List<Expense> expenses = Expense.accountExpenses(account, month);
		adapter.addTransactions(expenses);
		if(account.isAccountToPayBills()) {
			new BillRepository(new Repository<Bill>(MyApplication.getContext())).monthly(month)
					.subscribeOn(AndroidSchedulers.mainThread())
					.subscribe(new Subscriber<List<Bill>>("MonthTransactionsView.showBalance") {
						@Override
						public void onNext(List<Bill> bills) {
							adapter.addTransactions(bills);
							adapter.notifyDataSetChanged();
						}
					});
		}

		list.setAdapter(adapter);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setNestedScrollingEnabled(false);
		list.setMinimumHeight(singleRowHeight * adapter.getItemCount());

		for (Transaction transaction : adapter.getTransactions()) {
			if(!transaction.credited()) {
				balance += transaction.getAmount();
			} else if(!transaction.debited()) {
				balance -= transaction.getAmount();
			}
		}

		this.balance.setText(NumberFormat.getCurrencyInstance().format(balance / 100.0));
		//noinspection deprecation
		this.balance.setTextColor(getResources().getColor(balance >= 0 ? R.color.value_unreceived : R.color.value_unpaid));

		return balance;
	}
}
