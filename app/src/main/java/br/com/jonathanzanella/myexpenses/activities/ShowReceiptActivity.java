package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.model.Receipt;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowReceiptActivity extends BaseActivity {
	public static final String KEY_RECEIPT_ID = "KeyReceiptId";

	@Bind(R.id.act_show_receipt_name)
	TextView receiptName;
	@Bind(R.id.act_show_receipt_date)
	TextView receiptDate;
	@Bind(R.id.act_show_receipt_income)
	TextView receiptIncome;
	@Bind(R.id.act_show_receipt_source)
	TextView receiptSource;
	@Bind(R.id.act_show_receipt_account)
	TextView receiptAccount;

	private Receipt receipt;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_receipt);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setData();
	}

	private void setData() {
		receiptName.setText(receipt.getName());
		receiptDate.setText(Receipt.sdf.format(receipt.getDate().toDate()));
		receiptIncome.setText(NumberFormat.getCurrencyInstance().format(receipt.getIncome() / 100));
		receiptSource.setText(receipt.getSource().getName());
		receiptAccount.setText(receipt.getAccount().getName());
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		if(extras.containsKey(KEY_RECEIPT_ID))
			receipt = Receipt.find(extras.getLong(KEY_RECEIPT_ID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(KEY_RECEIPT_ID, receipt.getId());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(receipt != null) {
			receipt = Receipt.find(receipt.getId());
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
				Intent i = new Intent(this, EditReceiptActivity.class);
				i.putExtra(EditReceiptActivity.KEY_RECEIPT_ID, receipt.getId());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
