package br.com.jonathanzanella.myexpenses.resume;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
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
import br.com.jonathanzanella.myexpenses.helpers.TransactionsHelper;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository;
import br.com.jonathanzanella.myexpenses.source.Source;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;

class ReceiptMonthlyResumeAdapter extends RecyclerView.Adapter<ReceiptMonthlyResumeAdapter.ViewHolder> {
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM", Locale.getDefault());
	protected List<Receipt> receipts;
	@Getter
	private int totalValue;
	private int totalUnreceivedValue;
	private ReceiptRepository receiptRepository;

	private enum VIEW_TYPE {
		TYPE_NORMAL,
		TYPE_TOTAL_TO_PAY,
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

		@UiThread
		public void setData(final Receipt receipt) {
			if(name != null)
				name.setText(receipt.getName());
			if(date != null)
				date.setText(SIMPLE_DATE_FORMAT.format(receipt.getDate().toDate()));
			income.setText(receipt.getIncomeFormatted());
			income.setTypeface(null, Typeface.NORMAL);
			if(!receipt.isCredited())
				income.setTypeface(null, Typeface.BOLD);

			new AsyncTask<Void, Void, Source>() {

				@Override
				protected Source doInBackground(Void... voids) {
					return receipt.getSource();
				}

				@Override
				protected void onPostExecute(Source s) {
					super.onPostExecute(s);
					if(source != null && s != null)
						source.setText(s.getName());
				}
			}.execute();
		}

		public void setTotal(int totalValue) {
			income.setText(NumberFormat.getCurrencyInstance().format(totalValue / 100.0));
		}

		@OnClick(R.id.row_monthly_resume_receipt_income)
		public void onIncome() {
			if(getItemViewType() != VIEW_TYPE.TYPE_NORMAL.ordinal())
				return;

			final Receipt receipt = adapterWeakReference.get().receipts.get(getAdapterPosition());
			TransactionsHelper.showConfirmTransactionDialog(receipt, income.getContext(),
					new TransactionsHelper.DialogCallback() {
				@Override
				public void onPositiveButton() {
					ReceiptMonthlyResumeAdapter adapter = adapterWeakReference.get();
					adapter.updateTotalValue();
					adapter.notifyDataSetChanged();
				}
			});
		}
	}

	ReceiptMonthlyResumeAdapter(ReceiptRepository receiptRepository) {
		this.receiptRepository = receiptRepository;
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
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_receipt_total, parent, false);
		else if(viewType == VIEW_TYPE.TYPE_TOTAL_TO_PAY.ordinal())
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_receipt_total_to_receive, parent, false);
		else
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_receipt, parent, false);

		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if(isTotalView(position))
			holder.setTotal(totalValue);
		else if(isTotalToPayView(position))
			holder.setTotal(totalUnreceivedValue);
		else
			holder.setData(receipts.get(position));
	}

	private boolean isTotalView(int position) {
		return (receipts != null && position == receipts.size() + 1);
	}

	private boolean isTotalToPayView(int position) {
		return (receipts != null && position == receipts.size());
	}

	@Override
	public int getItemCount() {
		return receipts != null ? receipts.size() + 2 : 0;
	}

	void loadDataAsync(final DateTime month, final Runnable runnable) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				receipts = receiptRepository.resume(month);
				updateTotalValue();
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				notifyDataSetChanged();
				if(runnable != null)
					runnable.run();
			}
		}.execute();
	}

	private void updateTotalValue() {
		totalValue = 0;
		totalUnreceivedValue = 0;
		for (Receipt receipt : receipts) {
			totalValue += receipt.getIncome();
			if(!receipt.isCredited())
				totalUnreceivedValue += receipt.getIncome();
		}
	}
}
