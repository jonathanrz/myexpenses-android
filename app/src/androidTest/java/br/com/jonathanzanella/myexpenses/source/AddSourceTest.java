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
import br.com.jonathanzanella.myexpenses.helpers.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.helpers.UIHelper;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by jzanella on 7/24/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddSourceTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Before
	public void setUp() throws Exception {
		UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
		if (!uiDevice.isScreenOn())
			uiDevice.wakeUp();
	}

	@After
	public void tearDown() throws Exception {
		DatabaseHelper.reset(getContext());
	}

	@Test
	public void addNewSource() {
		activityTestRule.launchActivity(new Intent());

		UIHelper.openMenuAndClickItem(R.string.sources);

		final String accountsTitle = getContext().getString(R.string.sources);
		UIHelper.matchToolbarTitle(accountsTitle);

		UIHelper.clickIntoView(R.id.view_sources_fab);

		final String newSourceTitle = getContext().getString(R.string.new_source_title);
		UIHelper.matchToolbarTitle(newSourceTitle);

		final String sourceTitle = "Test";
		UIHelper.typeTextIntoView(R.id.act_edit_source_name, sourceTitle);
		UIHelper.clickIntoView(R.id.action_save);

		UIHelper.matchToolbarTitle(accountsTitle);

		onView(withId(R.id.row_source_name)).check(matches(withText(sourceTitle)));
	}

	private Context getContext() {
		return InstrumentationRegistry.getTargetContext();
	}
}
