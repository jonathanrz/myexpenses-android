package br.com.jonathanzanella.myexpenses.exceptions

class InvalidMethodCallException(method: String, klass: String, reason: String) : RuntimeException("$method should not be called from $klass: $reason")
