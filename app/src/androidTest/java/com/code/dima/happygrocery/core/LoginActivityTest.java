package com.code.dima.happygrocery.core;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.code.dima.happygrocery.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void loginActivityTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.phoneButton),
                        childAtPosition(
                                allOf(withId(R.id.main_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.phone_number),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.phone_layout),
                                        0),
                                0)));
        textInputEditText.perform(scrollTo(), replaceText("339"), closeSoftKeyboard());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.phone_number), withText("339"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.phone_layout),
                                        0),
                                0)));
        textInputEditText2.perform(scrollTo(), replaceText("3398"));

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.phone_number), withText("3398"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.phone_layout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText3.perform(closeSoftKeyboard());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textInputEditText4 = onView(
                allOf(withId(R.id.phone_number), withText("3398"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.phone_layout),
                                        0),
                                0)));
        textInputEditText4.perform(scrollTo(), replaceText("33987"));

        ViewInteraction textInputEditText5 = onView(
                allOf(withId(R.id.phone_number), withText("33987"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.phone_layout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText5.perform(closeSoftKeyboard());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textInputEditText6 = onView(
                allOf(withId(R.id.phone_number), withText("33987"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.phone_layout),
                                        0),
                                0)));
        textInputEditText6.perform(scrollTo(), replaceText("3398761494"));

        ViewInteraction textInputEditText7 = onView(
                allOf(withId(R.id.phone_number), withText("3398761494"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.phone_layout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText7.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.send_code), withText("Verifica numero di telefono"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatButton.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("com.google.android.material.appbar.AppBarLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0)),
                        4),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton2.perform(scrollTo(), click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
