package br.com.jonathanzanella.myexpenses.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jzanella on 1/31/16.
 */
@Table(database = MyDatabase.class)
public class Card extends BaseModel implements Chargeable {
	@Column
	@PrimaryKey(autoincrement = true) @Getter
	long id;

	@Column @Getter @Setter
	String name;

	@Column @Getter @Setter
	CardType type;

	@Column
	long accountId;

	public static List<Card> all() {
		return initQuery().queryList();
	}

	private static From<Card> initQuery() {
		return SQLite.select().from(Card.class);
	}

	public static Card find(long id) {
		return initQuery().where(Source_Table.id.eq(id)).querySingle();
	}

	public Account getAccount() {
		return Account.find(accountId);
	}

	public void setAccount(Account account) {
		accountId = account.getId();
	}

	@Override
	public ChargeableType getChargeableType() {
		return ChargeableType.CARD;
	}

	@Override
	public void debit(int value) {
		if(type == CardType.DEBIT) {
			Account a = getAccount();
			a.debit(value);
			a.save();
		}
	}
}
