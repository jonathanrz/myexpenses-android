package br.com.jonathanzanella.myexpenses.resume;

import android.content.DialogInterface;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
class ReceiptMonthlyResumeAdapter extends RecyclerView.Adapter<ReceiptMonthlyResumeAdapter.ViewHolder> {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
	protected List<Receipt> receipts;
	@Getter
	private int totalValue;
	private int totalUnreceivedValue;

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

		public void setData(Receipt receipt) {
			if(name != null)
				name.setText(receipt.getName());
			if(date != null)
				date.setText(sdf.format(receipt.getDate().toDate()));
			income.setText(receipt.getIncomeFormatted());
			if(receipt.isCredited())
				income.setTextColor(getColor(R.color.value_received));
			else
				income.setTextColor(getColor(R.color.value_unreceived));
			if(source != null)
				source.setText(receipt.getSource().getName());
		}

		private int getColor(@ColorRes int color) {
			return itemView.getContext().getResources().getColor(color);
		}

		public void setTotal(int totalValue) {
			income.setText(NumberFormat.getCurrencyInstance().format(totalValue / 100.0));
		}

		@OnClick(R.id.row_monthly_resume_receipt_income)
		public void onIncome() {
			final Receipt receipt = adapterWeakReference.get().receipts.get(getAdapterPosition());
			if(!receipt.isCredited()) {
				String message = income.getContext().getString(R.string.message_confirm_receipt);
				message = message.concat(" " + receipt.getName() + " - " + receipt.getIncomeFormatted() + "?");
				new AlertDialog.Builder(income.getContext())
						.setMessage(message)
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								receipt.credit();
								ReceiptMonthlyResumeAdapter adapter = adapterWeakReference.get();
								adapter.updateTotalValue();
								adapter.notifyDataSetChanged();
							}
						})
						.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								dialogInterface.dismiss();
							}
						})
						.show();
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
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_receipt_total, parent, false);
		else if(viewType == VIEW_TYPE.TYPE_TOTAL_TO_PAY.ordinal())
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_receipt_total_to_pay, parent, false);
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

	public void loadData(DateTime month) {
		receipts = Receipt.resume(month);
		totalValue = 0;
		totalUnreceivedValue = 0;

		updateTotalValue();
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
