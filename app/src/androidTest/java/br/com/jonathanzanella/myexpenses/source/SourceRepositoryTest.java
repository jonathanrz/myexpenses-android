package br.com.jonathanzanella.myexpenses.source;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.ActivityLifecycleHelper;
import br.com.jonathanzanella.myexpenses.helpers.builder.SourceBuilder;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SourceRepositoryTest {
	private SourceRepository repository;

	@Before
	public void setUp() throws Exception {
		new DatabaseHelper(getTargetContext()).recreateTables();

		repository = new SourceRepository();
	}

	@After
	public void tearDown() throws Exception {
		ActivityLifecycleHelper.closeAllActivities(getInstrumentation());
	}

	@Test
	public void can_save_source() throws Exception {
		Source source = new Source();
		source.setName("test");
		repository.save(source);

		assertThat(source.getId(), is(not(0L)));
		assertThat(source.getUuid(), is(not("")));
	}

	@Test
	public void can_load_saved_source() throws Exception {
		Source sourceSaved = new Source();
		sourceSaved.setName("test");
		repository.save(sourceSaved);

		Source source = repository.find(sourceSaved.getUuid());
		assertThat(source, is(source));
	}

	@Test
	public void source_unsync_returns_only_not_synced() throws Exception {
		Source sourceUnsync = new SourceBuilder().name("sourceUnsync").updatedAt(100L).build();
		sourceUnsync.setSync(false);
		repository.save(sourceUnsync);

		Source sourceSync = new SourceBuilder().name("sourceSync").updatedAt(100L).build();
		repository.save(sourceSync);
		repository.syncAndSave(sourceSync);

		List<Source> sources = repository.unsync();
		assertThat(sources.size(), is(1));
		assertThat(sources.get(0).getUuid(), is(sourceUnsync.getUuid()));
	}

	@Test
	public void load_user_sources_in_alphabetical_order() throws Exception {
		Source sourceB = new SourceBuilder().name("b").build();
		repository.save(sourceB);

		Source sourceA = new SourceBuilder().name("a").build();
		repository.save(sourceA);

		List<Source> sources = repository.all();
		assertThat(sources.get(0).getUuid(), is(sourceA.getUuid()));
		assertThat(sources.get(1).getUuid(), is(sourceB.getUuid()));
	}
}