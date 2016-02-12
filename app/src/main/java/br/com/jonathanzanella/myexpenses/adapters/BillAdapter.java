package br.com.jonathanzanella.myexpenses.adapters;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.activities.ShowBillActivity;
import br.com.jonathanzanella.myexpenses.models.Bill;
import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Setter;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
	protected List<Bill> bills;
	protected List<Bill> billsFiltered;
	@Setter
	BillAdapterCallback callback;

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_bill_name)
		TextView name;
		@Bind(R.id.row_bill_amount)
		TextView amount;
		@Bind(R.id.row_bill_due_date)
		TextView dueDate;
		@Bind(R.id.row_bill_init_date)
		TextView initDate;
		@Bind(R.id.row_bill_end_date)
		TextView endDate;

		WeakReference<BillAdapter> adapterWeakReference;

		public ViewHolder(View itemView, BillAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		public void setData(Bill bill) {
			name.setText(bill.getName());
			amount.setText(NumberFormat.getCurrencyInstance().format(bill.getAmount() / 100.0));
			dueDate.setText(String.valueOf(bill.getDueDate()));
			initDate.setText(Bill.sdf.format(bill.getInitDate().toDate()));
			endDate.setText(Bill.sdf.format(bill.getEndDate().toDate()));
		}

		@Override
		public void onClick(View v) {
			BillAdapter adapter = adapterWeakReference.get();
			Bill bill = adapter.getBill(getAdapterPosition());
			if(bill != null) {
				if(adapter.callback != null) {
					adapter.callback.onBillSelected(bill);
				} else {
					Intent i = new Intent(itemView.getContext(), ShowBillActivity.class);
					i.putExtra(ShowBillActivity.KEY_BILL_ID, bill.getId());
					itemView.getContext().startActivity(i);
				}
			}
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
		return new ViewHolder(v, this);
	}

	private int getLayout() {
		return R.layout.row_bill;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(billsFiltered.get(position));
	}

	@Override
	public int getItemCount() {
		return billsFiltered != null ? billsFiltered.size() : 0;
	}

	public void loadData() {
		bills = Bill.all();
		billsFiltered = bills;
	}

	public @Nullable Bill getBill(int position) {
		return billsFiltered != null ? billsFiltered.get(position) : null;
	}

	public void addBill(Bill b) {
		bills.add(b);
		billsFiltered.add(b);
		notifyItemInserted(billsFiltered.size() - 1);
	}

	public void filter(String filter) {
		if(filter == null || filter.compareTo("") == 0) {
			billsFiltered = bills;
			return;
		}

		billsFiltered = new ArrayList<>();
		for (Bill bill : bills) {
			if(StringUtils.containsIgnoreCase(bill.getName(), filter))
				billsFiltered.add(bill);
		}
	}
}
