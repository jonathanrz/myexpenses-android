package br.com.jonathanzanella.myexpenses.resume;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.card.CreditCardInvoiceActivity;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ShowExpenseActivity;
import br.com.jonathanzanella.myexpenses.helpers.TransactionsHelper;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;

class ExpenseMonthlyResumeAdapter extends RecyclerView.Adapter<ExpenseMonthlyResumeAdapter.ViewHolder> {
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

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_monthly_resume_expense_name) @Nullable
		TextView name;
		@Bind(R.id.row_monthly_resume_expense_date) @Nullable
		TextView date;
		@Bind(R.id.row_monthly_resume_expense_income)
		TextView income;
		@Bind(R.id.row_monthly_resume_expense_source) @Nullable
		TextView source;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		@UiThread
		public void setData(final Expense expense) {
			if(name != null)
				name.setText(expense.getName());
			if(date != null)
				date.setText(sdf.format(expense.getDate().toDate()));
			income.setText(NumberFormat.getCurrencyInstance().format(expense.getValue() / 100.0));
			income.setTypeface(null, Typeface.NORMAL);
			if(!expense.isCharged())
				income.setTypeface(null, Typeface.BOLD);
			if(source != null) {
				new AsyncTask<Void, Void, Chargeable>() {

					@Override
					protected Chargeable doInBackground(Void... voids) {
						return expense.getChargeable();
					}

					@Override
					protected void onPostExecute(Chargeable chargeable) {
						super.onPostExecute(chargeable);
						source.setText(chargeable.getName());
					}
				}.execute();
			}
		}

		public void setTotal(int totalValue) {
			income.setText(NumberFormat.getCurrencyInstance().format(totalValue / 100.0));
		}

		@OnClick(R.id.row_monthly_resume_expense_income)
		public void onIncome() {
			if(getItemViewType() != VIEW_TYPE.TYPE_NORMAL.ordinal())
				return;

			final Expense expense = getExpense(getAdapterPosition());
			TransactionsHelper.showConfirmTransactionDialog(expense, income.getContext(),
					new TransactionsHelper.DialogCallback() {
				@Override
				public void onPositiveButton() {
					updateTotalValue();
					notifyDataSetChanged();
				}
			});
		}

		@Override
		public void onClick(View v) {
			if(getItemViewType() != VIEW_TYPE.TYPE_NORMAL.ordinal())
				return;

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

		return new ViewHolder(v);
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
		updateTotalValue();
	}

	private void updateTotalValue() {
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
