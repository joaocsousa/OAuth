package com.tinycoolthings.oauthtest.info.twitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tinycoolthings.oauthtest.R;
import com.tinycoolthings.oauthtest.info.twitter.network.CustomTwitterApiClient;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

/**
 * Created by joaosousa on 08/03/15.
 */
public class TwitterActivity extends AppCompatActivity {

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
			updateSessionInfo(Twitter.getSessionManager().getActiveSession());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateSessionInfo(Twitter.getSessionManager().getActiveSession());
	}

	private void updateSessionInfo(TwitterSession session) {
		if (session != null) {
			findViewById(R.id.activity_twitter_information_container).setVisibility(View.VISIBLE);
			mTwitterLoginButton.setVisibility(View.GONE);
			new CustomTwitterApiClient(session).getUsersService().show(session.getUserId(), session.getUserName(), true, new Callback<User>() {
				@Override
				public void success(Result<User> result) {
					TextView informationTextView = (TextView) findViewById(R.id.activity_twitter_information);
					User user = result.data;
					if (user != null) {
						Picasso.with(TwitterActivity.this).load(user.profileImageUrlHttps.replace("_normal", ""))
							.into((ImageView) findViewById(R.id.activity_twitter_picture));
						informationTextView.setText(
							new StringBuilder()
								.append("Email: ").append(user.email).append("\n")
								.append("Name: ").append(user.name).append("\n")
								.append("ScreenName: ").append(user.screenName).toString()
						);
					}
				}

				@Override
				public void failure(TwitterException e) {
					Toast.makeText(TwitterActivity.this, "Failed to fetch user info", Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			findViewById(R.id.activity_twitter_information_container).setVisibility(View.GONE);
			mTwitterLoginButton.setVisibility(View.VISIBLE);
		}
	}

}
