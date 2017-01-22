package br.com.jonathanzanella.myexpenses.receipt;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.source.Source;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {
	protected List<Receipt> receipts;
	private ReceiptAdapterPresenter presenter;
	private DateTime date;

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

		WeakReference<ReceiptAdapter> adapterWeakReference;

		public ViewHolder(View itemView, ReceiptAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		public void setData(Receipt receipt) {
			name.setText(receipt.getName());
			date.setText(Receipt.sdf.format(receipt.getDate().toDate()));
			income.setText(NumberFormat.getCurrencyInstance().format(receipt.getIncome() / 100.0));
			receipt.getSource().subscribe(new Subscriber<Source>("ReceiptAdapter.setData") {
				@Override
				public void onNext(Source s) {
					source.setText(s.getName());
				}
			});

			receipt.getAccount()
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Subscriber<Account>("ReceiptAdapter.setData") {
						@Override
						public void onNext(Account a) {
							account.setText(a.getName());
						}
					});

			showInResume.setText(receipt.isShowInResume() ? R.string.yes : R.string.no);
		}

		@Override
		public void onClick(View v) {
			Receipt receipt = adapterWeakReference.get().getReceipt(getAdapterPosition());
			if(receipt != null) {
                Intent i = new Intent(itemView.getContext(), ShowReceiptActivity.class);
                i.putExtra(ShowReceiptActivity.KEY_RECEIPT_UUID, receipt.getUuid());
                itemView.getContext().startActivity(i);
			}
		}
	}

	ReceiptAdapter() {
		presenter = new ReceiptAdapterPresenter(this, new ReceiptRepository());
	}

	public void loadData(DateTime date) {
		receipts = presenter.getReceipts(true, date);
		this.date = date;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_receipt, parent, false);
		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(getReceipt(position));
	}

	@Override
	public int getItemCount() {
		return receipts != null ? receipts.size() : 0;
	}

	void addReceipt(@NonNull Receipt receipt) {
		presenter.addReceipt(receipt);
	}

	@Nullable
	private Receipt getReceipt(int position) {
		return receipts != null ? receipts.get(position) : null;
	}

	public void filter(String filter) {
		presenter.filter(filter);
		loadData(date);
	}
}