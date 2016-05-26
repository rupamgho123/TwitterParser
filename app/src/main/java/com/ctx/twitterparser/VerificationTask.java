package com.ctx.twitterparser;

import android.os.AsyncTask;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by rupam.ghosh on 26/05/16.
 */
public class VerificationTask extends AsyncTask<Void,Void,Void> {

  public interface VerificationTaskListener{
    void postVerification(AccessToken accessToken);
  }
  private Twitter twitter;
  private RequestToken requestToken;
  private String verifier;
  private AccessToken accessToken;
  private VerificationTaskListener listener;

  public VerificationTask(Twitter twitter,RequestToken requestToken,String verifier,VerificationTaskListener listener){
    this.twitter = twitter;
    this.requestToken = requestToken;
    this.verifier = verifier;
    this.listener = listener;
  }

  @Override protected Void doInBackground(Void... params) {
    try {
      accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
    } catch (TwitterException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    if(accessToken != null)
      listener.postVerification(accessToken);
  }
}
