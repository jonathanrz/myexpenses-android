package br.com.jonathanzanella.myexpenses.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.model.Account;
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
		receiptIncome.setText(NumberFormat.getCurrencyInstance().format(receipt.getIncome() / 100.0));
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
		getMenuInflater().inflate(R.menu.edit_delete, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit: {
				Intent i = new Intent(this, EditReceiptActivity.class);
				i.putExtra(EditReceiptActivity.KEY_RECEIPT_ID, receipt.getId());
				startActivity(i);
				break;
			}
			case R.id.action_delete: {
				new AlertDialog.Builder(this)
						.setTitle(android.R.string.dialog_alert_title)
						.setMessage(R.string.message_confirm_deletion)
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();

								Account a = receipt.getAccount();
								a.credit(receipt.getIncome() * -1);
								a.save();
								receipt.delete();
								Intent i = new Intent();
								setResult(RESULT_OK, i);
								finish();
							}
						})
						.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
						.show();
				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
