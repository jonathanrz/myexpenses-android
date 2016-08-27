package br.com.jonathanzanella.myexpenses.source;

import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Created by jzanella on 8/27/16.
 */

class SourceRepository {
	private From<Source> initQuery() {
		return SQLite.select().from(Source.class);
	}

	Source find(String uuid) {
		return initQuery().where(Source_Table.uuid.eq(uuid)).querySingle();
	}

	void save(Source source) {
		source.save();
	}
}
