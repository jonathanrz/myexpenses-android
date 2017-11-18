package br.com.jonathanzanella.myexpenses.ui.helpers;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.util.Log;

import java.util.List;

// copied from https://github.com/dannyroa/espresso-samples/blob/master/RecyclerView/app/src/androidTest/java/com/
// dannyroa/espresso_samples/recyclerview/TestUtils.java

public final class TestUtils {
	private TestUtils() {}

	public static br.com.jonathanzanella.myexpenses.ui.helpers.RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
		return new br.com.jonathanzanella.myexpenses.ui.helpers.RecyclerViewMatcher(recyclerViewId);
	}

	public static void waitForIdling() {
		List<IdlingResource> idlingResourceList = Espresso.getIdlingResources();
		for (IdlingResource idlingResource : idlingResourceList) {
			Log.d("TestUtils", "waiting for=" + idlingResource.getName());
			try {
				idlingResource.wait();
			} catch (InterruptedException e) {
				Log.w("TestUtils", "idling " + idlingResource.getName() + " interrupted");
			}
		}
	}
}
