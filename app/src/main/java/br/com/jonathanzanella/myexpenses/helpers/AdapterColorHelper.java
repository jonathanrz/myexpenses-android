package br.com.jonathanzanella.myexpenses.helpers;

public class AdapterColorHelper {
	private int oddColor;
	private int evenColor;

	public AdapterColorHelper(int oddColor, int evenColor) {
		this.oddColor = oddColor;
		this.evenColor = evenColor;
	}

	public int getColor(int position) {
		if(position % 4 == 0)
			return oddColor;
		else if(position % 2 == 0)
			return evenColor;
		else if((position + 1) % 4 == 0)
			return oddColor;
		else if((position + 1) % 2 == 0)
			return evenColor;
		else
			return oddColor;
	}
}
