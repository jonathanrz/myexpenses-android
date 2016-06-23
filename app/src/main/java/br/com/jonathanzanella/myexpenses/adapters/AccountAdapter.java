package br.com.jonathanzanella.myexpenses.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.activities.ShowAccountActivity;
import br.com.jonathanzanella.myexpenses.models.Account;
import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Setter;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
	protected List<Account> accounts;
	@Setter
	private boolean simplified = false;
	@Setter
	AccountAdapterCallback callback;

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_account_name)
		TextView name;
		@Bind(R.id.row_account_balance)
		TextView balance;
		@Bind(R.id.row_account_balance_date)
		TextView balanceDate;
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
			balanceDate.setText(Account.sdf.format(acc.getBalanceDate().toDate()));
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
		return simplified ? R.layout.row_account_simplified : R.layout.row_account;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(accounts.get(position));
	}

	@Override
	public int getItemCount() {
		return accounts != null ? accounts.size() : 0;
	}

	public void loadData() {
		accounts = Account.all();
	}

	public void addAccount(@NonNull Account acc) {
		accounts.add(acc);
		notifyItemInserted(accounts.size() - 1);
	}

	public @Nullable Account getAccount(int position) {
		return accounts != null ? accounts.get(position) : null;
	}
}
