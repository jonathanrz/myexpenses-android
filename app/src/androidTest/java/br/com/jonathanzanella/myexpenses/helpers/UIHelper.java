package br.com.jonathanzanella.myexpenses.helpers;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import br.com.jonathanzanella.myexpenses.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;

public final class UIHelper {
	private UIHelper() {}

	public static void openMenu() {
		onView(withContentDescription("Drawer Open")).perform(click());
	}

	private static void clickMenuItem(@StringRes int menuText) {
		ViewInteraction viewInteraction = onView(allOf(withId(R.id.design_menu_item_text), withText(menuText)));
		try {
			viewInteraction.perform(scrollTo()).perform(click());
		} catch (PerformException e) {
			viewInteraction.perform(click());
		}
	}

	public static void openMenuAndClickItem(@StringRes int menuText) {
		openMenu();
		clickMenuItem(menuText);
	}

	public static ViewInteraction matchToolbarTitle(CharSequence title) {
		return onView(isAssignableFrom(Toolbar.class))
				.check(matches(withToolbarTitle(is(title))));
	}

	private static Matcher<Object> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
		return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
			@Override public boolean matchesSafely(Toolbar toolbar) {
				return textMatcher.matches(toolbar.getTitle());
			}
			@Override public void describeTo(Description description) {
				description.appendText("with toolbar title: ");
				textMatcher.describeTo(description);
			}
		};
	}

	public static void clearAndTypeTextIntoView(@IdRes int view, String text) {
		try {
			onView(withId(view))
					.perform(clearText())
					.perform(typeText(text))
					.perform(closeSoftKeyboard());
		} catch (PerformException e) {
			onView(withId(view)).perform(typeText(text)).perform(closeSoftKeyboard());
		}
	}

	/**
	 *
	 * @deprecated Sometimes when clicking into a edit with text makes android open a popup.
	 * You should use clearAndTypeTextIntoView to first clear the edit text
	 */
	@Deprecated
	public static void typeTextIntoView(@IdRes int view, String text) {
		try {
			onView(withId(view))
					.perform(scrollTo())
					.perform(typeText(text))
					.perform(closeSoftKeyboard());
		} catch (PerformException e) {
			onView(withId(view)).perform(typeText(text)).perform(closeSoftKeyboard());
		}
	}

	public static void clickIntoView(@IdRes int view) {
		try {
			Espresso.closeSoftKeyboard();
			onView(withId(view)).perform(scrollTo()).perform(click());
		} catch (PerformException e) {
			onView(withId(view)).perform(click());
		}
	}

	public static void clickIntoView(String text) {
		try {
			onView(withText(text)).perform(scrollTo()).perform(click());
		} catch (PerformException e) {
			onView(withText(text)).perform(click());
		}
	}

	public static void clickIntoView(String text, @IdRes int id) {
		try {
			onView(allOf(
					withText(text),
					withId(id)))
					.perform(scrollTo()).perform(click());
		} catch (PerformException e) {
			onView(withText(text)).perform(click());
		}
	}

	public static void checkSnackbarText(String text) {
		onView(allOf(withId(android.support.design.R.id.snackbar_text), withText(text)))
				.check(matches(isDisplayed()));
	}

	public static void matchErrorMessage(@IdRes int view, String errorMessage) {
		onView(withId(view)).check(matches(hasErrorText(errorMessage)));
	}

	private static Matcher<View> hasErrorText(final String expectedError) {
		return new BoundedMatcher<View, View>(View.class) {

			@Override
			public void describeTo(Description description) {
				description.appendText("with error: " + expectedError);
			}

			@Override
			protected boolean matchesSafely(View view) {
				if (view instanceof EditText)
					return expectedError.equals(((EditText) view).getError());
				else if(view instanceof RadioButton)
					return expectedError.equals(((RadioButton) view).getError());
				else
					return false;
			}
		};
	}

	public static void setTimeInDatePicker(int year, int month, int day) {
		onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month, day));
		onView(withText("OK")).perform(click());
	}
}