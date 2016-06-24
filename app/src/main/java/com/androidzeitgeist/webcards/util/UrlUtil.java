/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.util;

import android.net.Uri;

public class UrlUtil {
    public static String formatForDisplaying(String url) {
        String host = Uri.parse(url).getHost();
        if (host.startsWith("www.")) {
            host = host.substring(4);
        }
        if (host.startsWith("m.")) {
            host = host.substring(2);
        }
        return host;
    }
}
