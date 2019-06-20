package com.example.imvcol.WebserviceConnection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.Window;

import com.example.imvcol.BaseHelper;
import com.example.imvcol.Usuario;
import com.example.imvcol.Utils.NetUtils;

import java.util.ArrayList;
import java.util.function.Function;

public abstract class WriteLogs {


    public abstract void receiveAfter() throws Exception;

    public void insertLogOnWservice(Context ctx, Window window, String mensaje) throws Exception {
        if (!NetUtils.isOnlineNet(ctx)) {
            throw new Exception("No hay conexión a internet");
        } else {
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    receiveAfter();
                }
            };
            remote.init(ctx, window, "Cargando");

            ArrayList queryDatos = new ArrayList();

            SQLiteDatabase db = BaseHelper.getReadable(ctx);
            Usuario usuario = new Usuario().selectUsuario(db);
            BaseHelper.tryClose(db);

            queryDatos.add(usuario.getQueryInsertLog(mensaje));
            remote.setQuery(queryDatos);
            remote.execute();
        }
    }
}
