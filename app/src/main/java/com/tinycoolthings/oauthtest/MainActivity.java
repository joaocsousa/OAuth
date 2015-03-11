package com.tinycoolthings.oauthtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tinycoolthings.oauthtest.info.FacebookActivity;
import com.tinycoolthings.oauthtest.info.GoogleActivity;
import com.tinycoolthings.oauthtest.info.TwitterActivity;

/**
 * Created by joaosousa on 08/03/15.
 */
public class MainActivity extends ActionBarActivity /*implements ConnectionCallbacks, OnConnectionFailedListener*/ {

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
	}

}
