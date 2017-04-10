package br.com.jonathanzanella.myexpenses.account;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.AdapterColorHelper;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import butterknife.Bind;
import butterknife.ButterKnife;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
	private final AccountAdapterPresenter presenter;

	private Format format = Format.NORMAL;
	private AccountAdapterCallback callback;
	private DateTime month;

	public enum Format {
		NORMAL,
		RESUME,
		LIST
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_account_name)
		TextView name;
		@Bind(R.id.row_account_balance)
		TextView balance;
		@Bind(R.id.row_account_to_pay_credit_card) @Nullable
		TextView accountToPayCreditCard;

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

		public void setData(Account acc) {
			itemView.setTag(acc.getUuid());
			if(format != Format.RESUME)
				itemView.setBackgroundColor(adapterColorHelper.getColorForGridWithTwoColumns(getAdapterPosition()));

			name.setText(acc.getName());
			balance.setText(CurrencyHelper.format(acc.getBalance()));
			if(accountToPayCreditCard != null)
				accountToPayCreditCard.setVisibility(acc.isAccountToPayCreditCard() ? View.VISIBLE : View.GONE);
		}

		@Override
		public void onClick(View v) {
			Account acc = getAccount(getAdapterPosition());
			if(acc != null) {
				if(callback == null) {
					Intent i = new Intent(itemView.getContext(), ShowAccountActivity.class);
					i.putExtra(ShowAccountActivity.KEY_ACCOUNT_UUID, acc.getUuid());
					if(month != null)
						i.putExtra(ShowAccountActivity.KEY_ACCOUNT_MONTH_TO_SHOW, month.getMillis());
					itemView.getContext().startActivity(i);
				} else {
					callback.onAccountSelected(acc);
				}
			}
		}
	}

	public AccountAdapter() {
		presenter = new AccountAdapterPresenter(this, new AccountRepository(new RepositoryImpl<Account>(MyApplication.getContext())), format);
	}

	public void refreshData() {
		presenter.loadAccountsAsync(format);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
		return new ViewHolder(v);
	}

	private int getLayout() {
		return format == Format.NORMAL ? R.layout.row_account : R.layout.row_account_simplified;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(getAccount(position));
	}

	@Override
	public int getItemCount() {
		return presenter.getAccountsSize();
	}

	@Nullable
	public Account getAccount(int position) {
		return presenter.getAccount(position);
	}

	public void setCallback(AccountAdapterCallback callback) {
		this.callback = callback;
	}

	public void setFormat(Format format) {
		this.format = format;
		refreshData();
	}

	public void setMonth(DateTime month) {
		this.month = month;
	}
}
