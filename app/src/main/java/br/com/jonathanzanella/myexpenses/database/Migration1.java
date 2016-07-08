package br.com.jonathanzanella.myexpenses.database;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.expense.Expense;

/**
 * Created by jzanella on 7/7/16.
 */
@Migration(version = 2, database = MyDatabase.class)
public class Migration1 extends AlterTableMigration<Expense> {
	public Migration1(Class<Expense> table) {
		super(table);
	}

	@Override
	public void onPreMigrate() {
		addColumn(SQLiteType.INTEGER, "valueToShowInOverview");
	}
}
