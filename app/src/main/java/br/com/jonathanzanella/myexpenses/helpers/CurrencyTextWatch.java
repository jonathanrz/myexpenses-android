package br.com.jonathanzanella.myexpenses.helpers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CurrencyTextWatch implements TextWatcher {
	private final EditText edit;
	private String current;

	public CurrencyTextWatch(EditText edit) {
		this.edit = edit;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(!s.toString().equals(current)){
			edit.removeTextChangedListener(this);

			String cleanString = s.toString().replaceAll("[^\\d]", "");

			double parsed = Double.parseDouble(cleanString);
			String formatted = CurrencyHelper.format(parsed);

			current = formatted;
			edit.setText(formatted);
			edit.setSelection(formatted.length());

			edit.addTextChangedListener(this);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {}
}
