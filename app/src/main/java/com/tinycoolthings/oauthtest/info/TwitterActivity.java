package com.tinycoolthings.oauthtest.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tinycoolthings.oauthtest.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

/**
 * Created by joaosousa on 08/03/15.
 */
public class TwitterActivity extends ActionBarActivity {

	private TwitterLoginButton mTwitterLoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter);

		Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
		setSupportActionBar(toolbar);

		toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NavUtils.navigateUpFromSameTask(TwitterActivity.this);
			}
		});

		TwitterAuthConfig authConfig =
			new TwitterAuthConfig("consumerKey",
				"consumerSecret");
		Fabric.with(this, new Twitter(authConfig));

		mTwitterLoginButton = (TwitterLoginButton) findViewById(R.id.activity_twitter_login_button);
		mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
			@Override
			public void success(Result<TwitterSession> result) {
				// Do something with result, which provides a
				// TwitterSession for making API calls
			}

			@Override
			public void failure(TwitterException exception) {
				// Do something on failure
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Pass the activity result to the login button.
		mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			TwitterSession session = Twitter.getSessionManager().getActiveSession();
			TwitterAuthToken authToken = session.getAuthToken();
			String token = authToken.token;
			String secret = authToken.secret;
		}
	}



}
