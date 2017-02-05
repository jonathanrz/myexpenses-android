package br.com.jonathanzanella.myexpenses.helpers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;

/**
 * Created by jzanella on 2/2/16.
 */
public class CurrencyTextWatch implements TextWatcher {
	private EditText edit;
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

			String cleanString = s.toString().replaceAll("[^\\d.]", "");

			double parsed = Double.parseDouble(cleanString);
			String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

			current = formatted;
			edit.setText(formatted);
			edit.setSelection(formatted.length());

			edit.addTextChangedListener(this);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {}
}
