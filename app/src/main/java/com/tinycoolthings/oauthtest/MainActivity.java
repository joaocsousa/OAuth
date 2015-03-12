package com.tinycoolthings.oauthtest;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.common.base.Optional;
import com.tinycoolthings.oauthtest.info.FacebookActivity;
import com.tinycoolthings.oauthtest.info.GoogleActivity;
import com.tinycoolthings.oauthtest.twitter.TwitterActivity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

/**
 * Created by joaosousa on 08/03/15.
 */
public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
		setSupportActionBar(toolbar);

		findViewById(R.id.activity_main_facebook_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, FacebookActivity.class));
			}
		});

		findViewById(R.id.activity_main_twitter_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, TwitterActivity.class));
			}
		});

		findViewById(R.id.activity_main_google_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, GoogleActivity.class));
			}
		});

		Optional<String> candidateTwitterApiKey = Optional.absent();
		Optional<String> candidateTwitterApiSecret = Optional.absent();

		try {
			ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			candidateTwitterApiKey = Optional.fromNullable((String) applicationInfo.metaData.get("twitterApiKey"));
			candidateTwitterApiSecret = Optional.fromNullable((String) applicationInfo.metaData.get("twitterApiSecret"));
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		if (candidateTwitterApiKey.isPresent() && candidateTwitterApiSecret.isPresent()) {
			TwitterAuthConfig authConfig = new TwitterAuthConfig(candidateTwitterApiKey.get(), candidateTwitterApiSecret.get());
			Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
		}


	}

}
