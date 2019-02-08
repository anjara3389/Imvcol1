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
    private Button btnFinalizarInventario;
    private TextView info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_continuar_sesion);

        btnContinuar = findViewById(R.id.frm_continuar_sesion_btn_continuar);
        btnFinalizarInventario = findViewById(R.id.frm_continuar_sesion_btn_finalizar_inventario);
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
        btnFinalizarInventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SQLiteDatabase db = BaseHelper.getWritable(FrmContinuarSesion.this);
                    new Usuario().delete(db);
                    new Inventario().delete(db);
                    new Bodega().delete(db);
                    new Producto().delete(db);
                    new Grupo().delete(db);
                    new Subgrupo().delete(db);
                    new Subgrupo2().delete(db);
                    new Subgrupo3().delete(db);
                    new Clase().delete(db);
                    BaseHelper.tryClose(db);
                    //dialogUtils.dissmissDialog();

                    Intent i = new Intent(FrmContinuarSesion.this, FrmLogin.class);
                    startActivityForResult(i, 1);
                    BaseHelper.tryClose(db);
                    Toast.makeText(FrmContinuarSesion.this, "El inventario finalizó exitosamente", Toast.LENGTH_LONG).show();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FrmContinuarSesion.this, "Error: " + e, Toast.LENGTH_LONG);
                }

            }
        });
    }
}
