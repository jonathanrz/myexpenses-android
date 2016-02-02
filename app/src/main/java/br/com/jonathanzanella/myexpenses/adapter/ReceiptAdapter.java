package br.com.jonathanzanella.myexpenses.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.activities.ShowReceiptActivity;
import br.com.jonathanzanella.myexpenses.model.Receipt;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {
	protected List<Receipt> receipts;

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_receipt_name)
		TextView name;
		@Bind(R.id.row_receipt_date)
		TextView date;
		@Bind(R.id.row_receipt_income)
		TextView income;
		@Bind(R.id.row_receipt_source)
		TextView source;
		@Bind(R.id.row_receipt_account)
		TextView account;

		WeakReference<ReceiptAdapter> adapterWeakReference;

		public ViewHolder(View itemView, ReceiptAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		public void setData(Receipt receipt) {
			name.setText(receipt.getName());
			date.setText(Receipt.sdf.format(receipt.getDate().toDate()));
			income.setText(NumberFormat.getCurrencyInstance().format(receipt.getIncome() / 100.0));
			source.setText(receipt.getSource().getName());
			account.setText(receipt.getAccount().getName());
		}

		@Override
		public void onClick(View v) {
			Receipt receipt = adapterWeakReference.get().getReceipt(getAdapterPosition());
			if(receipt != null) {
                Intent i = new Intent(itemView.getContext(), ShowReceiptActivity.class);
                i.putExtra(ShowReceiptActivity.KEY_RECEIPT_ID, receipt.getId());
                itemView.getContext().startActivity(i);
			}
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_receipt, parent, false);
		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(receipts.get(position));
	}

	@Override
	public int getItemCount() {
		return receipts != null ? receipts.size() : 0;
	}

	public void loadData() {
		receipts = Receipt.all();
	}

	public void addReceipt(@NonNull Receipt receipt) {
		receipts.add(receipt);
		notifyItemInserted(receipts.size() - 1);
	}

	public @Nullable Receipt getReceipt(int position) {
		return receipts != null ? receipts.get(position) : null;
	}
}
