package br.com.jonathanzanella.myexpenses;

import android.support.test.runner.AndroidJUnit4;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardType;
import br.com.jonathanzanella.myexpenses.expense.Expense;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by jzanella on 7/6/16.
 */
@RunWith(AndroidJUnit4.class)
public class BillsTestSuite {
	List<BaseModel> modelsToDestroy = new ArrayList<>();
	DateTime firstDayOfJune = new DateTime(2016, 6, 1, 0, 0, 0, 0);

	Account account = new Account();
	Card card = new Card();

	@Before
	public void setUp() throws Exception {
		account.setName("Account");
		account.setUserUuid(Environment.CURRENT_USER_UUID);
		account.save();
		modelsToDestroy.add(account);

		card.setName("CreditCard");
		card.setUserUuid(Environment.CURRENT_USER_UUID);
		card.setAccount(account);
		card.setType(CardType.CREDIT);
		card.save();
		modelsToDestroy.add(card);
	}

	@After
	public void tearDown() throws Exception {
		for (BaseModel baseModel : modelsToDestroy)
			baseModel.delete();
	}

	@Test
	public void billIsPaidWhenPaidWithCreditCard() {
		Bill bill = new Bill();
		bill.setName("bill");
		bill.setUserUuid(Environment.CURRENT_USER_UUID);
		bill.setAmount(10000);
		bill.setInitDate(firstDayOfJune);
		bill.setEndDate(firstDayOfJune);
		bill.save();
		modelsToDestroy.add(bill);

		Expense expense = new Expense();
		expense.setName("expense");
		expense.setUserUuid(Environment.CURRENT_USER_UUID);
		expense.setDate(firstDayOfJune);
		expense.setValue(1000);
		expense.setBill(bill);
		expense.setChargeable(card);
		expense.save();
		modelsToDestroy.add(expense);

		assertThat(Bill.monthly(firstDayOfJune).size(), is(0));
	}
}
