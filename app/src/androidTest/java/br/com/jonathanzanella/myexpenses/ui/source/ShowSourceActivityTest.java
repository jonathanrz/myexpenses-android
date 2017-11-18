package br.com.jonathanzanella.myexpenses.ui.source;

import android.content.Intent;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.R;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.ui.helpers.UIHelper.matchToolbarTitle;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class ShowSourceActivityTest {
	@Rule
	public ActivityTestRule<br.com.jonathanzanella.myexpenses.source.ShowSourceActivity> activityTestRule = new ActivityTestRule<>(br.com.jonathanzanella.myexpenses.source.ShowSourceActivity.class, true, false);

	@Inject
	br.com.jonathanzanella.myexpenses.source.SourceDataSource dataSource;
	private br.com.jonathanzanella.myexpenses.source.Source source;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();

		source = new br.com.jonathanzanella.myexpenses.source.Source();
		source.setName("test");
		dataSource.save(source);
	}

	@Test
	public void shows_account_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(br.com.jonathanzanella.myexpenses.source.ShowSourceActivity.Companion.getKEY_SOURCE_UUID(), source.getUuid());
		activityTestRule.launchActivity(i);

		final String editSourceTitle = getTargetContext().getString(R.string.source) + " " + source.getName();
		matchToolbarTitle(editSourceTitle);

		onView(withId(R.id.act_show_source_name)).check(matches(withText(source.getName())));
	}
}