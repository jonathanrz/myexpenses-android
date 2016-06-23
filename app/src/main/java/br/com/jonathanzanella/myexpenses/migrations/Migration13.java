package br.com.jonathanzanella.myexpenses.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.models.Source;

/**
 * Created by Jonathan Zanella on 12/02/16.
 */
@Migration(version = 13, database = MyDatabase.class)
public class Migration13 extends AlterTableMigration<Source> {

	public Migration13() {
		super(Source.class);
	}

	@Override
	public void onPreMigrate() {
		addColumn(SQLiteType.TEXT, "uuid");
	}
}