package br.com.jonathanzanella.myexpenses.expense;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardRepository;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import butterknife.Bind;
import butterknife.ButterKnife;

public class CreditCardMonthlyAdapter extends RecyclerView.Adapter<CreditCardMonthlyAdapter.ViewHolder> {
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM", Locale.getDefault());
	protected List<Expense> expenses;
	private int totalValue;
	private CardRepository cardRepository;

	private enum VIEW_TYPE {
		TYPE_NORMAL,
		TYPE_TOTAL
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.row_monthly_resume_expense_name) @Nullable
		TextView name;
		@Bind(R.id.row_monthly_resume_expense_date) @Nullable
		TextView date;
		@Bind(R.id.row_monthly_resume_expense_income)
		TextView income;
		@Bind(R.id.row_monthly_resume_expense_source) @Nullable
		TextView source;

		WeakReference<CreditCardMonthlyAdapter> adapterWeakReference;

		public ViewHolder(View itemView, CreditCardMonthlyAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);
		}

		public void setData(Expense expense) {
			if(name != null)
				name.setText(expense.getName());
			if(date != null)
				date.setText(SIMPLE_DATE_FORMAT.format(expense.getDate().toDate()));
			income.setText(NumberFormat.getCurrencyInstance().format(expense.getValue() / 100.0));
			if(source != null)
				source.setVisibility(View.GONE);
		}

		public void setTotal(int totalValue) {
			income.setText(NumberFormat.getCurrencyInstance().format(totalValue / 100.0));
		}
	}

	public CreditCardMonthlyAdapter(Context context) {
		cardRepository = new CardRepository(new RepositoryImpl<Card>(context),
				new ExpenseRepository(new RepositoryImpl<Expense>(context)));
	}

	@Override
	public int getItemViewType(int position) {
		if(expenses != null && position == expenses.size()) {
			return VIEW_TYPE.TYPE_TOTAL.ordinal();
		} else {
			return VIEW_TYPE.TYPE_NORMAL.ordinal();
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v;
		if(viewType == VIEW_TYPE.TYPE_TOTAL.ordinal())
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_expense_total, parent, false);
		else
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_expense, parent, false);

		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if(position == expenses.size())
			holder.setTotal(totalValue);
		else
			holder.setData(expenses.get(position));
	}

	@Override
	public int getItemCount() {
		return expenses != null ? expenses.size() + 1 : 0;
	}

	@WorkerThread
	public void loadData(Card creditCard, DateTime month) {
		expenses = cardRepository.creditCardBills(creditCard, month);
		totalValue = 0;

		for (Expense expense : expenses) {
			totalValue += expense.getValue();
		}
	}
}
