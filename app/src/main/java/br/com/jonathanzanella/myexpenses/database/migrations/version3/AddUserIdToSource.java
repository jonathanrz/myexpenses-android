package br.com.jonathanzanella.myexpenses.database.migrations.version3;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.source.Source;

/**
 * Created by jzanella on 7/10/16.
 */
@Migration(version = 3, database = MyDatabase.class)
public class AddUserIdToSource extends AlterTableMigration<Source> {
	public AddUserIdToSource(Class<Source> table) {
		super(table);
	}

	@Override
	public void onPreMigrate() {
		addColumn(SQLiteType.TEXT, "userUuid");
	}
}
