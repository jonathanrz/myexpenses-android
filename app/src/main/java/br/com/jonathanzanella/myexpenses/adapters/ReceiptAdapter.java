package br.com.jonathanzanella.myexpenses.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.activities.ShowReceiptActivity;
import br.com.jonathanzanella.myexpenses.models.Receipt;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {
	protected List<Receipt> receipts;
	protected List<Receipt> receiptsFiltered;

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
		@Bind(R.id.row_receipt_show_in_resume)
		TextView showInResume;

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
			showInResume.setText(receipt.isShowInResume() ? R.string.yes : R.string.no);
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
		holder.setData(receiptsFiltered.get(position));
	}

	@Override
	public int getItemCount() {
		return receiptsFiltered != null ? receiptsFiltered.size() : 0;
	}

	public void loadData(DateTime dateTime) {
		receipts = Receipt.monthly(dateTime);
		receiptsFiltered = receipts;
	}

	public void addReceipt(@NonNull Receipt receipt) {
		receipts.add(receipt);
		receiptsFiltered.add(receipt);
		notifyItemInserted(receiptsFiltered.size() - 1);
	}

	public @Nullable Receipt getReceipt(int position) {
		return receiptsFiltered != null ? receiptsFiltered.get(position) : null;
	}

	public void filter(String filter) {
		if(filter == null || filter.compareTo("") == 0) {
			receiptsFiltered = receipts;
			return;
		}

		receiptsFiltered = new ArrayList<>();
		for (Receipt receipt : receipts) {
			if(StringUtils.containsIgnoreCase(receipt.getName(), filter))
				receiptsFiltered.add(receipt);
		}
	}
}