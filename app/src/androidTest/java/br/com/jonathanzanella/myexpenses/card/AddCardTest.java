package br.com.jonathanzanella.myexpenses.card;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

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
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.AccountBuilder;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.checkSnackbarText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.clickIntoView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchErrorMessage;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.openMenuAndClickItem;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.typeTextIntoView;

/**
 * Created by jzanella on 7/24/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddCardTest {
	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
	@Rule
	public ActivityTestRule<EditCardActivity> editCardActivityTestRule = new ActivityTestRule<>(EditCardActivity.class);

	private Account account;

	@Before
	public void setUp() throws Exception {
		UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
		if (!uiDevice.isScreenOn())
			uiDevice.wakeUp();

		account = new AccountBuilder().build();
		new AccountRepository(new RepositoryImpl<Account>(MyApplication.getContext())).save(account);
	}

	@After
	public void tearDown() throws Exception {
		new DatabaseHelper(InstrumentationRegistry.getTargetContext()).recreateTables();
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void add_new_card() {
		activityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.cards);

		final String cardsTitle = getContext().getString(R.string.cards);
		matchToolbarTitle(cardsTitle);

		clickIntoView(R.id.view_card_fab);

		final String newCardTitle = getContext().getString(R.string.new_card_title);
		matchToolbarTitle(newCardTitle);

		final String accountTitle = "Test";
		typeTextIntoView(R.id.act_edit_card_name, accountTitle);

		clickIntoView(R.id.act_edit_card_type_credit);

		final String selectAccountTitle = getContext().getString(R.string.select_account_title);
		clickIntoView(R.id.act_edit_card_account);
		matchToolbarTitle(selectAccountTitle);
		clickIntoView(account.getName());

		matchToolbarTitle(newCardTitle);

		clickIntoView(R.id.action_save);

		matchToolbarTitle(cardsTitle);

		onView(withId(R.id.row_card_name)).check(matches(withText(accountTitle)));
	}

	@Test
	public void add_new_card_shows_error_with_empty_name() {
		editCardActivityTestRule.launchActivity(new Intent());

		final String newCardTitle = getContext().getString(R.string.new_card_title);
		matchToolbarTitle(newCardTitle);

		clickIntoView(R.id.action_save);

		final String errorMessage = getContext().getString(R.string.error_message_name_not_informed);
		matchErrorMessage(R.id.act_edit_card_name, errorMessage);
	}

	@Test
	public void add_new_card_shows_error_with_unselected_type() {
		editCardActivityTestRule.launchActivity(new Intent());

		final String newCardTitle = getContext().getString(R.string.new_card_title);
		matchToolbarTitle(newCardTitle);

		final String accountTitle = "Test";
		typeTextIntoView(R.id.act_edit_card_name, accountTitle);

		clickIntoView(R.id.action_save);

		final String errorMessage = getContext().getString(R.string.error_message_card_type_not_selected);
		checkSnackbarText(errorMessage);
	}

	@Test
	public void add_new_card_shows_error_with_unselected_account() {
		editCardActivityTestRule.launchActivity(new Intent());

		final String newCardTitle = getContext().getString(R.string.new_card_title);
		matchToolbarTitle(newCardTitle);

		final String accountTitle = "Test";
		typeTextIntoView(R.id.act_edit_card_name, accountTitle);

		clickIntoView(R.id.act_edit_card_type_credit);

		clickIntoView(R.id.action_save);

		final String errorMessage = getContext().getString(R.string.error_message_account_not_informed);
		matchErrorMessage(R.id.act_edit_card_account, errorMessage);
	}

	private Context getContext() {
		return InstrumentationRegistry.getTargetContext();
	}
}
