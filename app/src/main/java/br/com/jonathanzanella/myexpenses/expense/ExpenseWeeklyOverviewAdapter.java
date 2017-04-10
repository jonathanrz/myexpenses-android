package br.com.jonathanzanella.myexpenses.expense;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.card.CreditCardInvoiceActivity;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpenseWeeklyOverviewAdapter extends RecyclerView.Adapter<ExpenseWeeklyOverviewAdapter.ViewHolder> {
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd", Locale.getDefault());
	protected List<Expense> expenses;
	private int totalValue;

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@BindView(R.id.row_weekly_overview_expense_name)
		TextView name;
		@BindView(R.id.row_weekly_overview_expense_date)
		TextView date;
		@BindView(R.id.row_weekly_overview_expense_income)
		TextView income;
		@BindView(R.id.row_weekly_overview_expense_source)
		TextView source;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		@UiThread
		public void setData(final Expense expense) {
			name.setText(expense.getName());
			synchronized (this) {
				date.setText(SIMPLE_DATE_FORMAT.format(expense.getDate().toDate()));
			}
			income.setText(CurrencyHelper.format(expense.getValueToShowInOverview()));

			new AsyncTask<Void, Void, Chargeable>() {

				@Override
				protected Chargeable doInBackground(Void... voids) {
					return expense.getChargeableFromCache();
				}

				@Override
				protected void onPostExecute(Chargeable chargeable) {
					super.onPostExecute(chargeable);
					source.setText(chargeable.getName());
				}
			}.execute();
		}

		@Override
		public void onClick(View v) {
			Expense expense = getExpense(getAdapterPosition());
			if(expense != null) {
				if(expense.getCreditCard() != null) {
					Intent i = new Intent(itemView.getContext(), CreditCardInvoiceActivity.class);
					i.putExtra(CreditCardInvoiceActivity.KEY_CREDIT_CARD_UUID, expense.getCreditCard().getUuid());
					i.putExtra(CreditCardInvoiceActivity.KEY_INIT_DATE, expense.getDate());
					itemView.getContext().startActivity(i);
				} else {
					Intent i = new Intent(itemView.getContext(), ShowExpenseActivity.class);
					i.putExtra(ShowExpenseActivity.KEY_EXPENSE_UUID, expense.getUuid());
					itemView.getContext().startActivity(i);
				}
			}
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_weekly_overview_expense, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(expenses.get(position));
	}

	@Override
	public int getItemCount() {
		return expenses != null ? expenses.size() : 0;
	}

	public void setExpenses(List<Expense> expenses) {
		this.expenses = expenses;
		totalValue = 0;

		for (Expense expense : expenses)
			totalValue += expense.getValueToShowInOverview();
	}

	private Expense getExpense(int position) {
		return expenses.get(position);
	}

	public List<Expense> getExpenses() {
		return expenses;
	}

	public int getTotalValue() {
		return totalValue;
	}
}
