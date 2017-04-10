package br.com.jonathanzanella.myexpenses.transaction;

import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.helpers.TransactionsHelper;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
	private final List<Transaction> transactions = new ArrayList<>();

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.row_transaction_date)
		TextView date;
		@BindView(R.id.row_transaction_name)
		TextView name;
		@BindView(R.id.row_transaction_value)
		TextView value;

		private final WeakReference<TransactionAdapter> adapterWeakReference;

		public ViewHolder(View itemView, TransactionAdapter adapter) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			adapterWeakReference = new WeakReference<>(adapter);
		}

		public void setData(Transaction transaction) {
			date.setText(Transaction.SIMPLE_DATE_FORMAT.format(transaction.getDate().toDate()));
			name.setText(transaction.getName());
			value.setText(CurrencyHelper.format(transaction.getAmount()));
			value.setTypeface(null, Typeface.NORMAL);
			if(transaction instanceof Receipt) {
				value.setTextColor(getColor(R.color.receipt));
				if(!transaction.credited())
					value.setTypeface(null, Typeface.BOLD);
			} else if(transaction instanceof Expense || transaction instanceof Bill) {
				value.setTextColor(getColor(R.color.expense));
				if(!transaction.debited())
					value.setTypeface(null, Typeface.BOLD);
			}
		}

		@OnClick(R.id.row_transaction_value)
		public void onValue() {
			final int adapterPosition = getAdapterPosition();
			final TransactionAdapter adapter = adapterWeakReference.get();
			Transaction transaction = adapter.transactions.get(adapterPosition);
			TransactionsHelper.showConfirmTransactionDialog(transaction, date.getContext(), new TransactionsHelper.DialogCallback() {
				@Override
				public void onPositiveButton() {
					adapter.notifyItemChanged(adapterPosition);
				}
			});
		}

		private int getColor(@ColorRes int color) {
			//noinspection deprecation
			return itemView.getContext().getResources().getColor(color);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transaction, parent, false);
		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(transactions.get(position));
	}

	@Override
	public int getItemCount() {
		return transactions.size();
	}

	public void addTransactions(List<? extends Transaction> transactions) {
		this.transactions.addAll(transactions);
		Collections.sort(this.transactions, new Comparator<Transaction>() {
			@Override
			public int compare(Transaction lhs, Transaction rhs) {
				if(lhs.getDate().isAfter(rhs.getDate()))
					return 1;
				return -1;
			}
		});
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}
}
