package com.tinycoolthings.oauthtest.twitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;
import com.tinycoolthings.oauthtest.R;
import com.tinycoolthings.oauthtest.twitter.network.CustomTwitterApiClient;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

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
			findViewById(R.id.activity_twitter_information_container).setVisibility(View.VISIBLE);
			new CustomTwitterApiClient(session).getUsersService().show(session.getUserId(), session.getUserName(), true, new Callback<User>() {
				@Override
				public void success(Result<User> result) {
					TextView informationTextView = (TextView) findViewById(R.id.activity_twitter_information);
					Optional<User> candidateUser = Optional.fromNullable(result.data);
					if (candidateUser.isPresent()) {
						User user = candidateUser.get();
						Picasso.with(TwitterActivity.this).load(user.profileImageUrlHttps)
							.into((ImageView) findViewById(R.id.activity_twitter_picture));
						StringBuilder infoBuilder = new StringBuilder()
							.append("Email: ").append(user.email).append("\n")
							.append("Name: ").append(user.name).append("\n")
							.append("ScreenName: ").append(user.screenName);
						informationTextView.setText(
							infoBuilder.toString()
						);
					}
				}

				@Override
				public void failure(TwitterException e) {

				}
			});

		}
	}



}
