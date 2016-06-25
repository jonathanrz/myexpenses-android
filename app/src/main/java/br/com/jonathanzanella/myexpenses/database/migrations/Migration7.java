package br.com.jonathanzanella.myexpenses.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.Source_Table;

/**
 * Created by Jonathan Zanella on 12/02/16.
 */
@Migration(version = 7, database = MyDatabase.class)
public class Migration7 extends BaseMigration {

	@Override
	public void migrate(DatabaseWrapper database) {
		SQLite.update(Source.class)
				.set(Source_Table.sync.eq(false))
				.execute(database);
	}
}