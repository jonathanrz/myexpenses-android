package br.com.jonathanzanella.myexpenses.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.models.Card;

/**
 * Created by Jonathan Zanella on 12/02/16.
 */
@Migration(version = 22, database = MyDatabase.class)
public class Migration22 extends AlterTableMigration<Card> {

	public Migration22() {
		super(Card.class);
	}

	@Override
	public void onPreMigrate() {
		addColumn(SQLiteType.TEXT, "serverId");
		addColumn(SQLiteType.INTEGER, "createdAt");
		addColumn(SQLiteType.INTEGER, "updatedAt");
		addColumn(SQLiteType.INTEGER, "sync");
	}
}