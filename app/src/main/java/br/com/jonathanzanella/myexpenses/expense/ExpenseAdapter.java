package br.com.jonathanzanella.myexpenses.expense;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
	private List<Expense> expenses;
	private ExpenseAdapterPresenter presenter;
	private DateTime date;

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_expense_name)
		TextView name;
		@Bind(R.id.row_expense_date)
		TextView date;
		@Bind(R.id.row_expense_value)
		TextView value;
		@Bind(R.id.row_expense_chargeable)
		TextView chargeable;
		@Bind(R.id.row_expense_bill_layout)
		View billLayout;
		@Bind(R.id.row_expense_bill)
		TextView billView;
		@Bind(R.id.row_expense_charge_next_month)
		TableRow chargeNextMonth;

		WeakReference<ExpenseAdapter> adapterWeakReference;

		public ViewHolder(View itemView, ExpenseAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		public void setData(Expense expense) {
			name.setText(expense.getName());
			date.setText(Receipt.sdf.format(expense.getDate().toDate()));
			value.setText(NumberFormat.getCurrencyInstance().format(expense.getValue() / 100.0));
			expense.getChargeable()
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Subscriber<Chargeable>("ExpenseAdapter.setData") {

						@Override
						public void onNext(Chargeable c) {
							chargeable.setText(c.getName());
						}
					});

			chargeNextMonth.setVisibility(expense.isChargeNextMonth() ? View.VISIBLE : View.GONE);
			expense.getBill().subscribe(new Subscriber<Bill>("ExpenseAdapter.setData") {
				@Override
				public void onNext(Bill bill) {
					if(bill == null) {
						billLayout.setVisibility(View.GONE);
					} else {
						billLayout.setVisibility(View.VISIBLE);
						billView.setText(bill.getName());
					}
				}
			});
		}

		@Override
		public void onClick(View v) {
			Expense expense = adapterWeakReference.get().getExpense(getAdapterPosition());
			if(expense != null) {
                Intent i = new Intent(itemView.getContext(), ShowExpenseActivity.class);
                i.putExtra(ShowExpenseActivity.KEY_EXPENSE_UUID, expense.getUuid());
                itemView.getContext().startActivity(i);
			}
		}
	}

	ExpenseAdapter() {
		presenter = new ExpenseAdapterPresenter(this, new ExpenseRepository());
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_expense, parent, false);
		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(getExpense(position));
	}

	@Override
	public int getItemCount() {
		return expenses != null ? expenses.size() : 0;
	}

	public void loadData(DateTime date) {
		expenses = Expense.monthly(date);
		expenses = presenter.getExpenses(true, date);
		this.date = date;
	}

	void addExpense(@NonNull Expense expense) {
		presenter.addExpense(expense);
		expenses = presenter.getExpenses(false, date);
	}

	@Nullable
	private Expense getExpense(int position) {
		return expenses != null ? expenses.get(position) : null;
	}

	public void filter(String filter) {
		presenter.filter(filter);
		expenses = presenter.getExpenses(false, date);
	}
}