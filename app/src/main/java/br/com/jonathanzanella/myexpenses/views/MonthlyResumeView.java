package br.com.jonathanzanella.myexpenses.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapter.AccountAdapter;
import br.com.jonathanzanella.myexpenses.adapter.ExpenseMonthlyResumeAdapter;
import br.com.jonathanzanella.myexpenses.adapter.ReceiptMonthlyResumeAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jzanella onCard 2/2/16.
 */
@SuppressLint("ViewConstructor")
public class MonthlyResumeView extends BaseView {
	@Bind(R.id.view_monthly_resume_accounts)
	RecyclerView accounts;
	@Bind(R.id.view_monthly_resume_receipts)
	RecyclerView receipts;
	@Bind(R.id.view_monthly_resume_expenses)
	RecyclerView expenses;

	private AccountAdapter accountAdapter;
	private ReceiptMonthlyResumeAdapter receiptAdapter;
	private ExpenseMonthlyResumeAdapter expensesAdapter;

	public MonthlyResumeView(Context context, DateTime month) {
		super(context);

		receiptAdapter.loadData(month);
		receiptAdapter.notifyDataSetChanged();

		expensesAdapter.loadData(month);
		expensesAdapter.notifyDataSetChanged();
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_monthly_resume, this);
		ButterKnife.bind(this);

		initAccount();
		initReceipts();
		initExpenses();
	}

	private void initAccount() {
		accountAdapter = new AccountAdapter();
		accountAdapter.setSimplified(true);
		accountAdapter.loadData();

		accounts.setAdapter(accountAdapter);
		accounts.setHasFixedSize(true);
		accounts.setLayoutManager(new GridLayoutManager(getContext(), 3));
	}

	private void initReceipts() {
		receiptAdapter = new ReceiptMonthlyResumeAdapter();

		receipts.setAdapter(receiptAdapter);
		receipts.setHasFixedSize(true);
		receipts.setLayoutManager(new LinearLayoutManager(getContext()));
	}

	private void initExpenses() {
		expensesAdapter = new ExpenseMonthlyResumeAdapter();

		expenses.setAdapter(expensesAdapter);
		expenses.setHasFixedSize(true);
		expenses.setLayoutManager(new LinearLayoutManager(getContext()));
	}

	@Override
	public void refreshData() {
		super.refreshData();
		accountAdapter.loadData();
		accountAdapter.notifyDataSetChanged();
	}
}
