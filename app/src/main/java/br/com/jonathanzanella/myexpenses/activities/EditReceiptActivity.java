package br.com.jonathanzanella.myexpenses.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;

import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.model.Receipt;
import br.com.jonathanzanella.myexpenses.model.Source;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditReceiptActivity extends BaseActivity {
	public static final String KEY_RECEIPT_ID = "KeyReceiptId";
	private static final int REQUEST_SELECT_SOURCE = 1001;

	@Bind(R.id.act_edit_receipt_name)
	EditText editName;
	@Bind(R.id.act_edit_receipt_date)
	EditText editDate;
	@Bind(R.id.act_edit_receipt_income)
	EditText editIncome;
	@Bind(R.id.act_edit_receipt_source)
	EditText editSource;

	private Receipt receipt;
	private DateTime date;
	private Source source;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_receipt);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		date = DateTime.now();
		onBalanceDateChanged();
		editIncome.addTextChangedListener(new TextWatcher() {
			String current;
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!s.toString().equals(current)){
					editIncome.removeTextChangedListener(this);

					String cleanString = s.toString().replaceAll("[R$,.]", "");

					double parsed = Double.parseDouble(cleanString);
					String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

					current = formatted;
					editIncome.setText(formatted);
					editIncome.setSelection(formatted.length());

					editIncome.addTextChangedListener(this);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});

		if(receipt != null) {
			editName.setText(receipt.getName());
			editIncome.setText(NumberFormat.getCurrencyInstance().format(receipt.getIncome() / 100));
			editSource.setText(receipt.getSource().getName());
			source = receipt.getSource();
			onSourceSelected();
		}
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
		if(receipt != null)
			outState.putLong(KEY_RECEIPT_ID, receipt.getId());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_SELECT_SOURCE: {
				if(resultCode == RESULT_OK) {
					source = Source.find(data.getLongExtra(ListSourceActivity.KEY_SOURCE_SELECTED_ID, 0L));
					if(source != null)
						onSourceSelected();
				}
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
				date = date.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfYear(dayOfMonth);
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

	@OnClick(R.id.act_edit_receipt_source)
	void onSource() {
		startActivityForResult(new Intent(this, ListSourceActivity.class), REQUEST_SELECT_SOURCE);
	}

	private void save() {
		if(receipt == null)
			receipt = new Receipt();
		receipt.setName(editName.getText().toString());
		receipt.setDate(date);
		receipt.setIncome(Integer.parseInt(editIncome.getText().toString().replaceAll("[^\\d]", "")));
		receipt.setSource(source);
		receipt.save();

		Intent i = new Intent();
		i.putExtra(KEY_RECEIPT_ID, receipt.getId());
		setResult(RESULT_OK, i);
		finish();
	}
}
