package org.rhok.microdonaciones;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DonationInputDialog extends DialogFragment {

	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DonationInputDialogListener {
        public void onDonationSelection(DialogFragment dialog, int selected);
    }
    
    // Use this instance of the interface to deliver action events
 	DonationInputDialogListener mListener;
    
 	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DonationInputDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
 	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecciona la cantidad a donar")
        	.setItems(R.array.donations, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {
        				mListener.onDonationSelection(DonationInputDialog.this, which);
	             }
               });
        return builder.create();
    }
}
