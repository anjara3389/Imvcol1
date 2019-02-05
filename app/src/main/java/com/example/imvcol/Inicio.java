package com.example.imvcol;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        try {
            SQLiteDatabase db = BaseHelper.getReadable(this);
            Usuario usuario = new Usuario().selectUsuario(db);
            if (usuario != null) {
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
