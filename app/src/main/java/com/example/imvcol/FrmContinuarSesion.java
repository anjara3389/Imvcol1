package com.example.imvcol;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FrmContinuarSesion extends AppCompatActivity {

    private Button btnContinuar;
    private Button btnCerrarSesion;
    private TextView info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_continuar_sesion);

        btnContinuar = findViewById(R.id.frm_continuar_sesion_btn_continuar);
        btnCerrarSesion = findViewById(R.id.frm_continuar_sesion_btn_cerrar_sesion);
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SQLiteDatabase db = BaseHelper.getReadable(FrmContinuarSesion.this);
                    Usuario usuario = new Usuario().selectUsuario(db);
                    if (usuario.getCurrBodega() != null) {
                        Intent i = new Intent(FrmContinuarSesion.this, FrmInventario.class);
                        //i.putExtra("diferencia", true);
                        startActivityForResult(i, 1);
                        finish();
                    } else {
                        Intent i = new Intent(FrmContinuarSesion.this, FrmOpciones.class);
                        //i.putExtra("diferencia", true);
                        startActivityForResult(i, 1);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FrmContinuarSesion.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FrmContinuarSesion.this, FrmInventario.class);
                //i.putExtra("diferencia", true);
                startActivityForResult(i, 1);

                String pathDatabase = getDatabasePath("imvcol.db").getAbsolutePath();
                System.out.println("DATABSE ////////////////////////" + pathDatabase);

            }
        });
    }
}
