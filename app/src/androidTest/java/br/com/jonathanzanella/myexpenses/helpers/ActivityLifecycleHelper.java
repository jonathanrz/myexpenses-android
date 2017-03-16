package br.com.jonathanzanella.myexpenses.helpers;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.core.deps.guava.base.Throwables;
import android.support.test.espresso.core.deps.guava.collect.Sets;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitor;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public final class ActivityLifecycleHelper {
	private static final int NUMBER_OF_RETRIES = 100;
	private ActivityLifecycleHelper() {}

	public static void closeAllActivities(Instrumentation instrumentation) throws Exception {
		int i = 0;
		while (closeActivity(instrumentation)) {
			if (i++ > NUMBER_OF_RETRIES) {
				throw new AssertionError("Limit of retries excesses");
			}
			Thread.sleep(200);
		}
	}

	private static <X> X callOnMainSync(Instrumentation instrumentation, final Callable<X> callable) throws Exception {
		final AtomicReference<X> retAtomic = new AtomicReference<>();
		final AtomicReference<Throwable> exceptionAtomic = new AtomicReference<>();
		instrumentation.runOnMainSync(new Runnable() {
			@Override
			public void run() {
				try {
					retAtomic.set(callable.call());
				} catch (Throwable e) {
					exceptionAtomic.set(e);
				}
			}
		});
		final Throwable exception = exceptionAtomic.get();
		if (exception != null) {
			Throwables.propagateIfInstanceOf(exception, Exception.class);
			Throwables.propagate(exception);
		}
		return retAtomic.get();
	}

	private static Set<Activity> getActivitiesInStages(Stage... stages) {
		final Set<Activity> activities = Sets.newHashSet();
		final ActivityLifecycleMonitor instance = ActivityLifecycleMonitorRegistry.getInstance();
		for (Stage stage : stages) {
			final Collection<Activity> activitiesInStage = instance.getActivitiesInStage(stage);
			if (activitiesInStage != null) {
				activities.addAll(activitiesInStage);
			}
		}
		return activities;
	}

	private static boolean closeActivity(Instrumentation instrumentation) throws Exception {
		final Boolean activityClosed = callOnMainSync(instrumentation, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				final Set<Activity> activities = getActivitiesInStages(Stage.RESUMED,
						Stage.STARTED, Stage.PAUSED, Stage.STOPPED, Stage.CREATED);
				activities.removeAll(getActivitiesInStages(Stage.DESTROYED));
				if (activities.size() > 0) {
					final Activity activity = activities.iterator().next();
					activity.finish();
					return true;
				} else {
					return false;
				}
			}
		});
		if (activityClosed) {
			instrumentation.waitForIdleSync();
		}
		return activityClosed;
	}
}
