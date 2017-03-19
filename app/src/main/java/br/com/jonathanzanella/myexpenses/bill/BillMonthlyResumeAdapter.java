package br.com.jonathanzanella.myexpenses.bill;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;

public class BillMonthlyResumeAdapter extends RecyclerView.Adapter<BillMonthlyResumeAdapter.ViewHolder> {
	protected List<Bill> bills;
	private final BillRepository billRepository;
	@Getter
	private int totalValue;

	private enum ViewType {
		TYPE_NORMAL,
		TYPE_TOTAL
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.row_monthly_resume_bill_name) @Nullable
		TextView name;
		@Bind(R.id.row_monthly_resume_bill_day) @Nullable
		TextView day;
		@Bind(R.id.row_monthly_resume_bill_amount)
		TextView amount;

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);
		}

		public void setData(Bill bill) {
			if(name != null)
				name.setText(bill.getName());
			if(day != null)
				day.setText(String.valueOf(bill.getDueDate()));
			amount.setText(CurrencyHelper.format(bill.getAmount()));
		}

		public void setTotal(int totalValue) {
			amount.setText(CurrencyHelper.format(totalValue));
		}
	}

	public BillMonthlyResumeAdapter() {
		ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(MyApplication.getContext()));
		billRepository = new BillRepository(new RepositoryImpl<Bill>(MyApplication.getContext()), expenseRepository);
	}

	@Override
	public int getItemViewType(int position) {
		if(bills != null && position == bills.size()) {
			return ViewType.TYPE_TOTAL.ordinal();
		} else {
			return ViewType.TYPE_NORMAL.ordinal();
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v;
		if(viewType == ViewType.TYPE_TOTAL.ordinal())
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_bill_total, parent, false);
		else
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_monthly_resume_bill, parent, false);

		return new ViewHolder(v);
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

	@WorkerThread
	public void loadDataAsync(final DateTime month) {
		bills = billRepository.monthly(month);
		totalValue = 0;

		for (Bill bill : bills) {
			totalValue += bill.getAmount();
		}
	}
}