package br.com.jonathanzanella.myexpenses.source;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.helpers.FlowManagerHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.TestUtils.withRecyclerView;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;

/**
 * Created by jzanella on 8/28/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ListSourceActivityTest {
	@Rule
	public ActivityTestRule<ListSourceActivity> activityTestRule = new ActivityTestRule<>(ListSourceActivity.class, true, false);

	private SourceRepository repository = new SourceRepository(new DatabaseHelper(getContext()));

	@After
	public void tearDown() throws Exception {
		FlowManagerHelper.reset(getTargetContext());
	}

	@Test
	public void shows_empty_source_list() throws Exception {
		activityTestRule.launchActivity(new Intent());

		matchToolbarTitle(getTargetContext().getString(R.string.select_source_title));

		onView(withId(R.id.act_sources_list_empty)).check(matches(isDisplayed()));
	}

	@Test
	public void show_source_in_list() throws Exception {
		Source source = new SourceBuilder().build();
		repository.save(source);

		activityTestRule.launchActivity(new Intent());

		matchToolbarTitle(getTargetContext().getString(R.string.select_source_title));
		onView(withId(R.id.row_source_name)).check(matches(withText(source.getName())));
	}

	@Test
	public void show_sources_in_alphabetical_order() throws Exception {
		Source sourceB = new SourceBuilder().name("b").build();
		repository.save(sourceB);

		Source sourceA = new SourceBuilder().name("a").build();
		repository.save(sourceA);

		activityTestRule.launchActivity(new Intent());

		matchToolbarTitle(getTargetContext().getString(R.string.select_source_title));
		onView(withRecyclerView(R.id.act_sources_list)
				.atPositionOnView(0, R.id.row_source_name))
				.check(matches(withText(sourceA.getName())));
		onView(withRecyclerView(R.id.act_sources_list)
				.atPositionOnView(1, R.id.row_source_name))
				.check(matches(withText(sourceB.getName())));
	}
}