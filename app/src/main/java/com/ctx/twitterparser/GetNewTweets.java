package com.ctx.twitterparser;

import android.os.AsyncTask;
import android.util.Log;
import java.util.List;
import twitter4j.Status;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by rupam.ghosh on 26/05/16.
 */
public class GetNewTweets extends AsyncTask<Void,Void,Void> {

  Twitter twitter;
  List<twitter4j.Status> tweets;
  String searchString;
  interface GetNewTweetsListener{
    void onGetNewTweetsFinished(List<twitter4j.Status> tweets);
  }
  GetNewTweetsListener listener;

  GetNewTweets(Twitter twitter,String searchString,GetNewTweetsListener listener){
    this.twitter = twitter;
    this.searchString =searchString;
    this.listener =listener;
  }

  @Override protected Void doInBackground(Void... params) {
    try
    {
      Query query = new Query(searchString);
      query.setCount(100);
      QueryResult result = twitter.search(query);

      tweets = result.getTweets();
    }
    catch (TwitterException te)
    {
      Log.d("Twitter","Failed to search tweets: " + te.getMessage());
    }
    return null;
  }

  @Override protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    if(tweets != null)
      listener.onGetNewTweetsFinished(tweets);
  }
}
