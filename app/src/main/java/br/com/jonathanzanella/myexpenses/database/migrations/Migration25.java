package br.com.jonathanzanella.myexpenses.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.Expense_Table;

/**
 * Created by Jonathan Zanella on 12/02/16.
 */
@Migration(version = 25, database = MyDatabase.class)
public class Migration25 extends BaseMigration {

	@Override
	public void migrate(DatabaseWrapper database) {
		SQLite.update(Expense.class)
				.set(Expense_Table.sync.eq(false))
				.execute(database);
	}
}