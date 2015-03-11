package com.tinycoolthings.oauthtest.info;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;
import com.tinycoolthings.oauthtest.R;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/**
 * Created by joaosousa on 08/03/15.
 */
public class GoogleActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {

	// Request code to sign in
	private static final int REQUEST_CODE_SIGN_IN = 1001;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	// A flag indicating that a PendingIntent is in progress and prevents us from starting further intents.
	private boolean mIntentInProgress;

	//Store the connection result from onConnectionFailed callbacks so that we can resolve them when the user clicks sign-in.
	private ConnectionResult mConnectionResult;

	// Track whether the sign-in button has been clicked so that we know to resolve all issues preventing sign-in without waiting.
	private boolean mSignInClicked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google);

		Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
		setSupportActionBar(toolbar);

		toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NavUtils.navigateUpFromSameTask(GoogleActivity.this);
			}
		});

		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(Plus.API)
			.addScope(Plus.SCOPE_PLUS_LOGIN)
			.addScope(Plus.SCOPE_PLUS_PROFILE)
			.build();

		findViewById(R.id.activity_google_login_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mGoogleApiClient.isConnecting()) {
					mSignInClicked = true;
					if (mGoogleApiClient.isConnected()) {
						revokeAccess();
					} else {
						resolveSignInError();
					}
				}
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_SIGN_IN) {
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
	public void onConnected(Bundle bundle) {
		// We've resolved any connection errors.  mGoogleApiClient can be used to
		// access Google APIs on behalf of the user.
		mSignInClicked = false;
		Toast.makeText(this, "Google session is open!", Toast.LENGTH_SHORT).show();
		if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
			updateSessionInfo(true);
			setGooglePlusButtonText("Sign out");
		}
	}

	private void updateSessionInfo(Boolean connected) {
		Optional<Person> candidatePerson = Optional.absent();
		if (connected) {
			candidatePerson = Optional.fromNullable(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient));
		}
		if (connected && candidatePerson.isPresent()) {
			Person person = candidatePerson.get();
			findViewById(R.id.activity_google_information_container).setVisibility(View.VISIBLE);
			ImageView profilePictureView = (ImageView) findViewById(R.id.activity_google_picture);
			Picasso.with(this).load(person.getImage().getUrl()).into(profilePictureView);
			TextView informationTextView = (TextView) findViewById(R.id.activity_google_information);
			StringBuilder infoBuilder = new StringBuilder()
				.append("ID: ").append(person.getId()).append("\n\n")
				.append("Nickname: ").append(person.getNickname()).append("\n\n")
				.append("Name: ").append(
					Joiner.on(" ").skipNulls().join(
						person.getName().getGivenName(),
						person.getName().getMiddleName(),
						person.getName().getFamilyName())
				).append("\n\n")
				.append("DisplayName: ").append(person.getDisplayName()).append("\n\n")
				.append("Birthday: ").append(person.getBirthday()).append("\n\n")
				.append("Age: ").append(
					Joiner.on(" ").skipNulls().join(
						person.getAgeRange().getMin(),
						person.getAgeRange().getMax())
				).append("\n\n")
				.append("Location: ").append(person.getCurrentLocation());
			informationTextView.setText(infoBuilder.toString());
		} else {
			findViewById(R.id.activity_google_information_container).setVisibility(View.GONE);
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the user clicks
			// 'sign-in'.
			mConnectionResult = connectionResult;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
			Toast.makeText(this, "Google session is closed!", Toast.LENGTH_SHORT).show();
			updateSessionInfo(false);
			setGooglePlusButtonText("Sign in");
		}
	}

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		if (mConnectionResult != null && mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				startIntentSenderForResult(mConnectionResult.getResolution()
					.getIntentSender(), REQUEST_CODE_SIGN_IN, null, 0, 0, 0);
			} catch (IntentSender.SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	private void revokeAccess() {
		Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
		Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
			.setResultCallback(new ResultCallback<Status>() {
				@Override
				public void onResult(Status status) {
					mGoogleApiClient.connect();
					updateSessionInfo(false);
				}
			});
	}

	protected void setGooglePlusButtonText(String buttonText) {
		SignInButton signInButton = (SignInButton) findViewById(R.id.activity_google_login_button);
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
