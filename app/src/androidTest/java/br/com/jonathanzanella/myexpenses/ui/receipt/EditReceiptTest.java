package br.com.jonathanzanella.myexpenses.ui.receipt;

import android.content.Intent;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountDataSource;
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.ReceiptBuilder;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.clearAndTypeTextIntoView;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.matchToolbarTitle;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class EditReceiptTest {
	@Rule
	public ActivityTestRule<br.com.jonathanzanella.myexpenses.receipt.ShowReceiptActivity> activityTestRule = new ActivityTestRule<>(br.com.jonathanzanella.myexpenses.receipt.ShowReceiptActivity.class, true, false);
	@Inject
	AccountDataSource accountDataSource;
	@Inject
	SourceRepository sourceRepository;
	@Inject
	br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository repository;

	private br.com.jonathanzanella.myexpenses.receipt.Receipt receipt;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();

		Account a = new AccountBuilder().build();
		assertTrue(accountDataSource.save(a).blockingFirst().isValid());

		Source s = new SourceBuilder().build();
		assertTrue(sourceRepository.save(s).isValid());

		receipt = new ReceiptBuilder()
				.date(DateTime.now().minusDays(1))
				.account(a)
				.source(s)
				.build();
		assertTrue(repository.save(receipt).isValid());
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void edit_expense_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(br.com.jonathanzanella.myexpenses.receipt.ShowReceiptActivity.Companion.getKEY_RECEIPT_UUID(), receipt.getUuid());
		activityTestRule.launchActivity(i);

		final String showReceiptTitle = getTargetContext().getString(R.string.receipt) + " " + receipt.getName();
		matchToolbarTitle(showReceiptTitle);

		clickIntoView(R.id.action_edit);

		final String editReceiptTitle = getTargetContext().getString(R.string.edit_receipt_title);
		matchToolbarTitle(editReceiptTitle);
		onView(withId(R.id.act_edit_receipt_name)).check(matches(withText(receipt.getName())));
		String expectedDate = Transaction.Companion.getSIMPLE_DATE_FORMAT().format(receipt.getDate().toDate());
		onView(withId(R.id.act_edit_receipt_date)).check(matches(withText(expectedDate)));
		onView(withId(R.id.act_edit_receipt_account)).check(matches(withText(receipt.getAccountFromCache().getName())));
		clearAndTypeTextIntoView(R.id.act_edit_receipt_name, receipt.getName() + " changed");

		clickIntoView(R.id.action_save);

		clickIntoView(getTargetContext().getString(R.string.yes));

		matchToolbarTitle(showReceiptTitle + " changed");

		receipt = repository.find(receipt.getUuid());

		onView(withId(R.id.act_show_receipt_name)).check(matches(withText(receipt.getName())));
		assertThat(repository.all().size(), is(1));
	}
}