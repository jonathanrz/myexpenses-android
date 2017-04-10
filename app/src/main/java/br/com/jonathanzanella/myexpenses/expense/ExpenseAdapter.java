package br.com.jonathanzanella.myexpenses.expense;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
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
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.AdapterColorHelper;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
	private final ExpenseRepository expenseRepository;
	private final ExpenseAdapterPresenter presenter;
	private List<Expense> expenses;
	private DateTime date;

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@BindView(R.id.row_expense_name)
		TextView name;
		@BindView(R.id.row_expense_date)
		TextView date;
		@BindView(R.id.row_expense_value)
		TextView value;
		@BindView(R.id.row_expense_chargeable)
		TextView chargeable;
		@BindView(R.id.row_expense_bill_stt)
		TextView billViewStt;
		@BindView(R.id.row_expense_bill)
		TextView billView;
		@BindView(R.id.row_expense_charge_next_month)
		TextView chargeNextMonth;

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

		@UiThread
		public void setData(final Expense expense) {
			itemView.setTag(expense.getUuid());
			itemView.setBackgroundColor(adapterColorHelper.getColorForGridWithTwoColumns(getAdapterPosition()));
			name.setText(expense.getName());
			date.setText(Expense.SIMPLE_DATE_FORMAT.format(expense.getDate().toDate()));
			value.setText(CurrencyHelper.format(expense.getValue()));
			new AsyncTask<Void, Void, Chargeable>() {

				@Override
				protected Chargeable doInBackground(Void... voids) {
					return expense.getChargeableFromCache();
				}

				@Override
				protected void onPostExecute(Chargeable c) {
					super.onPostExecute(c);
					chargeable.setText(c.getName());
				}
			}.execute();

			chargeNextMonth.setVisibility(expense.isChargedNextMonth() ? View.VISIBLE : View.INVISIBLE);
			new AsyncTask<Void, Void, Bill>() {

				@Override
				protected Bill doInBackground(Void... voids) {
					return expense.getBill();
				}

				@Override
				protected void onPostExecute(Bill bill) {
					super.onPostExecute(bill);
					if(bill == null) {
						billViewStt.setVisibility(View.INVISIBLE);
						billView.setVisibility(View.INVISIBLE);
					} else {
						billViewStt.setVisibility(View.VISIBLE);
						billView.setVisibility(View.VISIBLE);
						billView.setText(bill.getName());
					}
				}
			}.execute();
		}

		@Override
		public void onClick(View v) {
			Expense expense = getExpense(getAdapterPosition());
			if(expense != null) {
                Intent i = new Intent(itemView.getContext(), ShowExpenseActivity.class);
                i.putExtra(ShowExpenseActivity.KEY_EXPENSE_UUID, expense.getUuid());
                itemView.getContext().startActivity(i);
			}
		}
	}

	ExpenseAdapter() {
		expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(MyApplication.getContext()));
		presenter = new ExpenseAdapterPresenter(this, expenseRepository);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_expense, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(getExpense(position));
	}

	@Override
	public int getItemCount() {
		return expenses != null ? expenses.size() : 0;
	}

	@WorkerThread
	public void loadData(DateTime date) {
		expenses = expenseRepository.monthly(date);
		expenses = presenter.getExpenses(true, date);
		this.date = date;
	}

	@Nullable
	private Expense getExpense(int position) {
		return expenses != null ? expenses.get(position) : null;
	}

	public void filter(String filter) {
		presenter.filter(filter);
		expenses = presenter.getExpenses(false, date);
	}
}