package br.com.jonathanzanella.myexpenses.receipt;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.openMenuAndClickItem;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.typeTextIntoView;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class ReceiptsViewTest {
	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

	private Receipt receipt;
	private Receipt receipt2;

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();

		ReceiptRepository repository = new ReceiptRepository(new RepositoryImpl<Receipt>(getTargetContext()));

		Source s = new SourceBuilder().build();
		assertTrue(new SourceRepository(new RepositoryImpl<Source>(getTargetContext())).save(s).isValid());

		Account a = new AccountBuilder().build();
		assertTrue(new AccountRepository(new RepositoryImpl<Account>(getTargetContext())).save(a).isValid());

		receipt = new ReceiptBuilder().name("receipt1").source(s).account(a).build();
		assertTrue(repository.save(receipt).isValid());

		receipt2 = new ReceiptBuilder().name("receipt2").source(s).account(a).build();
		assertTrue(repository.save(receipt2).isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void shows_receipt_correctly() throws Exception {
		activityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.receipts);

		final String receiptsTitle = getTargetContext().getString(R.string.receipts);
		matchToolbarTitle(receiptsTitle);

		clickIntoView(receipt.getName(), R.id.row_receipt_name);

		final String editReceiptTitle = getTargetContext().getString(R.string.receipt) + " " + receipt.getName();
		matchToolbarTitle(editReceiptTitle);

		String incomeAsCurrency = CurrencyHelper.format(receipt.getIncome());
		onView(withId(R.id.act_show_receipt_name)).check(matches(withText(receipt.getName())));
		onView(withId(R.id.act_show_receipt_income)).check(matches(withText(incomeAsCurrency)));
		Account account = receipt.getAccountFromCache();
		onView(withId(R.id.act_show_receipt_account)).check(matches(withText(account.getName())));

		Source source = receipt.getSource();
		onView(withId(R.id.act_show_receipt_source)).check(matches(withText(source.getName())));
	}

	@Test
	public void filter_do_not_show_receipt2() throws Exception {
		activityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.receipts);

		final String title = getTargetContext().getString(R.string.receipts);
		matchToolbarTitle(title);

		clickIntoView(R.id.search);
		typeTextIntoView(R.id.search_src_text, receipt.getName());

		onViewReceiptName(receipt).check(matches(isDisplayed()));
		onViewReceiptName(receipt2).check(doesNotExist());
	}

	private ViewInteraction onViewReceiptName(Receipt receipt) {
		return onView(allOf(
				withId(R.id.row_receipt_name),
				allOf(
					isDescendantOfA(withTagValue(is((Object)receipt.getUuid())))),
					withText(receipt.getName())));
	}
}