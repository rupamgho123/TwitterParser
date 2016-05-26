package com.ctx.twitterparser;

import android.os.AsyncTask;
import android.util.Log;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by rupam.ghosh on 25/05/16.
 */
class UpdateTwitterStatus extends AsyncTask<String, String, String> {

  public interface UpdateTwitterStatusListener {
    void onPreUpdate();

    void onPostUpdate();

    String getAccessToken();

    String getAccessTokenSecret();
  }

  UpdateTwitterStatusListener listener;
  Twitter twitter;

  UpdateTwitterStatus(Twitter twitter,UpdateTwitterStatusListener listener) {
    this.listener = listener;
    this.twitter = twitter;
  }

  @Override protected void onPreExecute() {
    super.onPreExecute();
    listener.onPreUpdate();
  }

  /**
   * getting Places JSON
   */
  protected String doInBackground(String... args) {
    Log.d("Tweet Text", "> " + args[0]);
    String status = args[0];
    try {

      twitter4j.Status response = twitter.updateStatus(status);

      Log.d("Status", "> " + response.getText());
    } catch (TwitterException e) {
      // Error in updating status
      Log.d("Twitter Update Error", e.getMessage());
    }
    return null;
  }

  /**
   * After completing background task Dismiss the progress dialog and show
   * the data in UI Always use runOnUiThread(new Runnable()) to update UI
   * from background thread, otherwise you will get error
   **/
  protected void onPostExecute(String file_url) {
    listener.onPostUpdate();
  }
}