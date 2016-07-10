package br.com.jonathanzanella.myexpenses.receipt;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.ListAccountActivity;
import br.com.jonathanzanella.myexpenses.user.SelectUserView;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import br.com.jonathanzanella.myexpenses.source.ListSourceActivity;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.source.Source;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditReceiptActivity extends BaseActivity {
	public static final String KEY_RECEIPT_UUID = "KeyReceiptUuid";
	private static final int REQUEST_SELECT_SOURCE = 1001;
	private static final int REQUEST_SELECT_ACCOUNT = 1002;

	@Bind(R.id.act_edit_receipt_name)
	EditText editName;
	@Bind(R.id.act_edit_receipt_date)
	EditText editDate;
	@Bind(R.id.act_edit_receipt_income)
	EditText editIncome;
	@Bind(R.id.act_edit_receipt_source)
	EditText editSource;
	@Bind(R.id.act_edit_receipt_account)
	EditText editAccount;
	@Bind(R.id.act_edit_receipt_repetition)
	EditText editRepetition;
	@Bind(R.id.act_edit_receipt_installment)
	EditText editInstallment;
	@Bind(R.id.act_edit_receipt_show_in_resume)
	CheckBox checkShowInResume;
	@Bind(R.id.act_edit_receipt_user)
	SelectUserView selectUserView;

	private Receipt receipt;
	private DateTime date;
	private Source source;
	private Account account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_receipt);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		editIncome.addTextChangedListener(new CurrencyTextWatch(editIncome));

		if(receipt == null) {
			initData();
		} else {
			setData();
		}
	}

	private void initData() {
		date = DateTime.now();
		onBalanceDateChanged();
		if(source != null)
			onSourceSelected();
		if(account != null)
			onAccountSelected();
	}

	private void setData() {
		editName.setText(receipt.getName());
		editIncome.setText(NumberFormat.getCurrencyInstance().format(receipt.getIncome() / 100.0));
		if(receipt.isCredited())
			editIncome.setTextColor(getColor(R.color.value_unpaid));
		editSource.setText(receipt.getSource().getName());
		source = receipt.getSource();
		checkShowInResume.setChecked(receipt.isShowInResume());
		onSourceSelected();
		account = receipt.getAccount();
		onAccountSelected();
		date = receipt.getDate();
		onBalanceDateChanged();
		selectUserView.setSelectedUser(receipt.getUserUuid());
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);

		if(extras == null)
			return;

		if(extras.containsKey(KEY_RECEIPT_UUID))
			receipt = Receipt.find(extras.getString(KEY_RECEIPT_UUID));
		if(extras.containsKey(ListSourceActivity.KEY_SOURCE_SELECTED_UUID))
			source = Source.find(extras.getString(ListSourceActivity.KEY_SOURCE_SELECTED_UUID));
		if(extras.containsKey(ListAccountActivity.KEY_ACCOUNT_SELECTED_UUID))
			account = Account.find(extras.getString(ListAccountActivity.KEY_ACCOUNT_SELECTED_UUID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(receipt != null)
			outState.putString(KEY_RECEIPT_UUID, receipt.getUuid());
		if(source != null)
			outState.putString(ListSourceActivity.KEY_SOURCE_SELECTED_UUID, source.getUuid());
		if(account != null)
			outState.putString(ListAccountActivity.KEY_ACCOUNT_SELECTED_UUID, account.getUuid());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_SELECT_SOURCE: {
				if(resultCode == RESULT_OK) {
					source = Source.find(data.getStringExtra(ListSourceActivity.KEY_SOURCE_SELECTED_UUID));
					if(source != null)
						onSourceSelected();
				}
				break;
			}
			case REQUEST_SELECT_ACCOUNT: {
				if(resultCode == RESULT_OK) {
					account = Account.find(data.getStringExtra(ListAccountActivity.KEY_ACCOUNT_SELECTED_UUID));
					if(account != null)
						onAccountSelected();
				}
				break;
			}
		}
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

	@OnClick(R.id.act_edit_receipt_date)
	void onBalanceDate() {
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				date = date.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);
				onBalanceDateChanged();
			}
		}, date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth()).show();
	}

	private void onBalanceDateChanged() {
		editDate.setText(Receipt.sdf.format(date.toDate()));
	}

	private void onSourceSelected() {
		editSource.setText(source.getName());
	}

	private void onAccountSelected() {
		editAccount.setText(account.getName());
	}

	@OnClick(R.id.act_edit_receipt_source)
	void onSource() {
		startActivityForResult(new Intent(this, ListSourceActivity.class), REQUEST_SELECT_SOURCE);
	}

	@OnClick(R.id.act_edit_receipt_account)
	void onAccount() {
		if(receipt == null)
			startActivityForResult(new Intent(this, ListAccountActivity.class), REQUEST_SELECT_ACCOUNT);
	}

	private void save() {
		int installment = Integer.parseInt(editInstallment.getText().toString());
		if(receipt == null)
			receipt = new Receipt();
		String originalName = editName.getText().toString();
		if(installment == 1)
			receipt.setName(originalName);
		else
			receipt.setName(String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, 1, installment));
		receipt.setDate(date);
		receipt.setIncome(Integer.parseInt(editIncome.getText().toString().replaceAll("[^\\d]", "")));
		receipt.setSource(source);
		receipt.setAccount(account);
		receipt.setShowInResume(checkShowInResume.isChecked());
		receipt.setUserUuid(selectUserView.getSelectedUser());
		receipt.save();

		int repetition = installment;
		if(repetition == 1)
			repetition = Integer.parseInt(editRepetition.getText().toString());
		for(int i = 1; i < repetition; i++) {
			if(installment != 1)
				receipt.setName(String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, i + 1, installment));
			receipt.repeat();
			receipt.save();
		}

		Intent i = new Intent();
		i.putExtra(KEY_RECEIPT_UUID, receipt.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}
}
