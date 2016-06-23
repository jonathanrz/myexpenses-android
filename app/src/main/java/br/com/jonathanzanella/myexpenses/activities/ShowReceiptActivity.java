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
import br.com.jonathanzanella.myexpenses.models.Account;
import br.com.jonathanzanella.myexpenses.models.Receipt;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowReceiptActivity extends BaseActivity {
	public static final String KEY_RECEIPT_UUID = "KeyReceiptUuid";

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
	@Bind(R.id.act_show_receipt_show_in_resume)
	TextView receiptShowInResume;

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
		receiptShowInResume.setText(receipt.isShowInResume() ? R.string.yes : R.string.no);
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		if(extras.containsKey(KEY_RECEIPT_UUID))
			receipt = Receipt.find(extras.getString(KEY_RECEIPT_UUID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_RECEIPT_UUID, receipt.getUuid());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(receipt != null) {
			receipt = Receipt.find(receipt.getUuid());
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
				i.putExtra(EditReceiptActivity.KEY_RECEIPT_UUID, receipt.getUuid());
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
