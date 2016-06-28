/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.androidzeitgeist.webcards.viewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.databinding.ActivityVideoBinding;

@SuppressLint("SetJavaScriptEnabled") // We need JavaScript support for displaying videos
public class VideoActivity extends Activity {
    private static final String EXTRA_VIDEO_URL = "video_url";

    public static void show(Context context, String videoUrl) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(EXTRA_VIDEO_URL, videoUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityVideoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_video);

        binding.videoView.getSettings().setJavaScriptEnabled(true);
        binding.videoView.loadUrl(getIntent().getStringExtra(EXTRA_VIDEO_URL));
    }
}
