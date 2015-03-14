package com.tinycoolthings.oauthtest.info.twitter.network;

import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;

/**
 * Created by joaosousa on 12/03/15.
 */
public class CustomTwitterApiClient extends TwitterApiClient {

	public CustomTwitterApiClient(Session session) {
		super(session);
	}

	public UsersService getUsersService() {
		return getService(UsersService.class);
	}

}
