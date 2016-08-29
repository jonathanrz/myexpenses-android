package br.com.jonathanzanella.myexpenses.account;

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

public class AccountRepository {
	private From<Account> initQuery() {
		return SQLite.select().from(Account.class);
	}

	public Account find(String uuid) {
		return initQuery().where(Account_Table.uuid.eq(uuid)).querySingle();
	}

	List<Account> userAccounts() {
		return initQuery().where(Account_Table.userUuid.is(Environment.CURRENT_USER_UUID)).queryList();
	}

	public OperationResult save(Account account) {
		OperationResult result = new OperationResult();
		if(StringUtils.isEmpty(account.getName()))
			result.addError(ValidationError.NAME);
		if(result.isValid())
			account.save();
		return result;
	}
}