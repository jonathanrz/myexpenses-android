package br.com.jonathanzanella.myexpenses.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.activities.CreditCardInvoiceActivity;
import br.com.jonathanzanella.myexpenses.activities.ShowExpenseActivity;
import br.com.jonathanzanella.myexpenses.models.Expense;
import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class ExpenseWeeklyOverviewAdapter extends RecyclerView.Adapter<ExpenseWeeklyOverviewAdapter.ViewHolder> {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
	@Getter
	protected List<Expense> expenses;
	@Getter
	private int totalValue;

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_weekly_overview_expense_name)
		TextView name;
		@Bind(R.id.row_weekly_overview_expense_date)
		TextView date;
		@Bind(R.id.row_weekly_overview_expense_income)
		TextView income;
		@Bind(R.id.row_weekly_overview_expense_source)
		TextView source;

		WeakReference<ExpenseWeeklyOverviewAdapter> adapterWeakReference;

		public ViewHolder(View itemView, ExpenseWeeklyOverviewAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		public void setData(Expense expense) {
			name.setText(expense.getName());
			date.setText(sdf.format(expense.getDate().toDate()));
			income.setText(NumberFormat.getCurrencyInstance().format(expense.getValue() / 100.0));
			source.setText(expense.getChargeable().getName());
		}

		@Override
		public void onClick(View v) {
			Expense expense = adapterWeakReference.get().getExpense(getAdapterPosition());
			if(expense != null) {
				if(expense.getCreditCard() != null) {
					Intent i = new Intent(itemView.getContext(), CreditCardInvoiceActivity.class);
					i.putExtra(CreditCardInvoiceActivity.KEY_CREDIT_CARD_UUID, expense.getCreditCard().getUuid());
					i.putExtra(CreditCardInvoiceActivity.KEY_INIT_DATE, expense.getDate());
					itemView.getContext().startActivity(i);
				} else {
					Intent i = new Intent(itemView.getContext(), ShowExpenseActivity.class);
					i.putExtra(ShowExpenseActivity.KEY_EXPENSE_ID, expense.getId());
					itemView.getContext().startActivity(i);
				}
			}
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_weekly_overview_expense, parent, false);
		return new ViewHolder(v, this);
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
			totalValue += expense.getValue();
	}

	private Expense getExpense(int position) {
		return expenses.get(position);
	}
}
