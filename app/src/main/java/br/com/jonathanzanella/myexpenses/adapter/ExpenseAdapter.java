package br.com.jonathanzanella.myexpenses.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.activities.ShowExpenseActivity;
import br.com.jonathanzanella.myexpenses.model.Expense;
import br.com.jonathanzanella.myexpenses.model.Receipt;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
	protected List<Expense> expenses;
	protected List<Expense> expensesFiltered;

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_expense_name)
		TextView name;
		@Bind(R.id.row_expense_date)
		TextView date;
		@Bind(R.id.row_expense_value)
		TextView value;
		@Bind(R.id.row_expense_chargeable)
		TextView chargeable;
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
			chargeable.setText(expense.getChargeable().getName());
			chargeNextMonth.setVisibility(expense.isChargeNextMonth() ? View.VISIBLE : View.GONE);
		}

		@Override
		public void onClick(View v) {
			Expense expense = adapterWeakReference.get().getExpense(getAdapterPosition());
			if(expense != null) {
                Intent i = new Intent(itemView.getContext(), ShowExpenseActivity.class);
                i.putExtra(ShowExpenseActivity.KEY_EXPENSE_ID, expense.getId());
                itemView.getContext().startActivity(i);
			}
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_expense, parent, false);
		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(expensesFiltered.get(position));
	}

	@Override
	public int getItemCount() {
		return expensesFiltered != null ? expensesFiltered.size() : 0;
	}

	public void loadData(DateTime dateTime) {
		expenses = Expense.monthly(dateTime);
		expensesFiltered = expenses;
	}

	public void addExpense(@NonNull Expense expense) {
		expenses.add(expense);
		expensesFiltered.add(expense);
		notifyItemInserted(expensesFiltered.size() - 1);
	}

	public @Nullable Expense getExpense(int position) {
		return expensesFiltered != null ? expensesFiltered.get(position) : null;
	}

	public void filter(String filter) {
		if(filter == null || filter.compareTo("") == 0) {
			expensesFiltered = expenses;
			return;
		}

		expensesFiltered = new ArrayList<>();
		for (Expense expense : expenses) {
			if(StringUtils.containsIgnoreCase(expense.getName(), filter))
				expensesFiltered.add(expense);
		}
	}
}