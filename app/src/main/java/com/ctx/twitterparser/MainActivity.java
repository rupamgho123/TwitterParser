package com.ctx.twitterparser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity
    implements GetOAuthRequestToken.GetOAuthRequestTokenListener,
    UpdateTwitterStatus.UpdateTwitterStatusListener,VerificationTask.VerificationTaskListener,
    GetUserDetails.GetUserDetailsListener,LogoutFromTwitter.LogoutListener,GetNewTweets.GetNewTweetsListener {
  // Login button
  Button btnLoginTwitter;
  // Update status button
  Button btnUpdateStatus;
  // Logout button
  Button btnLogoutTwitter;

  //Get last 100 tweets
  Button btnGetTweets;

  // EditText for update
  EditText txtUpdate;
  // lbl update
  TextView lblUpdate;
  TextView lblUserName;

  // Progress dialog
  ProgressDialog pDialog;

  // Shared Preferences
  private static SharedPreferences mSharedPreferences;

  // Internet Connection detector
  private ConnectionDetector cd;

  private Map<String,Integer> countMap;

  // Alert Dialog Manager
  AlertDialogManager alert = new AlertDialogManager();

  private ProgressDialog progressDialog;
  private static Twitter twitter;
  private static RequestToken requestToken;
  private static final String WORD = "cleartax";

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    cd = new ConnectionDetector(getApplicationContext());
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Loading ...");
    resetTwitter();

    // Check if Internet present
    if (!cd.isConnectingToInternet()) {
      // Internet Connection is not present
      alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
          "Please connect to working Internet connection", false);
      // stop executing code by return
      return;
    }

    // Check if twitter keys are set
    if (TwitterConfig.TWITTER_CONSUMER_KEY.trim().length() == 0
        || TwitterConfig.TWITTER_CONSUMER_SECRET.trim().length() == 0) {
      // Internet Connection is not present
      alert.showAlertDialog(MainActivity.this, "Twitter oAuth tokens",
          "Please set your twitter oauth tokens first!", false);
      // stop executing code by return
      return;
    }

    // All UI elements
    btnLoginTwitter = (Button) findViewById(R.id.btnLoginTwitter);
    btnUpdateStatus = (Button) findViewById(R.id.btnUpdateStatus);
    btnLogoutTwitter = (Button) findViewById(R.id.btnLogoutTwitter);
    txtUpdate = (EditText) findViewById(R.id.txtUpdateStatus);
    lblUpdate = (TextView) findViewById(R.id.lblUpdate);
    lblUserName = (TextView) findViewById(R.id.lblUserName);
    btnGetTweets = (Button) findViewById(R.id.getTweets);

    // Shared Preferences
    mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);

    /**
     * Twitter login button click event will call loginToTwitter() function
     * */
    btnLoginTwitter.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View arg0) {
        // Call login twitter function
        loginToTwitter();
      }
    });

    /**
     * Button click event to Update Status, will call updateTwitterStatus()
     * function
     * */
    btnUpdateStatus.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View v) {
        // Call update status function
        // Get the status from EditText
        String status = txtUpdate.getText().toString();

        // Check for blank text
        if (status.trim().length() > 0) {
          // update status
          new UpdateTwitterStatus(twitter,MainActivity.this).execute(status);
        } else {
          // EditText is empty
          Toast.makeText(getApplicationContext(), "Please enter status message", Toast.LENGTH_SHORT)
              .show();
        }
      }
    });

    /**
     * Button click event for logout from twitter
     * */
    btnLogoutTwitter.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View arg0) {
        // Call logout twitter function
        logoutFromTwitter();
      }
    });

    /** This if conditions is tested once is
     * redirected from twitter page. Parse the uri to get oAuth
     * Verifier
     * */
    if (!isTwitterLoggedInAlready()) {
      Uri uri = getIntent().getData();
      if (uri != null && uri.toString().startsWith(TwitterConfig.TWITTER_CALLBACK_URL)) {
        // oAuth verifier
        String verifier = uri.getQueryParameter(TwitterConfig.URL_TWITTER_OAUTH_VERIFIER);

        new VerificationTask(twitter,requestToken,verifier,this).execute();
      }
    }else{
      postVerfication();
    }

    btnGetTweets.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        progressDialog.show();
       new GetNewTweets(twitter,WORD,MainActivity.this).execute();
      }
    });
  }

  private void resetTwitter() {
    ConfigurationBuilder builder = new ConfigurationBuilder();
    builder.setOAuthConsumerKey(TwitterConfig.TWITTER_CONSUMER_KEY)
        .setOAuthConsumerSecret(TwitterConfig.TWITTER_CONSUMER_SECRET)
        .setOAuthAccessToken(null)
        .setOAuthAccessTokenSecret(null);
    Configuration configuration = builder.build();

    TwitterFactory factory = new TwitterFactory(configuration);
    twitter = factory.getInstance();
  }

  /**
   * Function to login twitter
   */
  private void loginToTwitter() {
    // Check if already logged in
    if (!isTwitterLoggedInAlready()) {
      new GetOAuthRequestToken(this, twitter).execute();
    } else {
      // user already logged into twitter
      Toast.makeText(getApplicationContext(), "Already Logged into twitter", Toast.LENGTH_LONG)
          .show();
    }
  }

  @Override public void onReceiveAuthUri(RequestToken requestToken, Uri uri) {
    MainActivity.requestToken = requestToken;
    this.startActivity(new Intent(Intent.ACTION_VIEW, uri));
  }

  /**
   * Function to logout from twitter
   * It will just clear the application shared preferences
   */
  private void logoutFromTwitter() {
    // Clear the shared preferences
    Editor e = mSharedPreferences.edit();
    e.remove(TwitterConfig.PREF_KEY_OAUTH_TOKEN);
    e.remove(TwitterConfig.PREF_KEY_OAUTH_SECRET);
    e.remove(TwitterConfig.PREF_KEY_TWITTER_LOGIN);
    e.commit();

    // After this take the appropriate action
    // I am showing the hiding/showing buttons again
    // You might not needed this code
    btnLogoutTwitter.setVisibility(View.GONE);
    btnGetTweets.setVisibility(View.GONE);
    btnUpdateStatus.setVisibility(View.GONE);
    txtUpdate.setVisibility(View.GONE);
    lblUpdate.setVisibility(View.GONE);
    lblUserName.setText("");
    lblUserName.setVisibility(View.GONE);

    btnLoginTwitter.setVisibility(View.VISIBLE);

    progressDialog.show();
    new LogoutFromTwitter(twitter,this,this).execute();
  }

  /**
   * Check user already logged in your application using twitter Login flag is
   * fetched from Shared Preferences
   */
  private boolean isTwitterLoggedInAlready() {
    // return twitter login status from Shared Preferences
    return mSharedPreferences.getBoolean(TwitterConfig.PREF_KEY_TWITTER_LOGIN, false);
  }

  protected void onResume() {
    super.onResume();
  }

  @Override public void onPreUpdate() {
    pDialog = new ProgressDialog(MainActivity.this);
    pDialog.setMessage("Updating to twitter...");
    pDialog.setIndeterminate(false);
    pDialog.setCancelable(false);
    pDialog.show();
  }

  @Override public void onPostUpdate() {
    // dismiss the dialog after getting all products
    pDialog.dismiss();
    // updating UI from Background Thread
    runOnUiThread(new Runnable() {
      @Override public void run() {
        Toast.makeText(getApplicationContext(), "Status tweeted successfully", Toast.LENGTH_SHORT)
            .show();
        // Clearing EditText field
        txtUpdate.setText("");
      }
    });
  }

  @Override public String getAccessToken() {
    return mSharedPreferences.getString(TwitterConfig.PREF_KEY_OAUTH_TOKEN, "");
  }

  @Override public String getAccessTokenSecret() {
    return mSharedPreferences.getString(TwitterConfig.PREF_KEY_OAUTH_SECRET, "");
  }

  @Override public void postVerification(AccessToken accessToken) {
    progressDialog.hide();
    Editor e = mSharedPreferences.edit();

    // After getting access token, access token secret
    // store them in application preferences
    e.putString(TwitterConfig.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
    e.putString(TwitterConfig.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
    // Store login status - true
    e.putBoolean(TwitterConfig.PREF_KEY_TWITTER_LOGIN, true);
    e.commit(); // save changes

    Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

    // Getting user details from twitter
    // For now i am getting his name only
    long userID = accessToken.getUserId();

    progressDialog.show();
    new GetUserDetails(twitter,userID,this).execute();
    postVerfication();
  }

  private void postVerfication(){
    // Hide login button
    btnLoginTwitter.setVisibility(View.GONE);

    // Show Update Twitter
    lblUpdate.setVisibility(View.VISIBLE);
    txtUpdate.setVisibility(View.VISIBLE);
    btnUpdateStatus.setVisibility(View.VISIBLE);
    btnLogoutTwitter.setVisibility(View.VISIBLE);
    btnGetTweets.setVisibility(View.VISIBLE);
  }

  @Override public void onFetchUserDetails(User user) {
    progressDialog.hide();
    if(user != null) {
      String username = user.getName();
      lblUserName.setText(Html.fromHtml("<b>Welcome " + username + "</b>"));
    }
  }

  @Override public void postLogout() {
    progressDialog.hide();
    resetTwitter();
  }

  @Override public void onGetNewTweetsFinished(List<Status> tweets) {
    progressDialog.hide();
    List<Status> filteredTweets = new ArrayList<>();
    for(Status status : tweets){
      HashtagEntity[] hashtagEntities = status.getHashtagEntities();
      UserMentionEntity[] userMentionEntities = status.getUserMentionEntities();
      boolean include = true;
      for(UserMentionEntity userMentionEntity : userMentionEntities){
        if(userMentionEntity.getText().toLowerCase().contains(WORD)){
          include = false;
        }
      }
      if(include) {
        for (HashtagEntity hashtagEntity : hashtagEntities) {
          if(hashtagEntity.getText().toLowerCase().contains(WORD)){
            include = false;
          }
        }
      }
      if(include) {
        filteredTweets.add(status);
      }
    }
    Toast.makeText(this,"Total tweets matching: "+filteredTweets.size(),Toast.LENGTH_SHORT).show();
    findTopThreeWords(filteredTweets);
  }

  private void findTopThreeWords(List<Status> filteredTweets){
    int SIZE = 3;
    countMap = new HashMap<>();
    for(Status status : filteredTweets){
      String[] wordsFromSentence = getWordsFromSentence(status.getText());
      for(String word : wordsFromSentence){
        if(countMap.containsKey(word)){
          countMap.put(word,countMap.get(word)+1);
        }else{
          countMap.put(word,Integer.valueOf(1));
        }
      }
    }

    Map<String,Integer> sortedMap = new TreeMap<>(new ValueComparator());
    sortedMap.putAll(countMap);

    Set<String> strings = sortedMap.keySet();
    StringBuilder resultBuilder = new StringBuilder();
    Iterator<String> iterator = strings.iterator();
    int i = 0;
    while(iterator.hasNext() && i < SIZE){
      String word = iterator.next();
      resultBuilder.append("\n"+word + " : "+countMap.get(word));
      i++;
    }
    Toast.makeText(this,"Top count words in search query "+resultBuilder.toString(),Toast.LENGTH_LONG).show();
  }

  private String[] getWordsFromSentence(String s){
    String[] words = s.split("\\s+");
    for (int i = 0; i < words.length; i++) {
      words[i] = words[i].replaceAll("[^\\w]", "").toLowerCase();
    }
    return words;
  }

  class ValueComparator implements Comparator<String>{

    @Override
    public int compare(String s1, String s2) {
      if(countMap.get(s1) >= countMap.get(s2)){
        return -1;
      }else{
        return 1;
      }
    }
  }
}
