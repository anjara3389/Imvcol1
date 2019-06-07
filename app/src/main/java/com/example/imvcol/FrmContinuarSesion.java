package com.example.imvcol;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FrmContinuarSesion extends AppCompatActivity implements YesNoDialogFragment.MyDialogDialogListener {

    private Button btnContinuar;
    private Button btnFinalizarInventario;
    private Usuario usuario;
    private static final int FINALIZAR_INVENTARIO = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_continuar_sesion);

        btnContinuar = findViewById(R.id.frm_continuar_sesion_btn_continuar);
        btnFinalizarInventario = findViewById(R.id.frm_continuar_sesion_btn_finalizar_inventario);
        try {
            final SQLiteDatabase db = BaseHelper.getReadable(FrmContinuarSesion.this);
            usuario = new Usuario().selectUsuario(db);

            btnContinuar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SQLiteDatabase db = BaseHelper.getReadable(FrmContinuarSesion.this);
                        System.out.println("GETN XXXXXXXXXXXXXXXXXX" + usuario.getnProductos());
                        System.out.println("GETN XXXXXXXXXXXXXXXXXX1" + usuario.getnProductos() == null);
                        System.out.println("GETNPRODUCTOSCOUNT" + new Producto().countProductos(db));
                        if (usuario.getCurrBodega() != null
                                && usuario.getnProductos() != null
                                && new Producto().countProductos(db) == usuario.getnProductos()) {
                            //if (usuario.getCurrGrupo() != null && usuario.getCurrSubgr() != null && !usuario.getDatosEnviados()) {
                            if (usuario.getCurrGrupo() != null && !usuario.getDatosEnviados()) {
                                Intent i = new Intent(FrmContinuarSesion.this, FrmInventario.class);
                                startActivityForResult(i, 1);
                                finish();
                            } else {
                                Intent i = new Intent(FrmContinuarSesion.this, FrmOpciones.class);
                                startActivityForResult(i, 1);
                                finish();
                            }
                        } else {
                            Intent i = new Intent(FrmContinuarSesion.this, FrmSelectBodega.class);
                            //i.putExtra("diferencia", true);
                            startActivityForResult(i, 1);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(FrmContinuarSesion.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG);
                    }

                }

            });
            btnFinalizarInventario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SQLiteDatabase db = BaseHelper.getReadable(FrmContinuarSesion.this);
                        if (usuario.getDatosEnviados() || new Inventario().countInventarios(db) == 0) {
                            YesNoDialogFragment dial2 = new YesNoDialogFragment();
                            dial2.setInfo(FrmContinuarSesion.this, FrmContinuarSesion.this, "Finalizar inventario", "¿Está seguro de finalizar el inventario? Los datos que no se hayan enviado se perderán. ", FINALIZAR_INVENTARIO);
                            dial2.show(getSupportFragmentManager(), "MyDialog");
                        } else {
                            throw new Exception("No se puede finalizar el inventario sin enviar los datos existentes.");
                        }
                        db.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(FrmContinuarSesion.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FrmContinuarSesion.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFinishDialog(boolean ans, int code) {
        if (code == FINALIZAR_INVENTARIO && ans) {
            try {
                SQLiteDatabase db = BaseHelper.getWritable(FrmContinuarSesion.this);
                if (new Producto().countProductos(db) > 0 && new Inventario().countInventarios(db) == 0) {
                    throw new Exception("Debe liberar la selección");
                } else {
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
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
