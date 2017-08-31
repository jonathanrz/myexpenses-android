package br.com.jonathanzanella.myexpenses.card;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.CardBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clearAndTypeTextIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EditCardTest {
	@Rule
	public ActivityTestRule<ShowCardActivity> activityTestRule = new ActivityTestRule<>(ShowCardActivity.class, true, false);

	private Card card;
	private CardRepository repository;

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();

		Account a = new AccountBuilder().build();
		AccountRepository accountRepository = new AccountRepository();
		ExpenseRepository expenseRepository = new ExpenseRepository();
		accountRepository.save(a);

		card = new CardBuilder().account(a).build(accountRepository);
		repository = new CardRepository(expenseRepository);
		assertTrue(repository.save(card).isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void edit_card_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowCardActivity.Companion.getKEY_CREDIT_CARD_UUID(), card.getUuid());
		activityTestRule.launchActivity(i);

		final String showExpenseTitle = getTargetContext().getString(R.string.card) + " " + card.getName();
		matchToolbarTitle(showExpenseTitle);

		clickIntoView(R.id.action_edit);

		final String editExpenseTitle = getTargetContext().getString(R.string.edit_card_title);
		matchToolbarTitle(editExpenseTitle);
		onView(withId(R.id.act_edit_card_name)).perform(scrollTo()).check(matches(withText(card.getName())));
		clearAndTypeTextIntoView(R.id.act_edit_card_name, card.getName() + " changed");
		onView(withId(R.id.act_edit_card_account)).perform(scrollTo()).check(matches(withText(card.getAccount().getName())));

		clickIntoView(R.id.action_save);

		matchToolbarTitle(showExpenseTitle + " changed");

		card = repository.find(card.getUuid());

		onView(withId(R.id.act_show_card_name)).check(matches(withText(card.getName())));
		assertThat(repository.all().size(), is(1));
	}
}
