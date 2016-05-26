package com.ctx.twitterparser;

/**
 * Created by rupam.ghosh on 25/05/16.
 */
public interface TwitterConfig {
  String TWITTER_CONSUMER_KEY = "cqBkWlwznPayS15DOK8R7NLJ7";
  String TWITTER_CONSUMER_SECRET = "Fv9VdaAk1zwIdf5e4QH8ghv27miYsxd8EiOZjNaYJgb47pvETX";
  String TWITTER_CALLBACK_URL = "http://www.twitterparser.com/getData";
  String PREFERENCE_NAME = "twitter_oauth";
  String PREF_KEY_OAUTH_TOKEN = "oauth_token";
  String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
  String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
  String URL_TWITTER_AUTH = "auth_url";
  String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
  String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
}
