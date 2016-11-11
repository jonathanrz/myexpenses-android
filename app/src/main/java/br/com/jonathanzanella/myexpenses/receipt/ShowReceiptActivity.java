package br.com.jonathanzanella.myexpenses.receipt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowReceiptActivity extends BaseActivity implements ReceiptContract.View {
	public static final String KEY_RECEIPT_UUID = ReceiptPresenter.KEY_RECEIPT_UUID;

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

	private ReceiptPresenter presenter = new ReceiptPresenter(new ReceiptRepository(), new SourceRepository(new Repository<Source>(this)), new AccountRepository());

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_receipt);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
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
	protected void onResume() {
		super.onResume();
		presenter.refreshReceipt();
	}

	@Override
	protected void onStop() {
		super.onStop();
		presenter.detachView();
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
				presenter.edit(this);
				break;
			}
			case R.id.action_delete: {
				presenter.delete(this);
				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void showReceipt(Receipt receipt) {
		receiptName.setText(receipt.getName());
		receiptDate.setText(Receipt.sdf.format(receipt.getDate().toDate()));
		receiptIncome.setText(NumberFormat.getCurrencyInstance().format(receipt.getIncome() / 100.0));
		receiptSource.setText(receipt.getSource().getName());
		receiptAccount.setText(receipt.getAccount().getName());
		receiptShowInResume.setText(receipt.isShowInResume() ? R.string.yes : R.string.no);
	}
}
