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

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.helpers.TransactionsHelper;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository;
import br.com.jonathanzanella.myexpenses.receipt.ShowReceiptActivity;
import br.com.jonathanzanella.myexpenses.source.Source;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

class ReceiptMonthlyResumeAdapter extends RecyclerView.Adapter<ReceiptMonthlyResumeAdapter.ViewHolder> {
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM", Locale.getDefault());
	protected List<Receipt> receipts;
	private final ReceiptRepository receiptRepository;
	private int totalValue;
	private int totalUnreceivedValue;

	private enum ViewType {
		TYPE_NORMAL,
		TYPE_TOTAL_TO_PAY,
		TYPE_TOTAL
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_monthly_resume_receipt_name) @Nullable
		TextView name;
		@Bind(R.id.row_monthly_resume_receipt_date) @Nullable
		TextView date;
		@Bind(R.id.row_monthly_resume_receipt_income)
		TextView income;
		@Bind(R.id.row_monthly_resume_receipt_source) @Nullable
		TextView source;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		@UiThread
		public void setData(final Receipt receipt) {
			itemView.setTag(receipt.getUuid());
			if(name != null)
				name.setText(receipt.getName());
			if(date != null) {
				synchronized (this) {
					date.setText(SIMPLE_DATE_FORMAT.format(receipt.getDate().toDate()));
				}
			}
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
			income.setText(CurrencyHelper.format(totalValue));
		}

		@OnClick(R.id.row_monthly_resume_receipt_income)
		public void onIncome() {
			if(getItemViewType() != ViewType.TYPE_NORMAL.ordinal())
				return;

			final Receipt receipt = getReceipt(getAdapterPosition());
			TransactionsHelper.showConfirmTransactionDialog(receipt, income.getContext(),
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
			if(getItemViewType() != ViewType.TYPE_NORMAL.ordinal())
				return;

			Receipt receipt = getReceipt(getAdapterPosition());
			if(receipt != null) {
				Intent i = new Intent(itemView.getContext(), ShowReceiptActivity.class);
				i.putExtra(ShowReceiptActivity.Companion.getKEY_RECEIPT_UUID(), receipt.getUuid());
				itemView.getContext().startActivity(i);
			}
		}
	}

	ReceiptMonthlyResumeAdapter(ReceiptRepository receiptRepository) {
		this.receiptRepository = receiptRepository;
	}

	@Override
	public int getItemViewType(int position) {
		if(isTotalView(position)) {
			return ViewType.TYPE_TOTAL.ordinal();
		} else if(isTotalToPayView(position)) {
			return ViewType.TYPE_TOTAL_TO_PAY.ordinal();
		} else {
			return ViewType.TYPE_NORMAL.ordinal();
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v;
		if(viewType == ViewType.TYPE_TOTAL.ordinal())
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_receipt_total, parent, false);
		else if(viewType == ViewType.TYPE_TOTAL_TO_PAY.ordinal())
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_receipt_total_to_receive, parent, false);
		else
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_receipt, parent, false);

		return new ViewHolder(v);
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
		return receipts != null && position == receipts.size() + 1;
	}

	private boolean isTotalToPayView(int position) {
		return receipts != null && position == receipts.size();
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

	private Receipt getReceipt(int position) {
		return receipts.get(position);
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

	int getTotalValue() {
		return totalValue;
	}
}
