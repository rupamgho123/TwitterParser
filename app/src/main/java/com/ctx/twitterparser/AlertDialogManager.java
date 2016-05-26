package com.ctx.twitterparser;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by rupam.ghosh on 25/05/16.
 */
public class AlertDialogManager implements DialogInterface.OnClickListener{
  /**
   * Function to display simple Alert Dialog
   * @param context - application context
   * @param title - alert dialog title
   * @param message - alert message
   * @param status - success/failure (used to set icon)
   *               - pass null if you don't want icon
   * */
  public void showAlertDialog(Context context, String title, String message,
      Boolean status) {
    AlertDialog alertDialog = new AlertDialog.Builder(context).create();

    // Setting Dialog Title
    alertDialog.setTitle(title);

    // Setting Dialog Message
    alertDialog.setMessage(message);

    // Setting OK Button
    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", this);

    // Showing Alert Message
    alertDialog.show();
  }

  @Override public void onClick(DialogInterface dialog, int which) {

  }
}
