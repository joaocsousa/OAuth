package com.tinycoolthings.oauthtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.tinycoolthings.oauthtest.results.FacebookActivity;

import java.util.Arrays;

/**
 * Created by joaosousa on 08/03/15.
 */
public class MainActivity extends ActionBarActivity implements Session.StatusCallback {

	UiLifecycleHelper mFacebookUiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
		setSupportActionBar(toolbar);

		LoginButton authButton = (LoginButton) findViewById(R.id.facebookLoginButton);
		authButton.setReadPermissions(Arrays.asList("public_profile", "email"));
		mFacebookUiHelper = new UiLifecycleHelper(this, this);
		mFacebookUiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mFacebookUiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mFacebookUiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		mFacebookUiHelper.onPause();
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
	public void call(Session session, SessionState state, Exception exception) {
		onSessionStateChange(session, state, exception);
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.i(MainActivity.class.getSimpleName(), "Facebook session is open!");
			startActivity(new Intent(this, FacebookActivity.class));
		} else if (state.isClosed()) {
			Log.i(MainActivity.class.getSimpleName(), "Facebook session is closed!");
		}
	}

}
