package com.ctx.twitterparser;

import android.os.AsyncTask;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Created by rupam.ghosh on 26/05/16.
 */
public class GetUserDetails extends AsyncTask<Void,Void,Void> {

  User user;
  Twitter twitter;
  GetUserDetailsListener listener;
  long userID;
  public interface GetUserDetailsListener{
    void onFetchUserDetails(User user);
  }
  GetUserDetails(Twitter twitter,long userID,GetUserDetailsListener listener){
    this.twitter = twitter;
    this.userID = userID;
    this.listener = listener;
  }

  @Override protected Void doInBackground(Void... params) {
    try {
      user = twitter.showUser(userID);
    } catch (TwitterException e1) {
      e1.printStackTrace();
    }
    return null;
  }

  @Override protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    if(user != null)
      listener.onFetchUserDetails(user);
  }
}
