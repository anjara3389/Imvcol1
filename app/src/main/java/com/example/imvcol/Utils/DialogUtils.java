package com.example.imvcol.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

public class DialogUtils {

    private ProgressDialog dialog;

    public DialogUtils(Context ctx, String msj) {
        dialog = new ProgressDialog(ctx);
        dialog.setMessage(msj);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
    }

    public void showDialog(Window window) {
        if (!dialog.isShowing()) {
            dialog.show();
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public void dissmissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
