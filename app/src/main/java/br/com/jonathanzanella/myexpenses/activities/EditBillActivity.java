package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helper.CurrencyTextWatch;
import br.com.jonathanzanella.myexpenses.model.Bill;
import butterknife.Bind;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditBillActivity extends BaseActivity {
	public static final String KEY_BILL_ID = "KeyBillId";

	@Bind(R.id.act_edit_bill_name)
	EditText editName;
	@Bind(R.id.act_edit_bill_amount)
	EditText editAmount;
	@Bind(R.id.act_edit_bill_due_date)
	EditText editDueDate;

	private Bill bill;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_bill);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		editAmount.addTextChangedListener(new CurrencyTextWatch(editAmount));

		if(bill != null) {
			editName.setText(bill.getName());
			editAmount.setText(NumberFormat.getCurrencyInstance().format(bill.getAmount() / 100.0));
			editDueDate.setText(String.valueOf(bill.getDueDate()));
		}
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		if(extras.containsKey(KEY_BILL_ID))
			bill = Bill.find(extras.getLong(KEY_BILL_ID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(bill != null)
			outState.putLong(KEY_BILL_ID, bill.getId());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_save:
				save();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void save() {
		if(bill == null)
			bill = new Bill();
		bill.setName(editName.getText().toString());
		bill.setAmount(Integer.parseInt(editAmount.getText().toString().replaceAll("[^\\d]", "")));
		bill.setDueDate(Integer.parseInt(editDueDate.getText().toString()));
		bill.save();

		Intent i = new Intent();
		i.putExtra(KEY_BILL_ID, bill.getId());
		setResult(RESULT_OK, i);
		finish();
	}
}
