package br.com.jonathanzanella.myexpenses.database.migrations.version4;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.expense.Expense;

/**
 * Created by jzanella on 7/10/16.
 */
@Migration(version = 4, database = MyDatabase.class)
public class AddRemovedExpense extends AlterTableMigration<Expense> {
	public AddRemovedExpense(Class<Expense> table) {
		super(table);
	}

	@Override
	public void onPreMigrate() {
		addColumn(SQLiteType.INTEGER, "removed");
	}
}
