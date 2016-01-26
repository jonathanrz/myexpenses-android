package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.model.Account;
import butterknife.Bind;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditAccountActivity extends BaseActivity {
    public static final String KEY_ACCOUNT_ID = "KeyAccountId";
    @Bind(R.id.act_edit_account_name)
    EditText editName;
    @Bind(R.id.act_edit_account_balance)
    EditText editBalance;
    @Bind(R.id.act_edit_account_balance_date)
    EditText editBalanceDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
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
