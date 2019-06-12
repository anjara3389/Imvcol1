package com.example.imvcol;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.imvcol.Utils.DialogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        try {
            SQLiteDatabase db = BaseHelper.getReadable(this);
            Usuario usuario = new Usuario().selectUsuario(db);

            if (usuario != null
                    && usuario.getnBodegas() != null
                    && usuario.getnGrupos() != null
                    && usuario.getnSubgrupos() != null
                    && usuario.getnSubgrupos2() != null
                    && usuario.getnSubgrupos3() != null
                    && usuario.getnClases() != null
                    && usuario.getUsuario() != null
                    && usuario.getClave() != null
                    && usuario.getnBodegas() + 1 == new Bodega().countBodegas(db)
                    && usuario.getnGrupos() + 1 == new Grupo().count(db)
                    && usuario.getnSubgrupos() + 1 == new Subgrupo().countSubgrupos(db)
                    && usuario.getnSubgrupos2() + 1 == new Subgrupo2().countSubgrupos2(db)
                    && usuario.getnSubgrupos3() + 1 == new Subgrupo3().countSubgrupos3(db)
                    && usuario.getnClases() + 1 == new Clase().countClases(db)) {
                if (!usuario.deleteOldSesion(Inicio.this)) {
                    System.out.println("//////PASA2");
                    Intent i = new Intent(this, FrmContinuarSesion.class);
                    startActivityForResult(i, 1);
                    finish();
                }
            } else {
                System.out.println("//////PASA3");
                Intent i = new Intent(this, FrmLogin.class);
                //i.putExtra("diferencia", true);
                startActivityForResult(i, 1);
                finish();
            }

            BaseHelper.tryClose(db);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
