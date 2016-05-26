package com.ctx.twitterparser;

import android.net.Uri;
import android.os.AsyncTask;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

/**
 * Created by rupam.ghosh on 25/05/16.
 */
public class GetOAuthRequestToken extends AsyncTask<Void, Void, Void> {

  public interface GetOAuthRequestTokenListener {
    void onReceiveAuthUri(RequestToken requestToken, Uri uri);
  }

  private GetOAuthRequestTokenListener listener;
  private Twitter twitter;
  private RequestToken requestToken;

  public GetOAuthRequestToken(GetOAuthRequestTokenListener listener, Twitter twitter) {
    this.listener = listener;
    this.twitter = twitter;
  }

  @Override protected Void doInBackground(Void... params) {
    try {
      requestToken = twitter.getOAuthRequestToken(TwitterConfig.TWITTER_CALLBACK_URL);
    } catch (TwitterException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    if (requestToken != null) {
      listener.onReceiveAuthUri(requestToken, Uri.parse(requestToken.getAuthenticationURL()));
    }
  }
}
