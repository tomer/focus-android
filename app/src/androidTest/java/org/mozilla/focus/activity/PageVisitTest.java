package org.mozilla.focus.activity;

import android.content.Context;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.text.format.DateUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.focus.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.mozilla.focus.fragment.FirstrunFragment.FIRSTRUN_PREF;

// This test visits each page and checks whether some essential elements are being displayed
@RunWith(AndroidJUnit4.class)
public class PageVisitTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<MainActivity>(MainActivity.class) {

        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();

            Context appContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext()
                    .getApplicationContext();
            Resources resources = appContext.getResources();

            PreferenceManager.getDefaultSharedPreferences(appContext)
                    .edit()
                    .putBoolean(FIRSTRUN_PREF, false)
                    .apply();
        }
    };

    @Test
    public void visitPagesTest() throws InterruptedException, UiObjectNotFoundException {

        // Initialize UiDevice instance
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        final long waitingTime = DateUtils.SECOND_IN_MILLIS * 2;

        UiObject firstViewBtn = mDevice.findObject(new UiSelector()
                .resourceId("org.mozilla.focus.debug:id/firstrun_exitbutton")
                .enabled(true));
        UiObject urlBar = mDevice.findObject(new UiSelector()
                .resourceId("org.mozilla.focus.debug:id/url")
                .clickable(true));
        ViewInteraction menuButton = onView(
                allOf(withId(R.id.menu),
                        isDisplayed()));
        UiObject webView = mDevice.findObject(new UiSelector()
                .className("android.webkit.Webview")
                .enabled(true));
        UiObject RightsItem = mDevice.findObject(new UiSelector()
                .className("android.widget.LinearLayout")
                .instance(2));
        UiObject rightsPartialText = mDevice.findObject(new UiSelector()
                .className("android.view.View")
                .description("Firefox Focus is free and open source software " +
                        "made by Mozilla and other contributors."));
        UiObject rightsHeading = mDevice.findObject(new UiSelector()
                .className("android.widget.TextView")
                .text("Your Rights"));
        UiObject AboutItem = mDevice.findObject(new UiSelector()
                .className("android.widget.LinearLayout")
                .instance(0)
                .enabled(true));
        UiObject aboutPartialText = mDevice.findObject(new UiSelector()
                .className("android.view.View")
                .description("Firefox Focus (Dev) puts you in control."));
        UiObject aboutHeading = mDevice.findObject(new UiSelector()
                .className("android.widget.TextView")
                .text("About"));
        UiObject HelpItem = mDevice.findObject(new UiSelector()
                .className("android.widget.LinearLayout")
                .instance(1)
                .enabled(true));

        /* Wait for app to load, and take the First View screenshot */
        firstViewBtn.waitForExists(waitingTime);
        firstViewBtn.click();
        urlBar.waitForExists(waitingTime);

        /* Main View Menu */
        menuButton.perform(click());
        RightsItem.waitForExists(waitingTime);

        /* Your Rights Page */
        RightsItem.click();
        webView.waitForExists(waitingTime);
        assertTrue(rightsHeading.exists());
        assertTrue(rightsPartialText.exists());

        /* About Page */
        mDevice.pressBack();
        menuButton.perform(click());
        AboutItem.click();
        webView.waitForExists(waitingTime);
        assertTrue(aboutHeading.exists());
        assertTrue(aboutPartialText.exists());

        /* Help Page */
        mDevice.pressBack();
        menuButton.perform(click());
        HelpItem.click();
        webView.waitForExists(waitingTime);
        // TBD
    }
}
