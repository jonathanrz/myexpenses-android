package br.com.jonathanzanella.myexpenses.source;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helper.builder.SourceBuilder;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class SourceRepositoryUnitTest {
	@Mock
	private Repository<Source> repository;

	private SourceRepository sourceRepository = new SourceRepository(repository);

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}

	@Test
	@Ignore("fix when convert  to kotlin")
	public void return_with_error_when_tried_to_save_source_without_name() throws Exception {
		Source source = new SourceBuilder().name(null).build();

		ValidationResult result = sourceRepository.save(source);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}
}