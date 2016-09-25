package br.com.jonathanzanella.myexpenses.source;

import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/27/16.
 */

public class SourceRepository {
	private From<Source> initQuery() {
		return SQLite.select().from(Source.class);
	}

	public Source find(String uuid) {
		return initQuery().where(Source_Table.uuid.eq(uuid)).querySingle();
	}

	List<Source> userSources() {
		return initQuery()
				.where(Source_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.orderBy(Source_Table.name, true)
				.queryList();
	}

	public OperationResult save(Source source) {
		OperationResult result = new OperationResult();
		if(StringUtils.isEmpty(source.getName()))
			result.addError(ValidationError.NAME);
		if(result.isValid())
			source.save();
		return result;
	}
}