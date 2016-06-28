/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.viewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.databinding.ActivityPhotoBinding;
import com.squareup.picasso.Picasso;

/**
 * Activity for displaying a single photo.
 */
public class PhotoActivity extends Activity {
    private static final String EXTRA_PHOTO_URL = "photo_url";

    public static void show(Context context, String photoUrl) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(EXTRA_PHOTO_URL, photoUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, 0);

        ActivityPhotoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_photo);

        Picasso.with(this)
                .load(getIntent().getStringExtra(EXTRA_PHOTO_URL))
                .into(binding.imageView);
    }

    public void onClose(View view) {
        finish();

        overridePendingTransition(0, android.R.anim.fade_out);
    }
}
