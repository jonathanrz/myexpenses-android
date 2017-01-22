package br.com.jonathanzanella.myexpenses.card;

import android.content.Context;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.database.MyDatabase;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.Expense_Table;
import br.com.jonathanzanella.myexpenses.helpers.DateHelper;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;
import rx.schedulers.Schedulers;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

/**
 * Created by jzanella on 1/31/16.
 */
@Table(database = MyDatabase.class)
@EqualsAndHashCode(callSuper = false)
public class Card extends BaseModel implements Chargeable, UnsyncModel {
	private AccountRepository accountRepository;
	private static final String LOG_TAG = "Card";
	private static final CardApi cardApi = new CardApi();
	@Column
	@PrimaryKey(autoincrement = true) @Setter @Getter
	long id;

	@Column @Unique @NotNull
	@Getter @Setter @Expose
	String uuid;

	@Column @Unique @NotNull
	@Getter @Setter @Expose
	String name;

	@Column @Getter @Setter @Expose
	CardType type;

	@Column @NotNull
	@Getter @Setter @Expose
	String accountUuid;

	@Column @NotNull @Getter @Setter @Expose
	String userUuid;

	@Column @Unique
	@Getter @Setter @Expose @SerializedName("_id")
	String serverId;

	@Column @Getter @Setter @Expose @SerializedName("created_at")
	long createdAt;

	@Column @Getter @Setter @Expose @SerializedName("updated_at")
	long updatedAt;

	@Column @Setter
	boolean sync;

	//TODO: necessary for DBFlow, remove with it
	public Card() {
	}

	public Card(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public static List<Card> all() {
		return initQuery().queryList();
	}

	public static List<Card> user() {
		return initQuery().where(Card_Table.userUuid.is(Environment.CURRENT_USER_UUID)).queryList();
	}

	public static List<Card> creditCards() {
		return initQuery()
				.where(Card_Table.type.eq(CardType.CREDIT))
				.queryList();
	}

	private static From<Card> initQuery() {
		return SQLite.select().from(Card.class);
	}

	public static Card find(String uuid) {
		return initQuery().where(Card_Table.uuid.eq(uuid)).querySingle();
	}

	public static long greaterUpdatedAt() {
		Card card = initQuery().orderBy(Card_Table.updatedAt, false).limit(1).querySingle();
		if(card == null)
			return 0L;
		return card.getUpdatedAt();
	}

	public static List<Card> unsync() {
		return initQuery().where(Card_Table.sync.eq(false)).queryList();
	}

	public static Card accountDebitCard(Account acc) {
		return initQuery()
				.where(Card_Table.accountUuid.eq(acc.getUuid()))
				.and(Card_Table.type.eq(CardType.DEBIT))
				.querySingle();
	}

	public Observable<Account> getAccount() {
		return accountRepository.find(accountUuid);
	}

	public void setAccount(Account account) {
		accountUuid = account.getUuid();
	}

	@Override
	public ChargeableType getChargeableType() {
		switch (type) {
			case CREDIT:
				return ChargeableType.CREDIT_CARD;
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
	public void debit(final int value) {
		if(type == CardType.DEBIT) {
			getAccount()
					.observeOn(Schedulers.io())
					.subscribe(new Subscriber<Account>("Card.debit") {
						@Override
						public void onNext(Account account) {
							account.debit(value);
							accountRepository.save(account);
						}
					});

		}
	}

	@Override
	public void credit(final int value) {
		if(type == CardType.DEBIT) {
			getAccount()
					.observeOn(Schedulers.io())
					.subscribe(new Subscriber<Account>("Card.credit") {
						@Override
						public void onNext(Account account) {
							account.credit(value);
							accountRepository.save(account);
						}
					});
		}
	}

	public int getInvoiceValue(DateTime month) {
		int total = 0;
		for (Expense expense : creditCardBills(month))
			total += expense.getValue();

		return total;
	}

	private static From<Expense> initExpenseQuery() {
		return SQLite.select().from(Expense.class);
	}

	public List<Expense> creditCardBills(DateTime month) {
		DateTime lastMonth = month.minusMonths(1);
		DateTime initOfMonth = DateHelper.firstDayOfMonth(lastMonth);
		DateTime endOfMonth = DateHelper.lastDayOfMonth(lastMonth);

		List<Expense> expenses = initExpenseQuery()
				.where(Expense_Table.chargeableUuid.eq(getUuid()))
				.and(Expense_Table.chargeableType.eq(ChargeableType.CREDIT_CARD))
				.and(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(true))
				.and(Expense_Table.charged.eq(false))
				.and(Expense_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.orderBy(Expense_Table.date, true)
				.queryList();

		initOfMonth = DateHelper.firstDayOfMonth(month);
		endOfMonth = DateHelper.lastDayOfMonth(month);

		expenses.addAll(initExpenseQuery()
				.where(Expense_Table.chargeableUuid.eq(getUuid()))
				.and(Expense_Table.chargeableType.eq(ChargeableType.CREDIT_CARD))
				.and(Expense_Table.date.between(initOfMonth).and(endOfMonth))
				.and(Expense_Table.chargeNextMonth.eq(false))
				.and(Expense_Table.charged.eq(false))
				.and(Expense_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.orderBy(Expense_Table.date, true)
				.queryList());

		return expenses;
	}

	@Override
	public void save() {
		if(id == 0) {
			if(uuid == null)
				uuid = UUID.randomUUID().toString();
			if(userUuid == null)
				userUuid = Environment.CURRENT_USER_UUID;
		}
		sync = false;
		super.save();
	}

	@Override
	public void syncAndSave(UnsyncModel unsyncModel) {
		Card card = Card.find(uuid);

		if(card != null && card.id != id) {
			if(card.getUpdatedAt() != getUpdatedAt())
				warning("Card overwritten", getData());
			id = card.id;
		}

		setServerId(unsyncModel.getServerId());
		setCreatedAt(unsyncModel.getCreatedAt());
		setUpdatedAt(unsyncModel.getUpdatedAt());

		save();
		sync = true;
		super.save();
	}

	@Override
	public boolean isSaved() {
		return id != 0;
	}

	@Override
	public String getData() {
		return "name=" + name +
				"\nuuid=" + uuid +
				"\ntype=" + type +
				"\naccount=" + accountUuid;
	}

	@Override
	public String getHeader(Context ctx) {
		return ctx.getString(R.string.cards);
	}

	@SuppressWarnings("unchecked")
	@Override
	public UnsyncModelApi getServerApi() {
		return cardApi;
	}
}
