package com.ctx.twitterparser;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import twitter4j.Twitter;

/**
 * Created by rupam.ghosh on 26/05/16.
 */
public class LogoutFromTwitter extends AsyncTask<Void,Void,Void> {
  Twitter twitter;
  Context context;
  interface LogoutListener{
    void postLogout();
  }
  LogoutListener listener;
  LogoutFromTwitter(Twitter twitter,Context context,LogoutListener listener){
      this.twitter = twitter;
      this.context = context;
      this.listener = listener;
  }
  @Override protected Void doInBackground(Void... params) {
    CookieSyncManager.createInstance(context);
    CookieManager cookieManager = CookieManager.getInstance();
    cookieManager.removeSessionCookie();

    //try {
    //  twitter.invalidateOAuth2Token();
    //} catch (TwitterException e) {
    //  e.printStackTrace();
    //}
    return null;
  }

  @Override protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    listener.postLogout();
  }
}
