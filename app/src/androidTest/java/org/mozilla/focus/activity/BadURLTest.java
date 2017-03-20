/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.activity;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.text.format.DateUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.focus.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.mozilla.focus.fragment.FirstrunFragment.FIRSTRUN_PREF;

// This test opens enters and invalid URL, and Focus should provide an appropriate error message
@RunWith(AndroidJUnit4.class)
public class BadURLTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<MainActivity>(MainActivity.class) {

        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();

            Context appContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext()
                    .getApplicationContext();

            PreferenceManager.getDefaultSharedPreferences(appContext)
                    .edit()
                    .putBoolean(FIRSTRUN_PREF, false)
                    .apply();
        }
    };

    @Test
    public void BadURLcheckTest() throws InterruptedException, UiObjectNotFoundException {
        UiDevice mDevice;
        final long waitingTime = DateUtils.SECOND_IN_MILLIS * 2;

        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject firstViewBtn = mDevice.findObject(new UiSelector()
                .resourceId("org.mozilla.focus.debug:id/firstrun_exitbutton")
                .enabled(true));
        UiObject urlBar = mDevice.findObject(new UiSelector()
                .resourceId("org.mozilla.focus.debug:id/url")
                .clickable(true));
        UiObject inlineAutocompleteEditText = mDevice.findObject(new UiSelector()
                .resourceId("org.mozilla.focus.debug:id/url_edit")
                .focused(true)
                .enabled(true));
        BySelector hint = By.clazz("android.widget.TextView")
                .res("org.mozilla.focus.debug", "search_hint")
                .clickable(true);
        UiObject webView = mDevice.findObject(new UiSelector()
                .className("android.webkit.Webview")
                .focused(true)
                .enabled(true));
        UiObject tryAgainBtn = mDevice.findObject(new UiSelector()
                .resourceId("errorTryAgain")
                .clickable(true));
        ViewInteraction floatingEraseButton = onView(
                allOf(withId(R.id.erase), isDisplayed()));
        UiObject notFoundMsg = mDevice.findObject(new UiSelector()
        .resourceId("et_dnsNotFound")
        .description("The address wasn’t understood")
        .enabled(true));
        UiObject notFounddetailedMsg = mDevice.findObject(new UiSelector()
                .description("You might need to install other software to open this address.")
                .enabled(true));

        /* Wait for First View UI */
        firstViewBtn.waitForExists(waitingTime);
        firstViewBtn.click();

        /* provide an invalid URL */
        urlBar.waitForExists(waitingTime);
        urlBar.click();
        inlineAutocompleteEditText.waitForExists(waitingTime);
        inlineAutocompleteEditText.clearTextField();
        inlineAutocompleteEditText.setText("htps://www.mozilla.org");
        mDevice.wait(Until.hasObject(hint), waitingTime);
        mDevice.pressKeyCode(KEYCODE_ENTER);
        tryAgainBtn.waitForExists(waitingTime);

        /* Check for error message */
        assertTrue(notFoundMsg.exists());
        assertTrue(notFounddetailedMsg.exists());
        assertTrue(tryAgainBtn.exists());

        /* provide URL that is handled by some other app */
        floatingEraseButton.perform(click());
        urlBar.waitForExists(waitingTime);
        urlBar.click();
        inlineAutocompleteEditText.waitForExists(waitingTime);
        inlineAutocompleteEditText.clearTextField();
        inlineAutocompleteEditText.setText("market://details?id=org.mozilla.firefox&referrer=" +
                "utm_source%3Dmozilla%26utm_medium%3DReferral%26utm_campaign%3Dmozilla-org");
        mDevice.wait(Until.hasObject(hint), waitingTime);
        mDevice.pressKeyCode(KEYCODE_ENTER);
        tryAgainBtn.waitForExists(waitingTime);

        /* Check for error message */
        assertTrue(notFoundMsg.exists());
        assertTrue(notFounddetailedMsg.exists());
        assertTrue(tryAgainBtn.exists());
    }
}
