package com.tinycoolthings.oauthtest.info.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.tinycoolthings.oauthtest.R;

import static java.util.Arrays.asList;

/**
 * Created by joaosousa on 08/03/15.
 */
public class FacebookActivity extends AppCompatActivity {

	private CallbackManager mCallbackManager;

	private FacebookCallback mFacebookCallback = new FacebookCallback() {
		@Override
		public void onSuccess(Object o) {
			updateSessionInfo();
		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onError(FacebookException error) {
			updateSessionInfo();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(getApplicationContext());

		mCallbackManager = CallbackManager.Factory.create();

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

		LoginManager.getInstance().registerCallback(mCallbackManager, mFacebookCallback);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mCallbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStart() {
		super.onStart();
		updateSessionInfo();
	}

	private void updateSessionInfo() {
//		GraphRequest meRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//			@Override
//			public void onCompleted(JSONObject object, GraphResponse response) {
//
//			}
//		});
//		Bundle parameters = new Bundle();
//		parameters.putString(FIELDS_PARAM, "id,name,link");
//		meRequest.setParameters(parameters);
//		meRequest.executeAsync();

		Profile profile = Profile.getCurrentProfile();
		if (profile != null) {
			findViewById(R.id.activity_facebook_information_container).setVisibility(View.VISIBLE);
			findViewById(R.id.activity_facebook_login_button).setVisibility(View.GONE);
			ProfilePictureView profilePictureView = (ProfilePictureView) findViewById(R.id.activity_facebook_profile_picture);
			profilePictureView.setProfileId(profile.getId());
			TextView informationTextView = (TextView) findViewById(R.id.activity_facebook_information);
			StringBuilder infoBuilder = new StringBuilder()
				.append("ID: ").append(profile.getId()).append("\n\n")
				.append("Username: ").append(profile.getName()).append("\n\n")
				.append("Full name: ").append(profile.getName()).append("\n\n")
				.append("Composed Name: ").append(
					TextUtils.join(" ",
						new String[]{
							profile.getFirstName(),
							profile.getMiddleName(),
							profile.getLastName()}))
				.append("\n\n");
			informationTextView.setText(infoBuilder.toString());
		} else {
			findViewById(R.id.activity_facebook_information_container).setVisibility(View.GONE);
			findViewById(R.id.activity_facebook_login_button).setVisibility(View.VISIBLE);
		}

	}

}
