package br.com.jonathanzanella.myexpenses.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import br.com.jonathanzanella.myexpenses.expense.EditExpenseActivity;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowCardActivity extends BaseActivity {
	public static final String KEY_CREDIT_CARD_UUID = "KeyCreateCardUuid";

	@Bind(R.id.act_show_card_name)
	TextView cardName;
	@Bind(R.id.act_show_card_account)
	TextView cardAccount;
	@Bind(R.id.act_show_card_type)
	TextView cardType;

	private Card card;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_card);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setData();
	}

	private void setData() {
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

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		if(extras.containsKey(KEY_CREDIT_CARD_UUID))
			card = Card.find(extras.getString(KEY_CREDIT_CARD_UUID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CREDIT_CARD_UUID, card.getUuid());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(card != null) {
			card = Card.find(card.getUuid());
			setData();
		}
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
				i.putExtra(EditCardActivity.KEY_CARD_UUID, card.getUuid());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@OnClick(R.id.act_show_card_pay_credit_card_bill)
	public void payCreditCardBill() {
		List<Expense> expenses = card.creditCardBills(DateTime.now().minusMonths(1));
		int totalExpense = 0;
		for (Expense expense : expenses) {
			totalExpense += expense.getValue();
			expense.setCharged(true);
			expense.save();
		}

		if(totalExpense == 0) {
			Toast.makeText(this, MyApplication.getContext().getString(R.string.empty_invoice), Toast.LENGTH_SHORT).show();
			return;
		}

		Expense e = new Expense();
		e.setName(MyApplication.getContext().getString(R.string.invoice) + " " + card.getName());
		e.setValue(totalExpense);
		e.save();

		Intent i = new Intent(this, EditExpenseActivity.class);
		i.putExtra(EditExpenseActivity.KEY_EXPENSE_UUID, e.getUuid());
		startActivity(i);
	}
}
