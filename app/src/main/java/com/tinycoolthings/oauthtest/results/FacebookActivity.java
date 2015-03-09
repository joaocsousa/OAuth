package com.tinycoolthings.oauthtest.results;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.common.base.Joiner;
import com.tinycoolthings.oauthtest.R;

/**
 * Created by joaosousa on 08/03/15.
 */
public class FacebookActivity extends ActionBarActivity {

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

		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
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
		}
	}

}
