package br.com.jonathanzanella.myexpenses.account;

import android.content.Context;
import android.os.AsyncTask;
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
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
import br.com.jonathanzanella.myexpenses.transaction.TransactionAdapter;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.ButterKnife;

public class MonthTransactionsView extends BaseView {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("MMMM/yy", Locale.getDefault());
	private ReceiptRepository receiptRepository;
	private ExpenseRepository expenseRepository;

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
		receiptRepository = new ReceiptRepository(new Repository<Receipt>(getContext()));
		expenseRepository = new ExpenseRepository(new Repository<Expense>(getContext()));
		inflate(getContext(), R.layout.view_account_month_transactions, this);
		ButterKnife.bind(this);
	}

	int showBalance(final Account account, final DateTime month, int balance) {
		header.setText(monthTransactionsTemplate.concat(" ").concat(sdf.format(month.toDate())));
		final TransactionAdapter adapter = new TransactionAdapter();

		loadReceipts(account, month, adapter);
		loadExpenses(account, month, adapter);
		loadBills(account, month, adapter);

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

	private void loadBills(Account account, final DateTime month, final TransactionAdapter adapter) {
		if(account.isAccountToPayBills()) {
			new AsyncTask<Void, Void, List<Bill>>() {

				@Override
				protected List<Bill> doInBackground(Void... voids) {
					return new BillRepository(new Repository<Bill>(MyApplication.getContext()), expenseRepository).monthly(month);
				}

				@Override
				protected void onPostExecute(List<Bill> bills) {
					super.onPostExecute(bills);
					adapter.addTransactions(bills);
					adapter.notifyDataSetChanged();
				}
			}.execute();
		}
	}

	private void loadExpenses(final Account account, final DateTime month, final TransactionAdapter adapter) {
		new AsyncTask<Void, Void, List<Expense>>() {

			@Override
			protected List<Expense> doInBackground(Void... voids) {
				return expenseRepository.accountExpenses(account, month);
			}

			@Override
			protected void onPostExecute(List<Expense> expenses) {
				super.onPostExecute(expenses);
				adapter.addTransactions(expenses);
				adapter.notifyDataSetChanged();
			}
		}.execute();
	}

	private void loadReceipts(final Account account, final DateTime month, final TransactionAdapter adapter) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				adapter.addTransactions(receiptRepository.monthly(month, account));
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				adapter.notifyDataSetChanged();
			}
		}.execute();
	}
}
