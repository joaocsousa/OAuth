package com.tinycoolthings.oauthtest.info.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.google.common.base.Joiner;
import com.tinycoolthings.oauthtest.R;

import static java.util.Arrays.asList;

/**
 * Created by joaosousa on 08/03/15.
 */
public class FacebookActivity extends ActionBarActivity implements Session.StatusCallback {

	private UiLifecycleHelper mFacebookUiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook);

		Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
		setSupportActionBar(toolbar);

		toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NavUtils.navigateUpFromSameTask(FacebookActivity.this);
			}
		});

		LoginButton authButton = (LoginButton) findViewById(R.id.activity_facebook_login_button);
		authButton.setReadPermissions(asList(getResources().getStringArray(R.array.facebook_permissions)));
		mFacebookUiHelper = new UiLifecycleHelper(this, this);
		mFacebookUiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mFacebookUiHelper.onResume();
		updateSessionInfo(Session.getActiveSession());
	}

	@Override
	public void onPause() {
		super.onPause();
		mFacebookUiHelper.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		mFacebookUiHelper.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mFacebookUiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mFacebookUiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mFacebookUiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Toast.makeText(this, "Facebook session is open!", Toast.LENGTH_SHORT).show();
		} else if (state.isClosed()) {
			Toast.makeText(this, "Facebook session is closed!", Toast.LENGTH_SHORT).show();

		}
		updateSessionInfo(session);
	}

	private void updateSessionInfo(Session session) {
		if (session != null && session.isOpened()) {
			findViewById(R.id.activity_facebook_information_container).setVisibility(View.VISIBLE);
			findViewById(R.id.activity_facebook_login_button).setVisibility(View.GONE);
			Request.newMeRequest(session, new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.activity_facebook_profile_picture);
						profilePictureView.setProfileId(user.getId());
						TextView informationTextView = (TextView) findViewById(R.id.activity_facebook_information);
						StringBuilder infoBuilder = new StringBuilder()
							.append("ID: ").append(user.getId()).append("\n\n")
							.append("Username: ").append(user.getUsername()).append("\n\n")
							.append("Full name: ").append(user.getName()).append("\n\n")
							.append("Composed Name: ").append(
								Joiner.on(" ").skipNulls().join(
									user.getFirstName(),
									user.getMiddleName(),
									user.getLastName())).append("\n\n")
							.append("Birthday: ").append(user.getBirthday()).append("\n\n")
							.append("Location: ").append(user.getLocation());
						informationTextView.setText(infoBuilder.toString());
					}
				}
			}).executeAsync();
		} else {
			findViewById(R.id.activity_facebook_information_container).setVisibility(View.GONE);
			findViewById(R.id.activity_facebook_login_button).setVisibility(View.VISIBLE);
		}
	}

}
