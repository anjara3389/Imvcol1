package com.example.imvcol;

import android.database.sqlite.SQLiteDatabase;
import android.os.BadParcelableException;
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

import java.util.Date;

public class FrmInventario extends AppCompatActivity {

    private EditText producto, numero, cantidad;
    private Button btnCargar, btnAceptar, btnCancelar;
    private RadioGroup rgOpciones;
    private RadioButton rbCodigo;
    private RadioButton rbLectura;
    private Object[] selectedProduct;

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
                        selectedProduct = new Producto().selectProducto(db, producto.getText().toString(), numero.getText().toString(), code);
                        BaseHelper.tryClose(db);

                        System.out.println("Producto " + selectedProduct[0].toString());
                        System.out.println("Producto " + selectedProduct[1].toString());
                        System.out.println("Producto " + selectedProduct[2].toString());
                        System.out.println("Producto " + selectedProduct[3].toString());

                        producto.setText(selectedProduct[1].toString());
                        numero.setText(selectedProduct[code == 0 ? 0 : 3].toString());

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

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
                    Object[] usu = new Usuario().selectUsuario(db);
                    Object[] inv = new Inventario().selectInventario(db, selectedProduct[0].toString());
                    int newCantidad = Integer.parseInt(cantidad.getText().toString());
                    if (inv == null) {
                        Inventario inventario = new Inventario(new Date(),
                                usu[2].toString(),
                                selectedProduct[0].toString(),
                                newCantidad,
                                usu[0].toString(),
                                null,
                                null,
                                null,
                                null);
                        inventario.insert(db);
                    } else {
                        //CONTINUAR AQUÍ
                        int cantidad = Integer.parseInt(selectedProduct[2].toString());
                        int numConteo = Integer.parseInt(usu[8].toString());
                        Inventario i = new Inventario(numConteo, cantidad, usu[0].toString());
                        i.updateCurrent(db, numConteo, selectedProduct[0].toString());
                    }
                    limpiar();
                    db.close();
                } catch (Exception e) {
                    Toast.makeText(FrmInventario.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiar();
            }
        });
    }

    private void limpiar() {
        selectedProduct = null;
        producto.setText("");
        numero.setText("");
        cantidad.setText("");
        rbCodigo.setChecked(true);
        rbLectura.setChecked(false);
        disableEnableCargar(true);
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
