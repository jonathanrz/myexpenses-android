package br.com.jonathanzanella.myexpenses.card;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.expense.EditExpenseActivity;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowCardActivity extends BaseActivity implements CardContract.View {
	public static final String KEY_CREDIT_CARD_UUID = "KeyCreateCardUuid";

	@Bind(R.id.act_show_card_name)
	TextView cardName;
	@Bind(R.id.act_show_card_account)
	TextView cardAccount;
	@Bind(R.id.act_show_card_type)
	TextView cardType;

	private CardPresenter presenter;

	public ShowCardActivity() {
		this.presenter = new CardPresenter(new CardRepository(), new AccountRepository());
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_card);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		presenter.viewUpdated(false);
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);

		if(extras != null && extras.containsKey(KEY_CREDIT_CARD_UUID))
			presenter.loadCard(extras.getString(KEY_CREDIT_CARD_UUID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CREDIT_CARD_UUID, presenter.getUuid());
	}

	@Override
	protected void onStart() {
		super.onStart();
		presenter.attachView(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		presenter.detachView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
				Intent i = new Intent(this, EditCardActivity.class);
				i.putExtra(EditCardActivity.KEY_CARD_UUID, presenter.getUuid());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@OnClick(R.id.act_show_card_pay_credit_card_bill)
	public void payCreditCardBill() {
		new AsyncTask<Void, Void, Expense>() {
			@Override
			protected Expense doInBackground(Void... voids) {
				return presenter.generateCreditCardBill();
			}

			@Override
			protected void onPostExecute(Expense expense) {
				super.onPostExecute(expense);
				if(expense != null) {
					Intent i = new Intent(ShowCardActivity.this, EditExpenseActivity.class);
					i.putExtra(EditExpenseActivity.KEY_EXPENSE_UUID, expense.getUuid());
					startActivity(i);
				} else {
					Context ctx = ShowCardActivity.this;
					Toast.makeText(ctx, ctx.getString(R.string.empty_invoice), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute();

	}

	@Override
	public void showCard(Card card) {
		cardName.setText(card.getName());
		cardAccount.setText(card.getAccount().getName());
		switch (card.getType()) {
			case CREDIT:
				cardType.setText(R.string.credit);
				break;
			case DEBIT:
				cardType.setText(R.string.debit);
				break;
		}
	}
}
