package br.com.jonathanzanella.myexpenses.card

internal class CardNotFoundException(uuid: String) : RuntimeException("Can't find card with uuid=" + uuid)