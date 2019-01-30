package com.example.imvcol;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class YesNoDialogFragment extends DialogFragment {
    private Context mContext;
    private AlertDialog.Builder alertDialogBuilder;
    private boolean answer;
    private String title;
    private String message;
    private int code;
    private MyDialogDialogListener listener;

    public interface MyDialogDialogListener {
        void onFinishDialog(boolean ans, int code);
    }

    public YesNoDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                answer = true;
                sendBackResult();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return alertDialogBuilder.create();
    }


    public void setInfo(final MyDialogDialogListener listener, Context cont, String title, String message, int code) {
        this.mContext = cont;
        this.title = title;
        this.message = message;
        this.listener = listener;
        this.code = code;
    }

    public void sendBackResult() {
        this.listener.onFinishDialog(answer, code);
        dismiss();
    }
}


