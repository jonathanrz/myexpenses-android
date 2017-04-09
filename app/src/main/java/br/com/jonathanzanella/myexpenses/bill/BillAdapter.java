package br.com.jonathanzanella.myexpenses.bill;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.AdapterColorHelper;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import butterknife.Bind;
import butterknife.ButterKnife;

class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
	protected List<Bill> bills;
	private BillAdapterCallback callback;

	private final BillAdapterPresenter presenter;

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_bill_name)
		TextView name;
		@Bind(R.id.row_bill_amount)
		TextView amount;
		@Bind(R.id.row_bill_due_date)
		TextView dueDate;
		@Bind(R.id.row_bill_dates)
		TextView dates;

		private final AdapterColorHelper adapterColorHelper;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);

			//noinspection deprecation
			int oddColor = itemView.getContext().getResources().getColor(R.color.color_list_odd);
			//noinspection deprecation
			int evenColor = itemView.getContext().getResources().getColor(R.color.color_list_even);
			adapterColorHelper = new AdapterColorHelper(oddColor, evenColor);

			itemView.setOnClickListener(this);
		}

		public void setData(Bill bill) {
			itemView.setBackgroundColor(adapterColorHelper.getColorForGridWithTwoColumns(getAdapterPosition()));
			name.setText(bill.getName());
			amount.setText(CurrencyHelper.format(bill.getAmount()));
			dueDate.setText(String.valueOf(bill.getDueDate()));
			String datesText = Bill.SIMPLE_DATE_FORMAT.format(bill.getInitDate().toDate()) + " - " +
					Bill.SIMPLE_DATE_FORMAT.format(bill.getEndDate().toDate());
			dates.setText(datesText);
		}

		@Override
		public void onClick(View v) {
			Bill bill = getBill(getAdapterPosition());
			if(bill != null) {
				if(callback != null) {
					callback.onBillSelected(bill);
				} else {
					Intent i = new Intent(itemView.getContext(), ShowBillActivity.class);
					i.putExtra(ShowBillActivity.KEY_BILL_UUID, bill.getUuid());
					itemView.getContext().startActivity(i);
				}
			}
		}
	}

	BillAdapter() {
		ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(MyApplication.getContext()));
		BillRepository repository = new BillRepository(new RepositoryImpl<Bill>(MyApplication.getContext()), expenseRepository);
		this.presenter = new BillAdapterPresenter(repository);
		refreshData();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
		return new ViewHolder(v);
	}

	private int getLayout() {
		return R.layout.row_bill;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(getBill(position));
	}

	@Override
	public int getItemCount() {
		return bills != null ? bills.size() : 0;
	}

	public final void refreshData() {
		bills = presenter.getBills(true);
	}

	@Nullable
	private Bill getBill(int position) {
		return bills != null ? bills.get(position) : null;
	}

	public void filter(String filter) {
		presenter.filter(filter);
		bills = presenter.getBills(false);
	}

	public void setCallback(BillAdapterCallback callback) {
		this.callback = callback;
	}
}
