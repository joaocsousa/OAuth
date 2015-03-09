package com.tinycoolthings.oauthtest.utils;

import com.google.android.gms.plus.model.people.Person;
import com.google.common.base.Optional;

/**
 * Created by joaosousa on 09/03/15.
 */
public enum StaticData {

	INSTANCE;

	private Optional<Person> mGoogleProfile = Optional.absent();

	public static StaticData staticData() {
		return INSTANCE;
	}

	public void setGoogleProfile(Person profile) {
		mGoogleProfile = Optional.fromNullable(profile);
	}

	public Optional<Person> getGoogleProfile() {
		return mGoogleProfile;
	}


}
