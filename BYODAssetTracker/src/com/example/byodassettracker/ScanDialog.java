package com.example.byodassettracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ScanDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.download)
               .setPositiveButton("Download AVG", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   String url = "http://market.android.com/details?id=com.antivirus&referrer=utm_source%3DAVG%26utm_medium%3DCTA%26utm_term%3Ddownload%26utm_campaign%3Dwebsite";
                	   Intent i = new Intent(Intent.ACTION_VIEW);
                	   i.setData(Uri.parse(url));
                	   startActivity(i);
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            	   public void onClick(DialogInterface dialog, int id) {}
               })
               ;
        // Create the AlertDialog object and return it
        return builder.create();
    }
}