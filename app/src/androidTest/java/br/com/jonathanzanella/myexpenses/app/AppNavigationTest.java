package br.com.jonathanzanella.myexpenses.app;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.UIHelper;
import br.com.jonathanzanella.myexpenses.views.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AppNavigationTest {
	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Before
	public void setUp() throws Exception {
		final MainActivity activity = activityTestRule.getActivity();
		Runnable wakeUpDevice = new Runnable() {
			public void run() {
				activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
						WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
		};
		activity.runOnUiThread(wakeUpDevice);
	}

	@Test
	public void clickOnAndroidHomeIcon_OpensNavigation() {
		activityTestRule.launchActivity(new Intent());

		onView(ViewMatchers.withId(R.id.drawer)).check(matches(isClosed(Gravity.START)));
		UIHelper.openMenu();

		onView(withId(R.id.drawer)).check(matches(isOpen(Gravity.START)));
	}
}
