package br.com.jonathanzanella.myexpenses.card;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.NonNull;

public enum CardType {
	CREDIT("CREDIT"),
	DEBIT("DEBIT");

	@NonNull @Getter
	private final String value;

	CardType(@NonNull String value) {
		this.value = value;
	}

	@Nullable
	public static CardType fromValue(String value) {
		for (CardType cardType : values()) {
			if(cardType.getValue().equals(value))
				return cardType;
		}

		return null;
	}
}
