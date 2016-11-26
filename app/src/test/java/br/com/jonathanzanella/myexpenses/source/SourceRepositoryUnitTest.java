package br.com.jonathanzanella.myexpenses.source;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.testRunner.KotlinTestRunner;
import br.com.jonathanzanella.myexpenses.testRunner.OpenedPackages;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by jzanella on 8/27/16.
 */
@RunWith(KotlinTestRunner.class)
@OpenedPackages("br.com.jonathanzanella.myexpenses.database")
public class SourceRepositoryUnitTest {
	@Mock
	Repository<Source> repository;

	private SourceRepository sourceRepository = new SourceRepository(repository);

	@Mock
	private Source source;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}

	@Test
	public void return_with_error_when_tried_to_save_source_without_name() throws Exception {
		when(source.getName()).thenReturn(null);

		OperationResult result = sourceRepository.save(source);

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}
}