package edu.upi.pakarmusik;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * Created by robby on 21/11/17.
 */

public class MyDialogFragment extends DialogFragment {
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tentang");
        builder.setMessage("Programmed by Robby Akbar\n\nCopyright © 2020\nPSTI UPI Purwakarta");
        builder.setPositiveButton("OK", (dialog, id) -> {
        });
        return builder.create();
    }
}
