package com.example.imvcol;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        try {
            SQLiteDatabase db = BaseHelper.getReadable(this);
            Usuario usuario = new Usuario().selectUsuario(db);
            /*if (usuario != null) {
                System.out.println("XXXXXXXXXXX BODEGA1 " + usuario.getnBodegas());
                System.out.println("XXXXXXXXXXX BODEGA2 " + new Bodega().countBodegas(db));
                System.out.println("XXXXXXXXXXX GRUPOS1 " + usuario.getnGrupos());
                System.out.println("XXXXXXXXXXX GRUPOS2 " + new Grupo().count(db));
                System.out.println("XXXXXXXXXXX SUBGRUPOS1 " + usuario.getnSubgrupos());
                System.out.println("XXXXXXXXXXX SUBGRUPOS2 " + new Subgrupo().countSubgrupos(db));
                System.out.println("XXXXXXXXXXX SUBGRUPOS21 " + usuario.getnSubgrupos2());
                System.out.println("XXXXXXXXXXX SUBGRUPOS22" + new Subgrupo2().countSubgrupos2(db));
                System.out.println("XXXXXXXXXXX SUBGRUPOS31" + usuario.getnSubgrupos3());
                System.out.println("XXXXXXXXXXX SUBGRUPOS32" + new Subgrupo3().countSubgrupos3(db));
                System.out.println("XXXXXXXXXXX CLASES1" + usuario.getnClases());
                System.out.println("XXXXXXXXXXX CLASES2" + new Clase().countClases(db));
            }*/
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
                Intent i = new Intent(this, FrmContinuarSesion.class);
                startActivityForResult(i, 1);
                finish();
            } else {
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
