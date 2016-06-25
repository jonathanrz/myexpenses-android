package br.com.jonathanzanella.myexpenses.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;

/**
 * Created by Jonathan Zanella on 12/02/16.
 */
@Migration(version = 20, database = MyDatabase.class)
public class Migration20 extends AlterTableMigration<Receipt> {

	public Migration20() {
		super(Receipt.class);
	}

	@Override
	public void onPreMigrate() {
		addColumn(SQLiteType.TEXT, "uuid");
	}
}