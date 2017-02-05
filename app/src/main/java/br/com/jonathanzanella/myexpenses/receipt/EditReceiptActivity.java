package br.com.jonathanzanella.myexpenses.receipt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.account.ListAccountActivity;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.source.ListSourceActivity;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
import br.com.jonathanzanella.myexpenses.user.SelectUserView;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditReceiptActivity extends BaseActivity implements ReceiptContract.EditView {
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

	private ReceiptPresenter presenter = new ReceiptPresenter(new ReceiptRepository(), new SourceRepository(new Repository<Source>(this)), new AccountRepository(new Repository<Account>(MyApplication.getContext())));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_receipt);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		editIncome.addTextChangedListener(new CurrencyTextWatch(editIncome));
		presenter.attachView(this);
		presenter.viewUpdated(false);
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);

		if(extras != null)
			presenter.storeBundle(extras);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		presenter.onSaveInstanceState(outState);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		presenter.attachView(this);
		switch (requestCode) {
			case REQUEST_SELECT_SOURCE: {
				if(resultCode == RESULT_OK) {
					String sourceUUid = data.getStringExtra(ListSourceActivity.KEY_SOURCE_SELECTED_UUID);
					if(sourceUUid != null)
						presenter.onSourceSelected(sourceUUid);
				}
				break;
			}
			case REQUEST_SELECT_ACCOUNT: {
				if(resultCode == RESULT_OK) {
					String accountUuid = data.getStringExtra(ListAccountActivity.Companion.getKEY_ACCOUNT_SELECTED_UUID());
					if(accountUuid != null)
						presenter.onAccountSelected(accountUuid);
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
	void onDate() {
		presenter.onDate(this);
	}

	@Override
	public void onDateChanged(DateTime date) {
		editDate.setText(Receipt.sdf.format(date.toDate()));
	}

	@Override
	public void onSourceSelected(Source source) {
		editSource.setText(source.getName());
	}

	@Override
	public void onAccountSelected(Account account) {
		editAccount.setText(account.getName());
	}

	@Override
	public int getInstallment() {
		return Integer.parseInt(editInstallment.getText().toString());
	}

	@Override
	public int getRepetition() {
		return Integer.parseInt(editRepetition.getText().toString());
	}

	@OnClick(R.id.act_edit_receipt_source)
	void onSource() {
		startActivityForResult(new Intent(this, ListSourceActivity.class), REQUEST_SELECT_SOURCE);
	}

	@OnClick(R.id.act_edit_receipt_account)
	void onAccount() {
		if(!presenter.hasReceipt())
			startActivityForResult(new Intent(this, ListAccountActivity.class), REQUEST_SELECT_ACCOUNT);
	}

	private void save() {
		presenter.save();
	}

	@Override
	public Receipt fillReceipt(Receipt receipt) {
		receipt.setName(editName.getText().toString());
		String income = editIncome.getText().toString().replaceAll("[^\\d]", "");
		if(!StringUtils.isEmpty(income))
			receipt.setIncome(Integer.parseInt(income));
		receipt.setShowInResume(checkShowInResume.isChecked());
		receipt.setUserUuid(selectUserView.getSelectedUser());
		return receipt;
	}

	@Override
	public void finishView() {
		Intent i = new Intent();
		i.putExtra(KEY_RECEIPT_UUID, presenter.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}

	@Override
	public void showError(ValidationError error) {
		switch (error) {
			case NAME:
				editName.setError(getString(error.getMessage()));
				break;
			case AMOUNT:
				editIncome.setError(getString(error.getMessage()));
				break;
			case SOURCE:
				editSource.setError(getString(error.getMessage()));
				break;
			case ACCOUNT:
				editAccount.setError(getString(error.getMessage()));
				break;
			default:
				Log.error(this.getClass().getName(), "Validation unrecognized, field:" + error);
		}
	}

	@Override
	@UiThread
	public void showReceipt(final Receipt receipt) {
		editName.setText(receipt.getName());
		editIncome.setText(NumberFormat.getCurrencyInstance().format(receipt.getIncome() / 100.0));
		if(receipt.isCredited())
			editIncome.setTextColor(getResources().getColor(R.color.value_unpaid));

		new AsyncTask<Void, Void, Source>() {

			@Override
			protected Source doInBackground(Void... voids) {
				return receipt.getSource();
			}

			@Override
			protected void onPostExecute(Source source) {
				super.onPostExecute(source);
				editSource.setText(source.getName());
			}
		}.execute();

		checkShowInResume.setChecked(receipt.isShowInResume());
		selectUserView.setSelectedUser(receipt.getUserUuid());
	}
}
