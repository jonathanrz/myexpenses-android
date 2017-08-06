package br.com.jonathanzanella.myexpenses.card

class CardNotFoundException(uuid: String) : RuntimeException("Can't find card with uuid=" + uuid)