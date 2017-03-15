package br.com.jonathanzanella.myexpenses.expense;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardRepository;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import butterknife.Bind;
import butterknife.ButterKnife;

class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.ViewHolder> {
	protected List<Expense> expenses;
	private int totalValue;
	private CardRepository cardRepository;

	private enum VIEW_TYPE {
		TYPE_NORMAL,
		TYPE_TOTAL
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.row_credit_card_expense_date) @Nullable
		TextView date;
		@Bind(R.id.row_credit_card_expense_value)
		TextView value;

		WeakReference<CreditCardAdapter> adapterWeakReference;

		public ViewHolder(View itemView, CreditCardAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);
		}

		public void setData(Expense expense) {
			if(date != null)
				date.setText(Receipt.SIMPLE_DATE_FORMAT.format(expense.getDate().toDate()));
			value.setText(NumberFormat.getCurrencyInstance().format(expense.getValue() / 100.0));
		}

		public void setTotal(int totalValue) {
			value.setText(NumberFormat.getCurrencyInstance().format(totalValue / 100.0));
		}
	}

	public CreditCardAdapter(Context context) {
		ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(context));
		cardRepository = new CardRepository(new RepositoryImpl<Card>(context), expenseRepository);
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
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_credit_card_expense_total, parent, false);
		else
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_credit_card_expense, parent, false);
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

	public void loadData(Card creditCard, DateTime date) {
		expenses = cardRepository.creditCardBills(creditCard, date);

		totalValue = 0;
		for (Expense expense : expenses)
			totalValue += expense.getValue();
	}
}
