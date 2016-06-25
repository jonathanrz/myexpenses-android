package br.com.jonathanzanella.myexpenses.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.expense.Expense;

/**
 * Created by Jonathan Zanella on 12/02/16.
 */
@Migration(version = 5, database = MyDatabase.class)
public class Migration3 extends AlterTableMigration<Expense> {

	public Migration3() {
		super(Expense.class);
	}

	@Override
	public void onPreMigrate() {
		// Simple ALTER TABLE migration wraps the statements into a nice builder notation
		addColumn(SQLiteType.INTEGER, "ignoreInOverview");
	}
}