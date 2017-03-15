package br.com.jonathanzanella.myexpenses.card;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class ShowCardActivityTest {
	@Rule
	public ActivityTestRule<ShowCardActivity> activityTestRule = new ActivityTestRule<>(ShowCardActivity.class, true, false);

	private Card card;
	private Account account;
	private ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(MyApplication.getContext()));
	private CardRepository repository = new CardRepository(new RepositoryImpl<Card>(MyApplication.getContext()), expenseRepository);
	private AccountRepository accountRepository = new AccountRepository(new RepositoryImpl<Account>(MyApplication.getContext()));

	@Before
	public void setUp() throws Exception {
		account = new AccountBuilder().build();
		accountRepository.save(account);

		card = new CardBuilder().account(account).build(accountRepository);
		repository.save(card);
	}

	@After
	public void tearDown() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void shows_account_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowCardActivity.KEY_CREDIT_CARD_UUID, card.getUuid());
		activityTestRule.launchActivity(i);

		final String editCardTitle = getTargetContext().getString(R.string.card) + " " + card.getName();
		matchToolbarTitle(editCardTitle);

		onView(withId(R.id.act_show_card_name)).check(matches(withText(card.getName())));
		onView(withId(R.id.act_show_card_account)).check(matches(withText(account.getName())));
	}
}