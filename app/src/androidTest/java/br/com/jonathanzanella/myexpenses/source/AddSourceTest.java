package br.com.jonathanzanella.myexpenses.source;

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

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
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
public class AddSourceTest {
	@Rule
	public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);
	@Rule
	public ActivityTestRule<EditSourceActivity> editSourceActivityTestRule = new ActivityTestRule<>(EditSourceActivity.class);

	@Before
	public void setUp() throws Exception {
		UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
		if (!uiDevice.isScreenOn())
			uiDevice.wakeUp();
	}

	@After
	public void tearDown() throws Exception {
		DatabaseHelper.reset(getContext());
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void add_new_source() {
		mainActivityTestRule.launchActivity(new Intent());

		openMenuAndClickItem(R.string.sources);

		final String sourcesTitle = getContext().getString(R.string.sources);
		matchToolbarTitle(sourcesTitle);

		clickIntoView(R.id.view_sources_fab);

		final String newSourceTitle = getContext().getString(R.string.new_source_title);
		matchToolbarTitle(newSourceTitle);

		final String sourceTitle = "Test";
		typeTextIntoView(R.id.act_edit_source_name, sourceTitle);
		clickIntoView(R.id.action_save);

		matchToolbarTitle(sourcesTitle);

		onView(withId(R.id.row_source_name)).check(matches(withText(sourceTitle)));
	}

	@Test
	public void add_new_source_shows_error_with_empty_name() {
		editSourceActivityTestRule.launchActivity(new Intent());

		final String newSourceTitle = getContext().getString(R.string.new_source_title);
		matchToolbarTitle(newSourceTitle);

		clickIntoView(R.id.action_save);

		final String errorMessage = getContext().getString(R.string.error_message_name_not_informed);
		matchErrorMessage(R.id.act_edit_source_name, errorMessage);
	}

	private Context getContext() {
		return InstrumentationRegistry.getTargetContext();
	}
}
