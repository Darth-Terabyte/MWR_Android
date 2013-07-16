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
                	   String url = "http://www.avg.com/antivirus-for-android";
                	   Intent i = new Intent(Intent.ACTION_VIEW);
                	   i.setData(Uri.parse(url));
                	   startActivity(i);
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}