package br.com.jonathanzanella.myexpenses.bill;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.CountingIdlingResource;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowBillActivity extends BaseActivity implements BillContract.View {
	public static final String KEY_BILL_UUID = "KeyBillUuid";

	@Bind(R.id.act_show_bill_name)
	TextView billName;
	@Bind(R.id.act_show_bill_amount)
	TextView billAmount;
	@Bind(R.id.act_show_bill_due_date)
	TextView billDueDate;
	@Bind(R.id.act_show_bill_init_date)
	TextView billInitDate;
	@Bind(R.id.act_show_bill_end_date)
	TextView billEndDate;

	private BillPresenter presenter;

	public ShowBillActivity() {
		ExpenseRepository expenseRepository = new ExpenseRepository(new Repository<Expense>(this));
		presenter = new BillPresenter(new BillRepository(new Repository<Bill>(this), expenseRepository), new CountingIdlingResource("ShowBillActivity"));
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_bill);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		presenter.onViewUpdated(false);
	}

	@Override
	protected void storeBundle(final Bundle extras) {
		super.storeBundle(extras);

		if(extras != null && extras.containsKey(KEY_BILL_UUID)) {
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... voids) {
					presenter.loadBill(extras.getString(KEY_BILL_UUID));
					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) {
					super.onPostExecute(aVoid);
					presenter.onViewUpdated(false);
				}
			}.execute();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_BILL_UUID, presenter.getUuid());
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
				Intent i = new Intent(this, EditBillActivity.class);
				i.putExtra(EditBillActivity.KEY_BILL_UUID, presenter.getUuid());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void showBill(Bill bill) {
		billName.setText(bill.getName());
		billAmount.setText(NumberFormat.getCurrencyInstance().format(bill.getAmount() / 100.0));
		billDueDate.setText(String.valueOf(bill.getDueDate()));
		billInitDate.setText(Bill.sdf.format(bill.getInitDate().toDate()));
		billEndDate.setText(Bill.sdf.format(bill.getEndDate().toDate()));
	}
}