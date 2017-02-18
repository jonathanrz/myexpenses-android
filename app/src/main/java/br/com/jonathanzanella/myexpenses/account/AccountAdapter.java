package br.com.jonathanzanella.myexpenses.account;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.Repository;
import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Setter;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
	private AccountAdapterPresenter presenter;

	private boolean simplified = false;
	private AccountAdapterCallback callback;
	@Setter
	private DateTime month;

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_account_name)
		TextView name;
		@Bind(R.id.row_account_balance)
		TextView balance;
		@Bind(R.id.row_account_to_pay_credit_card) @Nullable
		TextView accountToPayCreditCard;

		public ViewHolder(View itemView) {
			super(itemView);

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
			Account acc = getAccount(getAdapterPosition());
			if(acc != null) {
				if(callback != null) {
					callback.onAccountSelected(acc);
				} else {
					Intent i = new Intent(itemView.getContext(), ShowAccountActivity.class);
					i.putExtra(ShowAccountActivity.KEY_ACCOUNT_UUID, acc.getUuid());
					if(month != null)
						i.putExtra(ShowAccountActivity.KEY_ACCOUNT_MONTH_TO_SHOW, month.getMillis());
					itemView.getContext().startActivity(i);
				}
			}
		}
	}

	public AccountAdapter() {
		presenter = new AccountAdapterPresenter(this, new AccountRepository(new Repository<Account>(MyApplication.getContext())));
	}

	public void refreshData() {
		presenter.loadAccountsAsync();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
		return new ViewHolder(v);
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
