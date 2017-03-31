package br.com.jonathanzanella.myexpenses.receipt;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.source.Source;
import butterknife.Bind;
import butterknife.ButterKnife;

class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {
	protected List<Receipt> receipts;
	private final ReceiptAdapterPresenter presenter;
	private DateTime date;

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

		public ViewHolder(View itemView) {
			super(itemView);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		@UiThread
		public void setData(final Receipt receipt) {
			itemView.setTag(receipt.getUuid());
			name.setText(receipt.getName());
			date.setText(Receipt.SIMPLE_DATE_FORMAT.format(receipt.getDate().toDate()));
			income.setText(CurrencyHelper.format(receipt.getIncome()));

			new AsyncTask<Void, Void, Source>() {

				@Override
				protected Source doInBackground(Void... voids) {
					return receipt.getSource();
				}

				@Override
				protected void onPostExecute(Source s) {
					super.onPostExecute(s);
					source.setText(s.getName());
				}
			}.execute();

			new AsyncTask<Void, Void, Account>() {

				@Override
				protected Account doInBackground(Void... voids) {
					return receipt.getAccount();
				}

				@Override
				protected void onPostExecute(Account a) {
					super.onPostExecute(a);
					account.setText(a.getName());
				}
			}.execute();

			showInResume.setText(receipt.isShowInResume() ? R.string.yes : R.string.no);
		}

		@Override
		public void onClick(View v) {
			Receipt receipt = getReceipt(getAdapterPosition());
			if(receipt != null) {
                Intent i = new Intent(itemView.getContext(), ShowReceiptActivity.class);
                i.putExtra(ShowReceiptActivity.KEY_RECEIPT_UUID, receipt.getUuid());
                itemView.getContext().startActivity(i);
			}
		}
	}

	ReceiptAdapter(Context context) {
		presenter = new ReceiptAdapterPresenter(new ReceiptRepository(new RepositoryImpl<Receipt>(context)));
	}

	public void loadData(DateTime date) {
		receipts = presenter.getReceipts(true, date);
		this.date = date;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_receipt, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(getReceipt(position));
	}

	@Override
	public int getItemCount() {
		return receipts != null ? receipts.size() : 0;
	}

	@Nullable
	private Receipt getReceipt(int position) {
		return receipts != null ? receipts.get(position) : null;
	}

	public void filter(String filter) {
		presenter.filter(filter);
		receipts = presenter.getReceipts(false, date);
	}
}