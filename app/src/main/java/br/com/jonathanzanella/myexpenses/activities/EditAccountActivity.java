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
import org.joda.time.LocalDate;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.model.Account;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditAccountActivity extends BaseActivity {
    public static final String KEY_ACCOUNT_ID = "KeyAccountId";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    @Bind(R.id.act_edit_account_name)
    EditText editName;
    @Bind(R.id.act_edit_account_balance)
    EditText editBalance;
    @Bind(R.id.act_edit_account_balance_date)
    EditText editBalanceDate;

    private LocalDate balanceDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        balanceDate = LocalDate.now();
        onBalanceDateChanged();
        editBalance.addTextChangedListener(new TextWatcher() {
            String current;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    editBalance.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;
                    editBalance.setText(formatted);
                    editBalance.setSelection(formatted.length());

                    editBalance.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    @OnClick(R.id.act_edit_account_balance_date)
    void onBalanceDate() {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                balanceDate = balanceDate.withYear(year).withMonthOfYear(monthOfYear).withDayOfYear(dayOfMonth);
                onBalanceDateChanged();
            }
        }, balanceDate.getYear(), balanceDate.getMonthOfYear(), balanceDate.getDayOfMonth()).show();
    }

    private void onBalanceDateChanged() {
        editBalanceDate.setText(sdf.format(balanceDate.toDate()));
    }

    private void save() {
        Account account = new Account();
        account.setName(editName.getText().toString());
        account.setBalance(Integer.parseInt(editBalance.getText().toString().replaceAll("[^\\d]", "")));
        account.setBalanceDate(DateTime.now());
        account.save();

        Intent i = new Intent();
        i.putExtra(KEY_ACCOUNT_ID, account.getId());
        setResult(RESULT_OK, i);
        finish();
    }
}
