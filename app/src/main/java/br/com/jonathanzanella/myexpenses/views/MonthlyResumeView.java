package br.com.jonathanzanella.myexpenses.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapter.AccountAdapter;
import br.com.jonathanzanella.myexpenses.adapter.CreditCardAdapter;
import br.com.jonathanzanella.myexpenses.model.Card;
import br.com.jonathanzanella.myexpenses.model.CardType;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jzanella onCard 2/2/16.
 */
public class MonthlyResumeView extends BaseView {
	@Bind(R.id.view_monthly_resume_accounts)
	RecyclerView accounts;
	@Bind(R.id.view_monthly_resume_credit_card_bills)
	RecyclerView creditCardBills;

	private AccountAdapter accountAdapter;

	public MonthlyResumeView(Context context) {
		super(context);
	}

	public MonthlyResumeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MonthlyResumeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_monthly_resume, this);
		ButterKnife.bind(this);

		initAccount();
		initCreditCard();
	}

	private void initAccount() {
		accountAdapter = new AccountAdapter();
		accountAdapter.setSimplified(true);
		accountAdapter.loadData();

		accounts.setAdapter(accountAdapter);
		accounts.setHasFixedSize(true);
		accounts.setLayoutManager(new GridLayoutManager(getContext(), 3));
	}

	private void initCreditCard() {
		Card creditCard = null;
		for (Card card : Card.all()) {
			if(card.getType() == CardType.CREDIT) {
				creditCard = card;
				break;
			}
		}
		if(creditCard == null)
			return;

		CreditCardAdapter adapter = new CreditCardAdapter();
		adapter.loadData(creditCard, DateTime.now());

		creditCardBills.setAdapter(adapter);
		creditCardBills.setHasFixedSize(true);
		creditCardBills.setLayoutManager(new LinearLayoutManager(getContext()));
	}

	@Override
	public void refreshData() {
		super.refreshData();
		accountAdapter.loadData();
		accountAdapter.notifyDataSetChanged();
	}
}
