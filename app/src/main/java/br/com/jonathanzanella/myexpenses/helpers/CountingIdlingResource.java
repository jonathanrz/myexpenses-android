package br.com.jonathanzanella.myexpenses.helpers;

/**
 * Created by jzanella on 11/25/16.
 */

//This is necessary because CountingIdlingResource of Espresso is final and can't be mocked
public class CountingIdlingResource {
	private android.support.test.espresso.idling.CountingIdlingResource espressoIdlingResource;

	public CountingIdlingResource(String tag) {
		this.espressoIdlingResource = new android.support.test.espresso.idling.CountingIdlingResource(tag);
	}

	public void increment() {
		espressoIdlingResource.increment();
	}

	public void decrement() {
		espressoIdlingResource.decrement();
	}
}
