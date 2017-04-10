package br.com.jonathanzanella.myexpenses.account.transactions;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MonthTransactionsView extends BaseView implements MonthTransactionsContractView {
	public final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM/yy", Locale.getDefault());

	@BindView(R.id.view_month_transactions_list)
	RecyclerView list;
	@BindView(R.id.view_month_transactions_header)
	TextView header;
	@BindView(R.id.view_month_transactions_balance)
	TextView balance;

	int singleRowHeight;

	String monthTransactionsTemplate;

	private MonthTransactionsPresenter presenter;
	private LoadTransactionsCallback loadTransactionsCallback;

	public MonthTransactionsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MonthTransactionsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init() {
		singleRowHeight = getContext().getResources().getDimensionPixelSize(R.dimen.single_row_height);

		monthTransactionsTemplate = getResources().getString(R.string.month_transactions);
		inflate(getContext(), R.layout.view_account_month_transactions, this);
		ButterKnife.bind(this);
		presenter = new MonthTransactionsPresenter(getContext(), this);

		list.setAdapter(presenter.getAdapter());
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setNestedScrollingEnabled(false);
	}

	void showBalance(final Account account, final DateTime month, int balance) {
		header.setText(monthTransactionsTemplate.concat(" ").concat(simpleDateFormat.format(month.toDate())));
		presenter.showBalance(account, month, balance);
	}

	@Override
	public void onBalanceUpdated(int balance) {
		this.balance.setText(CurrencyHelper.format(balance));
		//noinspection deprecation
		this.balance.setTextColor(getResources().getColor(balance >= 0 ? R.color.value_unreceived : R.color.value_unpaid));

		if(loadTransactionsCallback != null)
			loadTransactionsCallback.onTransactionsLoaded(balance);

		list.setMinimumHeight(list.getAdapter().getItemCount() * singleRowHeight);
	}

	public void setLoadTransactionsCallback(LoadTransactionsCallback loadTransactionsCallback) {
		this.loadTransactionsCallback = loadTransactionsCallback;
	}
}
