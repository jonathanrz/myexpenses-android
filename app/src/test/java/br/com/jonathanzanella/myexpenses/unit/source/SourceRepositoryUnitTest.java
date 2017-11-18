package br.com.jonathanzanella.myexpenses.unit.source;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.unit.helper.builder.SourceBuilder;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class SourceRepositoryUnitTest {
	@Mock
	private br.com.jonathanzanella.myexpenses.source.SourceDao dao;
	private br.com.jonathanzanella.myexpenses.source.SourceRepository sourceRepository;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		sourceRepository = new br.com.jonathanzanella.myexpenses.source.SourceRepository(dao);
	}

	@Test
	public void return_with_error_when_tried_to_save_source_without_name() throws Exception {
		br.com.jonathanzanella.myexpenses.source.Source source = new SourceBuilder().name(null).build();

		ValidationResult result = sourceRepository.save(source);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}
}