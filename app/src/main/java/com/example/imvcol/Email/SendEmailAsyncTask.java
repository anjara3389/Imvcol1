package com.example.imvcol.Email;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.example.imvcol.BuildConfig;
import com.example.imvcol.Utils.DialogUtils;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class SendEmailAsyncTask extends AsyncTask<Void, Void, String> {
    private static Context ctx;
    private static Window window;
    private static String mail;
    private static String clave;
    private DialogUtils dialogUtils;

    public void init(Context context, Window window, String mail, String clave) {
        this.ctx = context;
        this.window = window;
        this.mail = mail;
        this.clave = clave;
        dialogUtils = new DialogUtils(this.ctx, "Enviando email");
        dialogUtils.showDialog(this.window);
    }

    @Override
    protected String doInBackground(Void... params) {
        if (BuildConfig.DEBUG) Log.v(SendEmailAsyncTask.class.getName(), "doInBackground()");
        try {
            GMailSender sender = new GMailSender("anulaciones@colacteos.com", "AnulacioneS*2015");
            sender.sendMail("Su contraseña DMS/INVFISCOL",
                    "Su contraseña es: " + clave,
                    mail,
                    mail);
            return "El mensaje ha sido enviado con éxito";
        } catch (AuthenticationFailedException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Credenciales erroneas");
            e.printStackTrace();
            return "Error: Credenciales erroneas " + e.getMessage();
        } catch (MessagingException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "El envío del mensaje falló");
            e.printStackTrace();
            return "Error: El envío del mensaje falló " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    protected void onPostExecute(String result) {
        if (result != null) {
            Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
        }
        dialogUtils.dissmissDialog();
    }

}