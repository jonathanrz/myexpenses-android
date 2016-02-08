package br.com.jonathanzanella.myexpenses.model;

import android.util.Log;

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
	private static final String LOG_TAG = "Card";
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
	public static List<Card> creditCards() {
		return initQuery()
				.where(Card_Table.type.eq(CardType.CREDIT))
				.queryList();
	}

	private static From<Card> initQuery() {
		return SQLite.select().from(Card.class);
	}

	public static Card find(long id) {
		return initQuery().where(Card_Table.id.eq(id)).querySingle();
	}

	public Account getAccount() {
		return Account.find(accountId);
	}

	public void setAccount(Account account) {
		accountId = account.getId();
	}

	@Override
	public ChargeableType getChargeableType() {
		switch (type) {
			case CREDIT:
				return ChargeableType.CARD;
			case DEBIT:
				return ChargeableType.DEBIT_CARD;
		}

		Log.e(LOG_TAG, "new card type?");
		return ChargeableType.DEBIT_CARD;
	}

	@Override
	public boolean canBePaidNextMonth() {
		return (type == CardType.CREDIT);
	}

	@Override
	public void debit(int value) {
		if(type == CardType.DEBIT) {
			Account a = getAccount();
			a.debit(value);
			a.save();
		}
	}

	@Override
	public void credit(int value) {
		if(type == CardType.DEBIT) {
			Account a = getAccount();
			a.credit(value);
			a.save();
		}
	}
}
