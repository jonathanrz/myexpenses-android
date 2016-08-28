package br.com.jonathanzanella.myexpenses.helpers;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import br.com.jonathanzanella.myexpenses.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;

/**
 * Created by jzanella on 7/24/16.
 */
public class UIHelper {
	public static void openMenu() {
		onView(withContentDescription("Drawer Open")).perform(click());
	}

	public static void clickMenuItem(@StringRes int menuText) {
		onView(allOf(withId(R.id.design_menu_item_text), withText(menuText))).perform(click());
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

	public static void typeTextIntoView(@IdRes int view, String text) {
		onView(withId(view)).perform(typeText(text)).perform(closeSoftKeyboard());
	}

	public static void clickIntoView(@IdRes int view) {
		onView(withId(view)).perform(click());
	}

	public static void matchErrorMessage(@IdRes int view, String errorMessage) {
		onView(withId(view)).check(matches(hasErrorText(errorMessage)));
	}

	private static Matcher<? super View> hasErrorText(String expectedError) {
		return new ErrorTextMatcher(expectedError);
	}

	private static class ErrorTextMatcher extends TypeSafeMatcher<View> {
		private final String expectedError;

		private ErrorTextMatcher(String expectedError) {
			this.expectedError = checkNotNull(expectedError);
		}

		@Override
		public boolean matchesSafely(View view) {
			if (!(view instanceof EditText)) {
				return false;
			}
			EditText editText = (EditText) view;
			return expectedError.equals(editText.getError());
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("with error: " + expectedError);
		}
	}
}