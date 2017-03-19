package br.com.jonathanzanella.myexpenses.resume;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.AccountAdapter;
import br.com.jonathanzanella.myexpenses.bill.BillMonthlyResumeAdapter;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;

@SuppressLint("ViewConstructor")
class ResumeMonthlyView extends BaseView {
	public static final int ACCOUNT_COLUMNS = 3;

	@Bind(R.id.view_monthly_resume_accounts)
	RecyclerView accounts;
	@Bind(R.id.view_monthly_resume_receipts)
	RecyclerView receipts;
	@Bind(R.id.view_monthly_resume_expenses)
	RecyclerView expenses;
	@Bind(R.id.view_monthly_resume_bills)
	RecyclerView bills;
	@Bind(R.id.view_monthly_resume_total_receipts)
	TextView totalReceipts;
	@Bind(R.id.view_monthly_resume_total_expenses)
	TextView totalExpenses;
	@Bind(R.id.view_monthly_resume_balance)
	TextView balance;

	int singleRowHeight;

	private final DateTime month;

	private AccountAdapter accountAdapter;
	private ReceiptMonthlyResumeAdapter receiptAdapter;
	private ExpenseMonthlyResumeAdapter expensesAdapter;
	private BillMonthlyResumeAdapter billsAdapter;
	private ReceiptRepository receiptRepository;
	private ExpenseRepository expenseRepository;

	public ResumeMonthlyView(Context context, DateTime month) {
		super(context);

		singleRowHeight = getResources().getDimensionPixelSize(R.dimen.single_row_height);
		this.month = month;
		accountAdapter.setMonth(month);
	}

	@Override
	@UiThread
	protected void init() {
		inflate(getContext(), R.layout.view_monthly_resume, this);
		expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(getContext()));
		receiptRepository = new ReceiptRepository(new RepositoryImpl<Receipt>(getContext()));
		ButterKnife.bind(this);

		initAccount();
		initReceipts();
		initExpenses();
		initBills();
	}

	private void initAccount() {
		accountAdapter = new AccountAdapter();
		accountAdapter.setSimplified(true);

		accounts.setAdapter(accountAdapter);
		accounts.setHasFixedSize(true);
		accounts.setLayoutManager(new GridLayoutManager(getContext(), ACCOUNT_COLUMNS));
	}

	private void initReceipts() {
		receiptAdapter = new ReceiptMonthlyResumeAdapter(receiptRepository);

		receipts.setAdapter(receiptAdapter);
		receipts.setHasFixedSize(true);
		receipts.setLayoutManager(new LinearLayoutManager(getContext()));
		receipts.setNestedScrollingEnabled(false);
	}

	private void initExpenses() {
		expensesAdapter = new ExpenseMonthlyResumeAdapter();

		expenses.setAdapter(expensesAdapter);
		expenses.setHasFixedSize(true);
		expenses.setLayoutManager(new LinearLayoutManager(getContext()));
		expenses.setNestedScrollingEnabled(false);
	}

	private void initBills() {
		billsAdapter = new BillMonthlyResumeAdapter();

		bills.setAdapter(billsAdapter);
		bills.setHasFixedSize(true);
		bills.setLayoutManager(new LinearLayoutManager(getContext()));
		bills.setNestedScrollingEnabled(false);
	}

	@Override
	@UiThread
	public void refreshData() {
		super.refreshData();
		accountAdapter.refreshData();
		accountAdapter.notifyDataSetChanged();

		loadReceipts();
		loadExpenses();
		loadBills();
	}

	@UiThread
	private void loadBills() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				billsAdapter.loadDataAsync(month);
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				billsAdapter.notifyDataSetChanged();
				bills.getLayoutParams().height = singleRowHeight * billsAdapter.getItemCount();

				updateTotalExpenses();
			}
		}.execute();
	}

	@UiThread
	private void loadExpenses() {
		new AsyncTask<Void, Void, List<Expense>>() {

			@Override
			protected List<Expense> doInBackground(Void... voids) {
				return expenseRepository.expensesForResumeScreen(month);
			}

			@Override
			protected void onPostExecute(List<Expense> expensesList) {
				super.onPostExecute(expensesList);
				expensesAdapter.setExpenses(expensesList);
				expensesAdapter.notifyDataSetChanged();
				expenses.getLayoutParams().height = singleRowHeight * expensesAdapter.getItemCount();

				updateTotalExpenses();
			}
		}.execute();
	}

	@UiThread
	private void updateTotalExpenses() {
		int totalExpensesValue = expensesAdapter.getTotalValue();
		totalExpensesValue += billsAdapter.getTotalValue();
		totalExpenses.setText(CurrencyHelper.format(totalExpensesValue));

		updateBalance();
	}

	@UiThread
	private void loadReceipts() {
		receiptAdapter.loadDataAsync(month, new Runnable() {
			@Override
			public void run() {
				receipts.getLayoutParams().height = singleRowHeight * receiptAdapter.getItemCount();
				int totalReceiptsValue = receiptAdapter.getTotalValue();
				totalReceipts.setText(CurrencyHelper.format(totalReceiptsValue));

				updateBalance();
			}
		});
	}

	@UiThread
	private void updateBalance() {
		int totalExpensesValue = expensesAdapter.getTotalValue();
		totalExpensesValue += billsAdapter.getTotalValue();

		int balanceValue = receiptAdapter.getTotalValue() - totalExpensesValue;
		balance.setText(CurrencyHelper.format(balanceValue));
		if(balanceValue >= 0) {
			//noinspection deprecation
			balance.setTextColor(getResources().getColor(R.color.value_unreceived));
		} else {
			//noinspection deprecation
			balance.setTextColor(getResources().getColor(R.color.value_unpaid));
		}
	}
}
