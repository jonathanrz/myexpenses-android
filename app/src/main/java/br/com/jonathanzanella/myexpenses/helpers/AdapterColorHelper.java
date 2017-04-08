package br.com.jonathanzanella.myexpenses.helpers;

public class AdapterColorHelper {
	private int oddColor;
	private int evenColor;

	public AdapterColorHelper(int oddColor, int evenColor) {
		this.oddColor = oddColor;
		this.evenColor = evenColor;
	}

	private boolean isMultipleOfTwo(int position) {
		return position % 2 == 0;
	}

	private boolean isMultipleOfFour(int position) {
		return position % 4 == 0;
	}

	public int getColorForGridWithTwoColumns(int position) {
		if(isMultipleOfFour(position))
			return oddColor;
		else if(isMultipleOfTwo(position))
			return evenColor;
		else if(isMultipleOfFour(position + 1))
			return oddColor;
		else if(isMultipleOfTwo(position + 1))
			return evenColor;
		else
			return oddColor;
	}

	public int getColorForLinearLayout(int position) {
		if(position % 2 == 0)
			return oddColor;
		else
			return evenColor;
	}
}
