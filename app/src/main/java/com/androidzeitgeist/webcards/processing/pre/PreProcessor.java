/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.processing.pre;

import okhttp3.Request;

/**
 * Processor that can modify a request before it is executed.
 */
public interface PreProcessor {
    Request process(Request request);
}
