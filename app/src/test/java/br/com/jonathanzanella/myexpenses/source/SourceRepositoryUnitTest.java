package br.com.jonathanzanella.myexpenses.source;

import org.junit.Test;

import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by jzanella on 8/27/16.
 */
public class SourceRepositoryUnitTest {
	private SourceRepository repository = new SourceRepository();

	@Test
	public void return_with_error_when_tried_to_save_source_without_name() throws Exception {
		OperationResult result = repository.save(new Source());

		assertFalse(result.isValid());
		assertTrue(result.getErrors().contains(ValidationError.NAME));
	}
}