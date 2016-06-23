package br.com.jonathanzanella.myexpenses.adapters;

import android.content.Intent;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
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
public class ExpenseMonthlyResumeAdapter extends RecyclerView.Adapter<ExpenseMonthlyResumeAdapter.ViewHolder> {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
	@Getter
	protected List<Expense> expenses;
	@Getter
	private int totalValue;
	private int totalUnpaidValue;

	private enum VIEW_TYPE {
		TYPE_NORMAL,
		TYPE_TOTAL_TO_PAY,
		TYPE_TOTAL
	}

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_monthly_resume_expense_name) @Nullable
		TextView name;
		@Bind(R.id.row_monthly_resume_expense_date) @Nullable
		TextView date;
		@Bind(R.id.row_monthly_resume_expense_income)
		TextView income;
		@Bind(R.id.row_monthly_resume_expense_source) @Nullable
		TextView source;

		WeakReference<ExpenseMonthlyResumeAdapter> adapterWeakReference;

		public ViewHolder(View itemView, ExpenseMonthlyResumeAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		public void setData(Expense expense) {
			if(name != null)
				name.setText(expense.getName());
			if(date != null)
				date.setText(sdf.format(expense.getDate().toDate()));
			income.setText(NumberFormat.getCurrencyInstance().format(expense.getValue() / 100.0));
			if(expense.isCharged())
				income.setTextColor(getColor(R.color.value_paid));
			else
				income.setTextColor(getColor(R.color.value_unpaid));
			if(source != null)
				source.setText(expense.getChargeable().getName());
		}

		private int getColor(@ColorRes int color) {
			return itemView.getContext().getResources().getColor(color);
		}

		public void setTotal(int totalValue) {
			income.setText(NumberFormat.getCurrencyInstance().format(totalValue / 100.0));
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
	public int getItemViewType(int position) {
		if(isTotalView(position)) {
			return VIEW_TYPE.TYPE_TOTAL.ordinal();
		} else if(isTotalToPayView(position)) {
			return VIEW_TYPE.TYPE_TOTAL_TO_PAY.ordinal();
		} else {
			return VIEW_TYPE.TYPE_NORMAL.ordinal();
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v;
		if(viewType == VIEW_TYPE.TYPE_TOTAL.ordinal())
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_expense_total, parent, false);
		else if(viewType == VIEW_TYPE.TYPE_TOTAL_TO_PAY.ordinal())
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_expense_total_to_pay, parent, false);
		else
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_expense, parent, false);

		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if(isTotalView(position))
			holder.setTotal(totalValue);
		else if(isTotalToPayView(position))
			holder.setTotal(totalUnpaidValue);
		else
			holder.setData(expenses.get(position));
	}

	private boolean isTotalView(int position) {
		return (expenses != null && position == expenses.size() + 1);
	}

	private boolean isTotalToPayView(int position) {
		return (expenses != null && position == expenses.size());
	}

	@Override
	public int getItemCount() {
		return expenses != null ? expenses.size() + 2 : 0;
	}

	public void setExpenses(List<Expense> expenses) {
		this.expenses = expenses;
		totalValue = 0;
		totalUnpaidValue = 0;

		for (Expense expense : expenses) {
			totalValue += expense.getValue();
			if(!expense.isCharged())
				totalUnpaidValue += expense.getValue();
		}
	}

	private Expense getExpense(int position) {
		return expenses.get(position);
	}
}
