package br.com.jonathanzanella.myexpenses.bill;

import android.os.AsyncTask;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class BillMonthlyResumeAdapter extends RecyclerView.Adapter<BillMonthlyResumeAdapter.ViewHolder> {
	protected List<Bill> bills;
	@Getter
	private int totalValue;

	private enum VIEW_TYPE {
		TYPE_NORMAL,
		TYPE_TOTAL
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.row_monthly_resume_bill_name) @Nullable
		TextView name;
		@Bind(R.id.row_monthly_resume_bill_day) @Nullable
		TextView day;
		@Bind(R.id.row_monthly_resume_bill_amount)
		TextView amount;

		WeakReference<BillMonthlyResumeAdapter> adapterWeakReference;

		public ViewHolder(View itemView, BillMonthlyResumeAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);
		}

		public void setData(Bill bill) {
			if(name != null)
				name.setText(bill.getName());
			if(day != null)
				day.setText(String.valueOf(bill.getDueDate()));
			amount.setText(NumberFormat.getCurrencyInstance().format(bill.getAmount() / 100.0));
		}

		public void setTotal(int totalValue) {
			amount.setText(NumberFormat.getCurrencyInstance().format(totalValue / 100.0));
		}
	}

	@Override
	public int getItemViewType(int position) {
		if(bills != null && position == bills.size()) {
			return VIEW_TYPE.TYPE_TOTAL.ordinal();
		} else {
			return VIEW_TYPE.TYPE_NORMAL.ordinal();
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v;
		if(viewType == VIEW_TYPE.TYPE_TOTAL.ordinal())
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_bill_total, parent, false);
		else
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_bill, parent, false);

		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if(position == bills.size())
			holder.setTotal(totalValue);
		else
			holder.setData(bills.get(position));
	}

	@Override
	public int getItemCount() {
		return bills != null ? bills.size() + 1 : 0;
	}

	public void loadDataAsync(final DateTime month, final Runnable runnable) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				bills = new BillRepository().monthly(month);
				totalValue = 0;

				for (Bill bill : bills) {
					totalValue += bill.getAmount();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				notifyDataSetChanged();
				runnable.run();
			}
		}.execute();
	}
}