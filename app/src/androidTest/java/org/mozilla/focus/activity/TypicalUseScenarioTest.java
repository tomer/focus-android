/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.activity;

import android.content.Context;
import android.content.res.Resources;
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
import android.support.test.uiautomator.UiScrollable;
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

@RunWith(AndroidJUnit4.class)
public class TypicalUseScenarioTest {

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
    public void TypicalUseTest() throws InterruptedException, UiObjectNotFoundException {
        UiDevice mDevice;
        final long waitingTime = DateUtils.SECOND_IN_MILLIS * 2;

        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        ViewInteraction menuButton = onView(
                allOf(withId(R.id.menu),
                        isDisplayed()));

        /* Wait for app to load, and take the First View screenshot */
        UiObject firstViewBtn = mDevice.findObject(new UiSelector()
                .resourceId("org.mozilla.focus.debug:id/firstrun_exitbutton")
                .enabled(true));
        UiObject inlineAutocompleteEditText = mDevice.findObject(new UiSelector()
                .resourceId("org.mozilla.focus.debug:id/url_edit")
                .focused(true)
                .enabled(true));
        UiObject urlBar = mDevice.findObject(new UiSelector()
                .resourceId("org.mozilla.focus.debug:id/url")
                .clickable(true));
        UiObject webView = mDevice.findObject(new UiSelector()
                .className("android.webkit.Webview")
                .focused(true)
                .enabled(true));
        UiObject searchHint = mDevice.findObject(new UiSelector()
                .resourceId("org.mozilla.focus.debug:id/search_hint")
                .clickable(true));
        ViewInteraction floatingEraseButton = onView(
                allOf(withId(R.id.erase),
                        isDisplayed()));
        UiObject erasedMsg = mDevice.findObject(new UiSelector()
                .text("Your browsing history has been erased.")
                .resourceId("org.mozilla.focus.debug:id/snackbar_text")
                .enabled(true));
        UiObject lockIcon = mDevice.findObject(new UiSelector()
        .resourceId("org.mozilla.focus.debug:id/lock")
        .description("Secure connection"));

        firstViewBtn.waitForExists(waitingTime);
        firstViewBtn.click();

        // Let's search for something
        urlBar.waitForExists(waitingTime);
        urlBar.click();

        inlineAutocompleteEditText.waitForExists(waitingTime);
        inlineAutocompleteEditText.clearTextField();
        inlineAutocompleteEditText.setText("mozilla focus");
        searchHint.waitForExists(waitingTime);
        assertTrue(searchHint.getText().equals("Search for mozilla focus"));
        searchHint.click();
        webView.waitForExists(waitingTime);
        assertTrue (urlBar.getText().contains("mozilla"));
        assertTrue (urlBar.getText().contains("focus"));

        // Let's delete my history
        floatingEraseButton.perform(click());
        erasedMsg.waitForExists(waitingTime);
        assertTrue(erasedMsg.exists());
        assertTrue(urlBar.exists());

        // Let's go to an actual URL which is https://
        urlBar.click();
        inlineAutocompleteEditText.waitForExists(waitingTime);
        inlineAutocompleteEditText.clearTextField();
        inlineAutocompleteEditText.setText("https://www.google.com");
        searchHint.waitForExists(waitingTime);
        assertTrue(searchHint.getText().equals("Search for https://www.google.com"));
        mDevice.pressKeyCode(KEYCODE_ENTER);
        webView.waitForExists(waitingTime);
        assertTrue (urlBar.getText().contains("https://www.google"));
        assertTrue (lockIcon.exists());

        // Let's delete my history again
        floatingEraseButton.perform(click());
        erasedMsg.waitForExists(waitingTime);
        assertTrue(erasedMsg.exists());
        assertTrue(urlBar.exists());

        // Let's go to an actual URL which is http://
        urlBar.click();
        inlineAutocompleteEditText.waitForExists(waitingTime);
        inlineAutocompleteEditText.clearTextField();
        inlineAutocompleteEditText.setText("http://www.example.com");
        searchHint.waitForExists(waitingTime);
        assertTrue(searchHint.getText().equals("Search for http://www.example.com"));
        mDevice.pressKeyCode(KEYCODE_ENTER);
        webView.waitForExists(waitingTime);
        assertTrue (urlBar.getText().contains("http://www.example.com"));
        assertTrue (!lockIcon.exists());

        /* Go to settings and disable everything */
        menuButton.perform(click());
        UiObject settingMenuItem = mDevice.findObject(new UiSelector()
                .resourceId("org.mozilla.focus.debug:id/settings"));
        settingMenuItem.click();

        BySelector settingsHeading = By.clazz("android.view.View")
                .res("org.mozilla.focus.debug","toolbar")
                .enabled(true);
        mDevice.wait(Until.hasObject(settingsHeading),waitingTime);

        UiScrollable settingsList = new UiScrollable(new UiSelector()
                .resourceId("android:id/list").scrollable(true));
        UiObject blockAdTrackerEntry = settingsList.getChild(new UiSelector()
                .className("android.widget.LinearLayout")
                .instance(2));
        UiObject blockAdTrackerValue = blockAdTrackerEntry.getChild(new UiSelector()
                .resourceId("android:id/switch_widget"));

        UiObject blockAnalyticTrackerEntry = settingsList.getChild(new UiSelector()
                .className("android.widget.LinearLayout")
                .instance(4));
        UiObject blockAnalyticTrackerValue = blockAnalyticTrackerEntry.getChild(new UiSelector()
                .resourceId("android:id/switch_widget"));

        UiObject blockSocialTrackerEntry = settingsList.getChild(new UiSelector()
                .className("android.widget.LinearLayout")
                .instance(6));
        UiObject blockSocialTrackerValue = blockSocialTrackerEntry.getChild(new UiSelector()
                .resourceId("android:id/switch_widget"));

        blockAdTrackerEntry.click();
        blockAnalyticTrackerEntry.click();
        blockSocialTrackerEntry.click();
        assertTrue(blockAdTrackerValue.getText().equals("OFF"));
        assertTrue(blockAnalyticTrackerValue.getText().equals("OFF"));
        assertTrue(blockSocialTrackerValue.getText().equals("OFF"));

        UiObject navigateUp = mDevice.findObject(new UiSelector()
        .description("Navigate up"));
        navigateUp.click();

        //Back to the webpage
        webView.waitForExists(waitingTime);
        assertTrue (urlBar.getText().contains("http://www.example.com"));
        assertTrue (!lockIcon.exists());
    }
}
