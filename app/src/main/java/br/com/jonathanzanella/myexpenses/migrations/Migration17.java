package br.com.jonathanzanella.myexpenses.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.models.Bill;

/**
 * Created by Jonathan Zanella on 12/02/16.
 */
@Migration(version = 17, database = MyDatabase.class)
public class Migration17 extends AlterTableMigration<Bill> {

	public Migration17() {
		super(Bill.class);
	}

	@Override
	public void onPreMigrate() {
		addColumn(SQLiteType.TEXT, "uuid");
	}
}