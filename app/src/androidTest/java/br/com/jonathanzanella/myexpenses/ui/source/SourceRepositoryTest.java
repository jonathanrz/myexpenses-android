package br.com.jonathanzanella.myexpenses.ui.source;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import javax.inject.Inject;

import br.com.jonathanzanella.TestApp;
import br.com.jonathanzanella.myexpenses.App;
import br.com.jonathanzanella.myexpenses.ui.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SourceRepositoryTest {
	@Inject
	br.com.jonathanzanella.myexpenses.source.SourceDataSource dataSource;

	@Before
	public void setUp() throws Exception {
		TestApp.Companion.getTestComponent().inject(this);
		App.Companion.resetDatabase();
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void can_save_source() throws Exception {
		br.com.jonathanzanella.myexpenses.source.Source source = new br.com.jonathanzanella.myexpenses.source.Source();
		source.setName("test");
		dataSource.save(source);

		assertThat(source.getId(), is(not(0L)));
		assertThat(source.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_source() throws Exception {
		br.com.jonathanzanella.myexpenses.source.Source sourceSaved = new br.com.jonathanzanella.myexpenses.source.Source();
		sourceSaved.setName("test");
		dataSource.save(sourceSaved);

		br.com.jonathanzanella.myexpenses.source.Source source = dataSource.find(sourceSaved.getUuid());
		assertThat(source, is(source));
	}

	@Test
	public void source_unsync_returns_only_not_synced() throws Exception {
		br.com.jonathanzanella.myexpenses.source.Source sourceUnsync = new SourceBuilder().name("sourceUnsync").updatedAt(100L).build();
		sourceUnsync.setSync(false);
		dataSource.save(sourceUnsync);

		br.com.jonathanzanella.myexpenses.source.Source sourceSync = new SourceBuilder().name("sourceSync").updatedAt(100L).build();
		dataSource.save(sourceSync);
		dataSource.syncAndSave(sourceSync);

		List<br.com.jonathanzanella.myexpenses.source.Source> sources = dataSource.unsync();
		assertThat(sources.size(), is(1));
		assertThat(sources.get(0).getUuid(), is(sourceUnsync.getUuid()));
	}

	@Test
	public void load_user_sources_in_alphabetical_order() throws Exception {
		br.com.jonathanzanella.myexpenses.source.Source sourceB = new SourceBuilder().name("b").build();
		dataSource.save(sourceB);

		br.com.jonathanzanella.myexpenses.source.Source sourceA = new SourceBuilder().name("a").build();
		dataSource.save(sourceA);

		List<br.com.jonathanzanella.myexpenses.source.Source> sources = dataSource.all();
		assertThat(sources.get(0).getUuid(), is(sourceA.getUuid()));
		assertThat(sources.get(1).getUuid(), is(sourceB.getUuid()));
	}
}