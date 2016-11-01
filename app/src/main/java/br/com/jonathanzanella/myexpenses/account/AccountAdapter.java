package br.com.jonathanzanella.myexpenses.account;

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

import br.com.jonathanzanella.myexpenses.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Setter;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
	private AccountAdapterPresenter presenter;

	private boolean simplified = false;
	AccountAdapterCallback callback;
	@Setter
	DateTime month;

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_account_name)
		TextView name;
		@Bind(R.id.row_account_balance)
		TextView balance;
		@Bind(R.id.row_account_to_pay_credit_card) @Nullable
		TextView accountToPayCreditCard;

		WeakReference<AccountAdapter> adapterWeakReference;

		public ViewHolder(View itemView, AccountAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		public void setData(Account acc) {
			name.setText(acc.getName());
			balance.setText(NumberFormat.getCurrencyInstance().format(acc.getBalance() / 100.0));
			if(accountToPayCreditCard != null)
				accountToPayCreditCard.setText(acc.isAccountToPayCreditCard() ? R.string.yes : R.string.no);
		}

		@Override
		public void onClick(View v) {
			AccountAdapter adapter = adapterWeakReference.get();
			Account acc = adapter.getAccount(getAdapterPosition());
			if(acc != null) {
				if(adapter.callback != null) {
					adapter.callback.onAccountSelected(acc);
				} else {
					Intent i = new Intent(itemView.getContext(), ShowAccountActivity.class);
					i.putExtra(ShowAccountActivity.KEY_ACCOUNT_UUID, acc.getUuid());
					if(adapter.month != null)
						i.putExtra(ShowAccountActivity.KEY_ACCOUNT_MONTH_TO_SHOW, adapter.month.getMillis());
					itemView.getContext().startActivity(i);
				}
			}
		}
	}

	public AccountAdapter() {
		presenter = new AccountAdapterPresenter(this, new AccountRepository());
	}

	public void refreshData() {
		presenter.loadAccountsAsync();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
		return new ViewHolder(v, this);
	}

	private int getLayout() {
		return simplified ? R.layout.row_account_simplified : R.layout.row_account;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(getAccount(position));
	}

	@Override
	public int getItemCount() {
		return presenter.getAccountsSize();
	}

	void addAccount(@NonNull Account acc) {
		presenter.addAccount(acc);
	}

	public @Nullable Account getAccount(int position) {
		return presenter.getAccount(position);
	}

	public void setCallback(AccountAdapterCallback callback) {
		this.callback = callback;
	}

	public void setSimplified(boolean simplified) {
		this.simplified = simplified;
	}
}
