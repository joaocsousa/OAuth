package com.tinycoolthings.oauthtest;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.tinycoolthings.oauthtest.results.FacebookActivity;
import com.tinycoolthings.oauthtest.results.GoogleActivity;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import static com.google.android.gms.plus.Plus.PeopleApi;
import static com.tinycoolthings.oauthtest.utils.StaticData.staticData;
import static java.util.Arrays.asList;

/**
 * Created by joaosousa on 08/03/15.
 */
public class MainActivity extends ActionBarActivity implements Session.StatusCallback, ConnectionCallbacks, OnConnectionFailedListener {

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;

	private UiLifecycleHelper mFacebookUiHelper;

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	/* A flag indicating that a PendingIntent is in progress and prevents
	   * us from starting further intents.
	   */
	private boolean mIntentInProgress;

	/* Track whether the sign-in button has been clicked so that we know to resolve
	 * all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;

	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
		setSupportActionBar(toolbar);

		LoginButton authButton = (LoginButton) findViewById(R.id.facebookLoginButton);
		authButton.setReadPermissions(asList(getResources().getStringArray(R.array.facebook_permissions)));
		mFacebookUiHelper = new UiLifecycleHelper(this, this);
		mFacebookUiHelper.onCreate(savedInstanceState);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(Plus.API)
			.addScope(Plus.SCOPE_PLUS_LOGIN)
			.addScope(Plus.SCOPE_PLUS_PROFILE)
			.build();

		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mGoogleApiClient.isConnecting()) {
					mSignInClicked = true;
					resolveSignInError();
				}
			}
		});

		findViewById(R.id.facebookInfoButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, FacebookActivity.class));
			}
		});


		findViewById(R.id.googleInfoButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, GoogleActivity.class));
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		mFacebookUiHelper.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mFacebookUiHelper.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			if (resultCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
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
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
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
		if (state.isOpened()) {
			Toast.makeText(this, "Facebook session is open!", Toast.LENGTH_SHORT).show();
			findViewById(R.id.facebookInfoButton).setVisibility(View.VISIBLE);
			startActivity(new Intent(this, FacebookActivity.class));
		} else if (state.isClosed()) {
			Toast.makeText(this, "Facebook session is closed!", Toast.LENGTH_SHORT).show();
			findViewById(R.id.facebookInfoButton).setVisibility(View.GONE);
		}
	}

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
			} catch (IntentSender.SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the user clicks
			// 'sign-in'.
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
			Toast.makeText(this, "Google session is closed!", Toast.LENGTH_SHORT).show();
			findViewById(R.id.googleInfoButton).setVisibility(View.GONE);
			setGooglePlusButtonText("Sign in");
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// We've resolved any connection errors.  mGoogleApiClient can be used to
		// access Google APIs on behalf of the user.
		mSignInClicked = false;
		Toast.makeText(this, "Google session is open!", Toast.LENGTH_SHORT).show();
		if (PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
			staticData().setGoogleProfile(PeopleApi.getCurrentPerson(mGoogleApiClient));
			findViewById(R.id.googleInfoButton).setVisibility(View.VISIBLE);
			setGooglePlusButtonText("Sign out");
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		mGoogleApiClient.connect();
	}

	protected void setGooglePlusButtonText(String buttonText) {
		SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
		for (int i = 0; i < signInButton.getChildCount(); i++) {
			View v = signInButton.getChildAt(i);
			if (v instanceof TextView) {
				TextView mTextView = (TextView) v;
				mTextView.setText(buttonText);
				return;
			}
		}
	}

}
