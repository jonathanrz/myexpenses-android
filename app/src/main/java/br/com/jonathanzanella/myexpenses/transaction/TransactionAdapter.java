package br.com.jonathanzanella.myexpenses.transaction;

import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;

/**
 * Created by Jonathan Zanella on 13/02/16.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
	@Getter
	List<Transaction> transactions = new ArrayList<>();

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.row_transaction_date)
		TextView date;
		@Bind(R.id.row_transaction_name)
		TextView name;
		@Bind(R.id.row_transaction_value)
		TextView value;

		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		public void setData(Transaction transaction) {
			date.setText(Transaction.sdf.format(transaction.getDate().toDate()));
			name.setText(transaction.getName());
			value.setText(NumberFormat.getCurrencyInstance().format(transaction.getAmount() / 100.0));
			value.setTypeface(null, Typeface.NORMAL);
			if(transaction instanceof Receipt) {
				value.setTextColor(getColor(R.color.value_unreceived));
				if(!transaction.credited())
					value.setTypeface(null, Typeface.BOLD);
			} else if(transaction instanceof Expense || transaction instanceof Bill) {
				value.setTextColor(getColor(R.color.value_unpaid));
				if(!transaction.debited())
					value.setTypeface(null, Typeface.BOLD);
			}
		}

		private int getColor(@ColorRes int color) {
			return itemView.getContext().getResources().getColor(color);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transaction, parent, false);
		return new ViewHolder(v);
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
}
