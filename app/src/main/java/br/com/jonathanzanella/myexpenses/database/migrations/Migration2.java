package br.com.jonathanzanella.myexpenses.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.account.Account;

/**
 * Created by Jonathan Zanella on 12/02/16.
 */
@Migration(version = 3, database = MyDatabase.class)
public class Migration2 extends AlterTableMigration<Account> {

	public Migration2() {
		super(Account.class);
	}

	@Override
	public void onPreMigrate() {
		// Simple ALTER TABLE migration wraps the statements into a nice builder notation
		addColumn(SQLiteType.INTEGER, "accountToPayCreditCard");
	}
}