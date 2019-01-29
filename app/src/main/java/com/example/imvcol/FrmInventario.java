package com.example.imvcol;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.imvcol.Utils.DialogUtils;

import org.json.JSONObject;

public class FrmInventario extends AppCompatActivity {

    private EditText producto, numero, cantidad;
    private Button btnCargar, btnAceptar, btnCancelar;
    private RadioGroup rgOpciones;
    private RadioButton rbCodigo;
    private RadioButton rbLectura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_inventario);

        producto = findViewById(R.id.frm_inventario_txt_producto);
        numero = findViewById(R.id.frm_inventario_txt_numero);
        cantidad = findViewById(R.id.frm_inventario_txt_cantidad);
        btnCargar = findViewById(R.id.frm_inventario_btn_cargar);
        btnAceptar = findViewById(R.id.frm_inventario_btn_aceptar);
        btnCancelar = findViewById(R.id.frm_inventario_btn_cancelar);
        rgOpciones = findViewById(R.id.frm_inventario_rbgroup_opciones);
        rbCodigo = findViewById(R.id.frm_inventario_rbtn_codigo);
        rbLectura = findViewById(R.id.frm_inventario_rbtn_lectura);
        disableEnableAfter(false);

        btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int code = rbCodigo.isChecked() ? 0 : 1;
                    if (producto.getText() != null || numero.getText() != null) {
                        SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
                        Object[] product = new Producto().selectProducto(db, producto.getText().toString(), numero.getText().toString(), code);

                        System.out.println("Producto " + product[0].toString());
                        System.out.println("Producto " + product[1].toString());
                        System.out.println("Producto " + product[2].toString());
                        System.out.println("Producto " + product[3].toString());

                        producto.setText(product[1].toString());
                        numero.setText(product[code == 0 ? 0 : 3].toString());

                        disableEnableCargar(false);

                    } else {
                        Toast.makeText(FrmInventario.this, "Introduzca un valor", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(FrmInventario.this, "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });
    }


    private void disableEnableCargar(boolean enable) {
        producto.setFocusable(enable);
        producto.setFocusableInTouchMode(enable);
        producto.setClickable(enable);

        numero.setFocusable(enable);
        numero.setFocusableInTouchMode(enable);
        numero.setClickable(enable);

        rbCodigo.setFocusable(enable);
        rbCodigo.setFocusableInTouchMode(enable);
        rbCodigo.setClickable(enable);

        rbLectura.setFocusable(enable);
        rbLectura.setFocusableInTouchMode(enable);
        rbLectura.setClickable(enable);

        btnCargar.setEnabled(enable);
        btnCargar.setFocusable(enable);
        btnCargar.setFocusableInTouchMode(enable);
        btnCargar.setClickable(enable);

        disableEnableAfter(!enable);

        //producto.setEnabled(false);
        //numero.setEnabled(false);
        //rbCodigo.setEnabled(false);
        //rbLectura.setEnabled(false);

    }

    private void disableEnableAfter(boolean enable) {
        cantidad.setEnabled(enable);
        btnAceptar.setEnabled(enable);

        cantidad.setFocusable(enable);
        cantidad.setFocusableInTouchMode(enable);
        cantidad.setClickable(enable);

        btnAceptar.setFocusable(enable);
        btnAceptar.setFocusableInTouchMode(enable);
        btnAceptar.setClickable(enable);
    }
}
