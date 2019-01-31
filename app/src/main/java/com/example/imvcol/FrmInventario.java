package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.BadParcelableException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.imvcol.Utils.DialogUtils;
import com.example.imvcol.WebserviceConnection.ExecuteRemoteQuery;
import com.example.imvcol.com.google.zxing.integration.android.IntentIntegrator;
import com.example.imvcol.com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class FrmInventario extends AppCompatActivity implements YesNoDialogFragment.MyDialogDialogListener {

    private EditText producto, numero, cantidad;
    private Button btnCargar, btnAceptar, btnCancelar;
    private RadioGroup rgOpciones;
    private RadioButton rbCodigo;
    private RadioButton rbLectura;
    private Object[] selectedProduct;
    private Object[][] productos;
    private ListView listaProductos;
    private static final int SUMAR_CANTIDAD = 1;
    private static final int FINALIZAR_CONTEO = 2;
    private static final int FINALIZAR_INVENTARIO = 3;
    private Usuario usuario;
    private Inventario currentInventario;
    DialogUtils dialogUtils;

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
        listaProductos = findViewById(R.id.frm_inventario_lst);
        listaProductos.setClickable(true);
        listaProductos.setVisibility(View.GONE);
        disableEnableAfter(false);


        try {
            SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
            usuario = new Usuario().selectUsuario(db);
            BaseHelper.tryClose(db);
        } catch (Exception e) {
            e.printStackTrace();
        }

        rbLectura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Thread.sleep(2000);
                    if (rbLectura.isChecked()) {
                        IntentIntegrator scanIntegrator = new IntentIntegrator(FrmInventario.this);
                        scanIntegrator.initiateScan();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(FrmInventario.this, "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });


        btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (producto.getText().toString() != "" && producto.getText().length() != 0) {
                        SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
                        productos = new Producto().selectProductsByDescripcion(db, producto.getText().toString());
                        if (productos.length == 0) {
                            throw new Exception("No se encuentran coincidencias");
                        } else {
                            final ArrayList<String> nombres = new ArrayList();

                            for (int i = 0; i < productos.length; i++) {
                                nombres.add(productos[i][0].toString() + " - " + productos[i][1].toString());
                            }
                            BaseHelper.tryClose(db);

                            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(FrmInventario.this, android.R.layout.simple_list_item_1, nombres);
                            listaProductos.setAdapter(itemsAdapter);
                            listaProductos.setVisibility(View.VISIBLE);
                            btnAceptar.setVisibility(View.GONE);
                            btnCancelar.setVisibility(View.GONE);
                            btnCargar.setVisibility(View.GONE);
                        }

                    } else if (numero.toString() != "" && numero.getText().length() != 0) {
                        int code = rbCodigo.isChecked() ? 0 : 1;
                        SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
                        selectedProduct = new Producto().selectProductByNumber(db, numero.getText().toString(), code);
                        BaseHelper.tryClose(db);
                        //System.out.println("Producto " + selectedProduct[0].toString());
                        //System.out.println("Producto " + selectedProduct[1].toString());
                        //System.out.println("Producto " + selectedProduct[2].toString());
                        //System.out.println("Producto " + selectedProduct[3].toString());

                        producto.setText(selectedProduct[1].toString());
                        numero.setText(selectedProduct[code == 0 ? 0 : 3].toString());

                        disableEnableCargar(false);
                    } else {
                        throw new Exception("Introduzca un valor");
                    }
                } catch (Exception e) {
                    Toast.makeText(FrmInventario.this, "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        listaProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (productos != null) {
                    int code = rbCodigo.isChecked() ? 0 : 1;
                    selectedProduct = productos[position];
                    listaProductos.setVisibility(View.GONE);
                    btnAceptar.setVisibility(View.VISIBLE);
                    btnCancelar.setVisibility(View.VISIBLE);
                    btnCargar.setVisibility(View.VISIBLE);

                    producto.setText(selectedProduct[1].toString());
                    numero.setText(selectedProduct[code == 0 ? 0 : 3].toString());

                    disableEnableCargar(false);
                }
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (cantidad.getText().toString() == "") {
                        throw new Exception("Introducir una cantidad");
                    } else {
                        SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
                        currentInventario = new Inventario().selectInventario(db, selectedProduct[0].toString());
                        int newCantidad = Integer.parseInt(cantidad.getText().toString());
                        if (currentInventario == null) {
                            Inventario inventario = new Inventario(new Date().toString(),
                                    usuario.getCurrBodega(),
                                    selectedProduct[0].toString(),
                                    newCantidad,
                                    usuario.getUsuario(),
                                    null,
                                    null,
                                    null,
                                    null);
                            inventario.insert(db);
                            Toast.makeText(FrmInventario.this, "Registro insertado", Toast.LENGTH_LONG).show();
                            limpiar();
                        } else {
                            if ((usuario.getCurrConteo() == 1 && currentInventario.getConteo1() != null) ||
                                    (usuario.getCurrConteo() == 2 && currentInventario.getConteo2() != null) ||
                                    (usuario.getCurrConteo() == 3 && currentInventario.getConteo3() != null)) {
                                YesNoDialogFragment dial = new YesNoDialogFragment();
                                dial.setInfo(FrmInventario.this, FrmInventario.this, "Sumar cantidad", "Producto contabilizado anteriormente, ¿Desea sumar la cantidad introducida?", SUMAR_CANTIDAD);
                                dial.show(getSupportFragmentManager(), "MyDialog");
                            } else {
                                updateCantidad(0);
                                limpiar();
                                Toast.makeText(FrmInventario.this, "Registro insertado", Toast.LENGTH_LONG).show();
                            }
                        }
                        db.close();
                    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        //menu.findItem(R.id.)
        setTitle("INVCOL");
        return true;
    }

    private void cambiarConteo() throws Exception {
        YesNoDialogFragment dial = new YesNoDialogFragment();
        dial.setInfo(FrmInventario.this, FrmInventario.this, "Finalizar inventario", "¿Está seguro de finalizar el inventario?", FINALIZAR_CONTEO);
        dial.show(getSupportFragmentManager(), "MyDialog");
        if (usuario.getCurrConteo() != 3) {
            SQLiteDatabase db = BaseHelper.getWritable(this);
            usuario.setCurrConteo(usuario.getCurrConteo() + 1);
            usuario.updateCurrent(db);
            db.close();
            Toast.makeText(this, "Conteo acutalizado", Toast.LENGTH_LONG).show();
        } else {
            throw new Exception("Se pueden realizar solamente 3 conteos");
        }
    }

    private void finalizarInventario() throws Exception {
        YesNoDialogFragment dial = new YesNoDialogFragment();
        dial.setInfo(FrmInventario.this, FrmInventario.this, "Finalizar inventario", "¿Está seguro de finalizar el inventario?", FINALIZAR_INVENTARIO);
        dial.show(getSupportFragmentManager(), "MyDialog");
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

    @Override
    public void onFinishDialog(boolean ans, int code) {
        if (ans == true) {
            if (code == SUMAR_CANTIDAD) {
                updateCantidad(Integer.parseInt(cantidad.getText().toString()));
            }
        }
    }

    public void updateCantidad(int cant) {
        SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());

        if (usuario.getCurrConteo() == 1) {
            currentInventario.setConteo1(currentInventario.getConteo1() + cant);
        } else if (usuario.getCurrConteo() == 2) {
            currentInventario.setConteo2(currentInventario.getConteo2() + cant);
        } else {
            currentInventario.setConteo3(currentInventario.getConteo3() + cant);
        }

        currentInventario.updateCurrent(db, usuario.getCurrConteo(), selectedProduct[0].toString());
        Toast.makeText(FrmInventario.this, "Registro actualizado", Toast.LENGTH_LONG).show();
        BaseHelper.tryClose(db);
        limpiar();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {

            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            numero.setText(scanContent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void insertResultsOnWebservice(final View v, final SQLiteDatabase db) throws Exception {
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                Toast.makeText(FrmInventario.this, "El inventario finalizó exitosamente", Toast.LENGTH_LONG).show();
                /*ArrayList resultsDatos = (ArrayList) object;
                System.out.println("productos1" + resultsDatos.get(0));
                ArrayList rawProductos = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), null, FrmOpciones.this);
                if (resultsDatos.get(0).equals("[]")) {
                    dialogUtils.dissmissDialog();
                    BaseHelper.tryClose(db);
                    Toast.makeText(v.getContext(), "No se han podido cargar los datos, intente nuevamente", Toast.LENGTH_LONG).show();
                } else {
                    fillDatabase(rawProductos);
                    dialogUtils.dissmissDialog();
                    Intent i = new Intent(v.getContext(), FrmInventario.class);
                    //i.putExtra("datos", resultsDatos);
                    startActivityForResult(i, 1);
                }*/
            }
        };
        remote.setContext(v.getContext());

        ArrayList queryDatos = new ArrayList();
        ArrayList<Inventario> inventarios = new Inventario().selectInventarios(db);

        for (int i = 0; i < inventarios.size(); i++) {
            Inventario inventario = inventarios.get(i);
            String query = "UPDATE referencias_fis SET " +
                    "toma_1=" + inventario.getConteo1() + " " +
                    "usu_toma_1=" + inventario.getUsuario1() + " " +
                    "toma_2=" + inventario.getConteo2() + " " +
                    "usu_toma_2=" + inventario.getUsuario1() + " " +
                    "toma_3=" + inventario.getConteo3() + "" +
                    "usu_toma_3=" + inventario.getUsuario3() + " " +
                    "fecha_ultima=" + inventario.getFecha() + " " +
                    "WHERE codigo=" + inventario.getProducto() + " " +
                    "AND bodega=" + inventario.getBodega() + " ";
            queryDatos.add(query);
            System.out.println("QUERYYYYYY///" + query);
        }

        remote.setQuery(queryDatos);
        remote.execute();
    }
}
