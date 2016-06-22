/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.androidzeitgeist.webcards.overlay.OverlayService;

/**
 * This activity handles incoming URLs and hands them off to a background service.
 */
public class URLActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        if (intent == null) {
            finishAffinity();
            return;
        }

        final String url = intent.getDataString();
        if (TextUtils.isEmpty(url)) {
            finishAffinity();
            return;
        }

        OverlayService.processUrl(this, url);
        finishAffinity();
    }
}
