package com.tinycoolthings.oauthtest.results;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.plus.model.people.Person;
import com.squareup.picasso.Picasso;
import com.tinycoolthings.oauthtest.R;

import static com.tinycoolthings.oauthtest.utils.StaticData.staticData;

/**
 * Created by joaosousa on 08/03/15.
 */
public class GoogleActivity extends ActionBarActivity {

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

		if (staticData().getGoogleProfile().isPresent()) {
			Person profile = staticData().getGoogleProfile().get();
			ImageView profilePictureView = (ImageView) findViewById(R.id.activity_google_picture);
			Picasso.with(this).load(profile.getImage().getUrl().substring(0,
				profile.getImage().getUrl().length() - 2)
				+ 400).into(profilePictureView);
			TextView informationTextView = (TextView) findViewById(R.id.activity_google_information);
			StringBuilder infoBuilder = new StringBuilder()
				.append("ID: ").append(profile.getId()).append("\n\n")
				.append("Nickname: ").append(profile.getNickname()).append("\n\n")
				.append("Name: ").append(profile.getName()).append("\n\n")
				.append("DisplayName: ").append(profile.getDisplayName()).append("\n\n")
				.append("Birthday: ").append(profile.getBirthday()).append("\n\n")
				.append("Age: ").append(profile.getAgeRange()).append("\n\n")
				.append("Location: ").append(profile.getCurrentLocation());
			informationTextView.setText(infoBuilder.toString());
		}

	}

}
