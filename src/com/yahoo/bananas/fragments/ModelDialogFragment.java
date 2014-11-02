package com.yahoo.bananas.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ModelDialogFragment extends DialogFragment {
	// Static constants
	private static final String BUNDLEKEY_TITLE    = "title";
	private static final String BUNDLEKEY_MESSAGE  = "message";
	private static final String BUNDLEKEY_YESLABEL = "yesLabel";
	private static final String BUNDLEKEY_NOLABEL  = "noLabel";
	// Member Variables
	final private Runnable runnable;
	
	/* NO DEFAULT CTOR
    public ModelDialogFragment() {
    	// Empty constructor required for DialogFragment
    	runnable = null;
    } */

    public ModelDialogFragment(Runnable onYes) {
        // Empty constructor required for DialogFragment
    	runnable = onYes;
    }
    
    /**
     * A quick model dialog to ask anything you want.
     * The user will get a choice, and will run an impl you pass if they say yes, or just close if not.
     * @param onYes - what to do if the user says yes.
     */
    public static ModelDialogFragment newInstance(String title, String message, String yesLabel, String noLabel, Runnable onYes) {
    	ModelDialogFragment frag = new ModelDialogFragment(onYes);
    	Bundle args = new Bundle();
    	args.putString(BUNDLEKEY_TITLE   , title   );
    	args.putString(BUNDLEKEY_MESSAGE , message );
    	args.putString(BUNDLEKEY_YESLABEL, yesLabel);
    	args.putString(BUNDLEKEY_NOLABEL , noLabel );
    	frag.setArguments(args);
    	return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         String title    = getArguments().getString(BUNDLEKEY_TITLE   );
         String message  = getArguments().getString(BUNDLEKEY_MESSAGE );
         String yesLabel = getArguments().getString(BUNDLEKEY_YESLABEL);
         String noLabel  = getArguments().getString(BUNDLEKEY_NOLABEL );
         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
         alertDialogBuilder.setTitle(title);
         alertDialogBuilder.setMessage(message);
         alertDialogBuilder.setPositiveButton(yesLabel,  new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
            	 runnable.run();
             }
         });
         alertDialogBuilder.setNegativeButton(noLabel, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
             }
         });
         
         return alertDialogBuilder.create();
    }
}