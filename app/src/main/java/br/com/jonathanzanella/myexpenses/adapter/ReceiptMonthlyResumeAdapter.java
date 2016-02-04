package br.com.jonathanzanella.myexpenses.adapter;

import android.support.annotation.Nullable;
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
import br.com.jonathanzanella.myexpenses.model.Receipt;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class ReceiptMonthlyResumeAdapter extends RecyclerView.Adapter<ReceiptMonthlyResumeAdapter.ViewHolder> {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
	protected List<Receipt> receipts;
	private int totalValue;

	private enum VIEW_TYPE {
		TYPE_NORMAL,
		TYPE_TOTAL
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.row_monthly_resume_receipt_name) @Nullable
		TextView name;
		@Bind(R.id.row_monthly_resume_receipt_date) @Nullable
		TextView date;
		@Bind(R.id.row_monthly_resume_receipt_income)
		TextView income;
		@Bind(R.id.row_monthly_resume_receipt_source) @Nullable
		TextView source;

		WeakReference<ReceiptMonthlyResumeAdapter> adapterWeakReference;

		public ViewHolder(View itemView, ReceiptMonthlyResumeAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);
		}

		public void setData(Receipt receipt) {
			if(name != null)
				name.setText(receipt.getName());
			if(date != null)
				date.setText(sdf.format(receipt.getDate().toDate()));
			income.setText(NumberFormat.getCurrencyInstance().format(receipt.getIncome() / 100.0));
			if(source != null)
				source.setText(receipt.getSource().getName());
		}

		public void setTotal(int totalValue) {
			income.setText(NumberFormat.getCurrencyInstance().format(totalValue / 100.0));
		}
	}

	@Override
	public int getItemViewType(int position) {
		if(receipts != null && position == receipts.size()) {
			return VIEW_TYPE.TYPE_TOTAL.ordinal();
		} else {
			return VIEW_TYPE.TYPE_NORMAL.ordinal();
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v;
		if(viewType == VIEW_TYPE.TYPE_TOTAL.ordinal())
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_receipt_total, parent, false);
		else
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_receipt, parent, false);

		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if(position == receipts.size())
			holder.setTotal(totalValue);
		else
			holder.setData(receipts.get(position));
	}

	@Override
	public int getItemCount() {
		return receipts != null ? receipts.size() + 1 : 0;
	}

	public void loadData(DateTime month) {
		receipts = Receipt.monthly(month);
		totalValue = 0;

		for (Receipt receipt : receipts) {
			totalValue += receipt.getIncome();
		}
	}
}
