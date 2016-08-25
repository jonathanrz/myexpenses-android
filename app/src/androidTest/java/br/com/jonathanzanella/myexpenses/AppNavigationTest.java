package br.com.jonathanzanella.myexpenses;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.helpers.UIHelper;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by jzanella on 7/24/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AppNavigationTest {
	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public void clickOnAndroidHomeIcon_OpensNavigation() {
		activityTestRule.launchActivity(new Intent());

		onView(withId(R.id.act_main_drawer)).check(matches(isClosed(Gravity.START)));
		UIHelper.openMenu();

		onView(withId(R.id.act_main_drawer)).check(matches(isOpen(Gravity.START)));
	}
}
