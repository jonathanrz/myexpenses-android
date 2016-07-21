package br.com.jonathanzanella.myexpenses.database.migrations.version5;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;

/**
 * Created by jzanella on 7/10/16.
 */
@Migration(version = 5, database = MyDatabase.class)
public class AddRemovedToReceipt extends AlterTableMigration<Receipt> {
	public AddRemovedToReceipt(Class<Receipt> table) {
		super(table);
	}

	@Override
	public void onPreMigrate() {
		addColumn(SQLiteType.INTEGER, "removed");
	}
}
