package br.com.jonathanzanella.myexpenses.card;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapter;
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapterBuilder;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.BindView;

public class CreditCardInvoiceActivity extends BaseActivity {
	public static final String KEY_CREDIT_CARD_UUID = "KeyCreateCardUuid";
	public static final String KEY_INIT_DATE = "KeyInitDate";

	@BindView(R.id.tabs)
	TabLayout tabs;
	@BindView(R.id.act_credit_card_invoice_pager)
	ViewPager pager;

	private Card card;
	private DateTime initDate;
	private CardRepository cardRepository;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credit_card_invoice);

		cardRepository = new CardRepository(new RepositoryImpl<Card>(getContext()),
				new ExpenseRepository(new RepositoryImpl<Expense>(getContext())));
	}

	@Override
	@UiThread
	protected void storeBundle(final Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				if(extras.containsKey(KEY_CREDIT_CARD_UUID))
					card = cardRepository.find(extras.getString(KEY_CREDIT_CARD_UUID));
				if(extras.containsKey(KEY_INIT_DATE))
					initDate = (DateTime) extras.getSerializable(KEY_INIT_DATE);
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);

				MonthlyPagerAdapter adapter = new MonthlyPagerAdapter(CreditCardInvoiceActivity.this,
						new MonthlyPagerAdapterBuilder() {
					@Override
					public BaseView buildView(Context ctx, DateTime date) {
						return new CreditCardInvoiceView(ctx, card, date);
					}
				});
				pager.setAdapter(adapter);
				pager.setCurrentItem(adapter.getDatePosition(initDate));
				tabs.setupWithViewPager(pager);
			}
		}.execute();
	}
}
