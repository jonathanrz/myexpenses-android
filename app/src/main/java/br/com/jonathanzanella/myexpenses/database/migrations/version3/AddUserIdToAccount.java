package br.com.jonathanzanella.myexpenses.database.migrations.version3;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;

/**
 * Created by jzanella on 7/10/16.
 */
@Migration(version = 3, database = MyDatabase.class)
public class AddUserIdToAccount extends AlterTableMigration<Account> {
	public AddUserIdToAccount(Class<Account> table) {
		super(table);
	}

	@Override
	public void onPreMigrate() {
		addColumn(SQLiteType.TEXT, "userUuid");
	}
}
