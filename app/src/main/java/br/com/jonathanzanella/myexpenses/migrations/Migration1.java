package br.com.jonathanzanella.myexpenses.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.models.Receipt;

/**
 * Created by Jonathan Zanella on 12/02/16.
 */
@Migration(version = 2, database = MyDatabase.class)
public class Migration1 extends AlterTableMigration<Receipt> {

	public Migration1() {
		super(Receipt.class);
	}

	@Override
	public void onPreMigrate() {
		// Simple ALTER TABLE migration wraps the statements into a nice builder notation
		addColumn(SQLiteType.INTEGER, "ignoreInResume");
	}
}