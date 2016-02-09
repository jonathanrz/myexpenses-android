package br.com.jonathanzanella.myexpenses.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapter.AccountAdapter;
import br.com.jonathanzanella.myexpenses.adapter.BillMonthlyResumeAdapter;
import br.com.jonathanzanella.myexpenses.adapter.ExpenseMonthlyResumeAdapter;
import br.com.jonathanzanella.myexpenses.adapter.ReceiptMonthlyResumeAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jzanella onCard 2/2/16.
 */
@SuppressLint("ViewConstructor")
public class ResumeMonthlyView extends BaseView {
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

	private AccountAdapter accountAdapter;
	private ReceiptMonthlyResumeAdapter receiptAdapter;
	private ExpenseMonthlyResumeAdapter expensesAdapter;
	private BillMonthlyResumeAdapter billsAdapter;
	private DateTime month;

	public ResumeMonthlyView(Context context, DateTime month) {
		super(context);

		singleRowHeight = getResources().getDimensionPixelSize(R.dimen.single_row_height);
		this.month = month;
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_monthly_resume, this);
		ButterKnife.bind(this);

		initAccount();
		initReceipts();
		initExpenses();
		initBills();
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
	public void refreshData() {
		super.refreshData();
		accountAdapter.loadData();
		accountAdapter.notifyDataSetChanged();

		receiptAdapter.loadData(month);
		receiptAdapter.notifyDataSetChanged();
		receipts.getLayoutParams().height = singleRowHeight * receiptAdapter.getItemCount();

		expensesAdapter.loadData(month);
		expensesAdapter.notifyDataSetChanged();
		expenses.getLayoutParams().height = singleRowHeight * expensesAdapter.getItemCount();

		billsAdapter.loadData(month);
		billsAdapter.notifyDataSetChanged();
		bills.getLayoutParams().height = singleRowHeight * billsAdapter.getItemCount();

		int totalReceiptsValue = receiptAdapter.getTotalValue();
		totalReceipts.setText(NumberFormat.getCurrencyInstance().format(totalReceiptsValue / 100.0));
		int totalExpensesValue = expensesAdapter.getTotalValue();
		totalExpenses.setText(NumberFormat.getCurrencyInstance().format(totalExpensesValue / 100.0));
		int balanceValue = totalReceiptsValue - totalExpensesValue;
		balance.setText(NumberFormat.getCurrencyInstance().format(balanceValue / 100.0));
		if(balanceValue >= 0)
			balance.setTextColor(getResources().getColor(R.color.value_unreceived));
		else
			balance.setTextColor(getResources().getColor(R.color.value_unpaid));
	}
}
