package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.BadParcelableException;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.imvcol.Utils.DialogUtils;
import com.example.imvcol.WebserviceConnection.ExecuteRemoteQuery;
import com.example.imvcol.com.google.zxing.integration.android.IntentIntegrator;
import com.example.imvcol.com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
    private static final int CERRAR_SESION = 4;
    private Usuario usuario;
    private Inventario currentInventario;
    private DialogUtils dialogUtils;
    private Spinner spnFaltantes;
    private HashMap<Integer, String> mapFaltantes;

    private Object[][] wholeFaltantes;
    private ArrayList rawFaltantes;
    private String[] dataSpnFaltantes;
    private ArrayAdapter<String> adapterFaltantes;

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
        spnFaltantes = findViewById(R.id.frm_inventario_spn_faltantes);
        listaProductos = findViewById(R.id.frm_inventario_lst);
        listaProductos.setClickable(true);
        listaProductos.setVisibility(View.GONE);
        disableEnableAfter(false);
        try {
            SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
            usuario = new Usuario().selectUsuario(db);
            prepareFaltantesSpinner();
            BaseHelper.tryClose(db);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FrmInventario.this, "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        rbLectura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbLectura.isChecked()) {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(FrmInventario.this);
                    scanIntegrator.initiateScan();
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

                        String rawCantidad = cantidad.getText().toString().replaceAll("\\+", "+");
                        String[] parts = rawCantidad.replace(" ", "").split("\\+");
                        int total = 0;

                        for (int i = 0; i < parts.length; i++) {
                            total += Integer.parseInt(parts[i]);
                        }

                        if (currentInventario == null) {
                            Inventario inventario = new Inventario(new Date().toString(),
                                    usuario.getCurrBodega(),
                                    selectedProduct[0].toString(),
                                    total,
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
                                updateCantidad(total);
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

    private void prepareFaltantesSpinner() throws Exception {
        final SQLiteDatabase db = BaseHelper.getReadable(this);
        Object[][] falta = new Producto().selectProductsNotOnInventario(db, usuario.getCurrConteo());
        wholeFaltantes = new Object[falta != null ? falta.length + 1 : 0 + 1][2];
        wholeFaltantes[0][0] = "-1";
        wholeFaltantes[0][1] = "Seleccione un producto";

        if (falta != null) {
            for (int i = 0; i < falta.length; i++) {
                for (int j = 0; j < falta[0].length; j++) {
                    wholeFaltantes[i + 1][j] = falta[i][j];
                }
            }
        }

        rawFaltantes = ArrayUtils.mapObjects(wholeFaltantes);

        mapFaltantes = (HashMap<Integer, String>) rawFaltantes.get(1);
        dataSpnFaltantes = (String[]) rawFaltantes.get(0);

        adapterFaltantes = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnFaltantes);
        adapterFaltantes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFaltantes.setAdapter(adapterFaltantes);
        BaseHelper.tryClose(db);

        spnFaltantes.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        try {
                            if (mapFaltantes.get(spnFaltantes.getSelectedItemPosition()) != "-1") {
                                SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
                                selectedProduct = new Producto().selectProductByNumber(db, mapFaltantes.get(spnFaltantes.getSelectedItemPosition()), 0);
                                producto.setText(selectedProduct[1].toString());
                                numero.setText(selectedProduct[0].toString());
                                BaseHelper.tryClose(db);
                                disableEnableCargar(false);
                            } else {
                                //disableEnableCargar(true);
                                selectedProduct = null;
                                producto.setText("");
                                numero.setText("");
                                cantidad.setText("");
                                rbCodigo.setChecked(true);
                                rbLectura.setChecked(false);
                                disableEnableCargar(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(FrmInventario.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_diferencias:
                Intent i = new Intent(this, FrmInventarios.class);
                i.putExtra("diferencia", true);
                startActivityForResult(i, 1);
                break;
            case R.id.action_totales:
                Intent r = new Intent(this, FrmInventarios.class);
                r.putExtra("diferencia", false);
                startActivityForResult(r, 1);
                break;
            case R.id.action_finalizar_conteo:
                YesNoDialogFragment dial = new YesNoDialogFragment();
                dial.setInfo(FrmInventario.this, FrmInventario.this, "Finalizar conteo", "¿Está seguro de finalizar el conteo?", FINALIZAR_CONTEO);
                dial.show(getSupportFragmentManager(), "MyDialog");
                break;
            case R.id.action_finalizar_inventario:
                YesNoDialogFragment dial2 = new YesNoDialogFragment();
                dial2.setInfo(FrmInventario.this, FrmInventario.this, "Finalizar inventario", "¿Está seguro de finalizar el inventario?", FINALIZAR_INVENTARIO);
                dial2.show(getSupportFragmentManager(), "MyDialog");
                break;
            case R.id.action_cerrar_sesion:
                YesNoDialogFragment dia = new YesNoDialogFragment();
                dia.setInfo(FrmInventario.this, FrmInventario.this, "Cerrar sesión", "¿Está seguro de cerrar sesión? Se perderán todos los datos.", CERRAR_SESION);
                dia.show(getSupportFragmentManager(), "MyDialog");
                break;
            default:
                break;
        }
        return true;
    }

    private void limpiar() {
        try {
            selectedProduct = null;
            producto.setText("");
            numero.setText("");
            cantidad.setText("");
            rbCodigo.setChecked(true);
            rbLectura.setChecked(false);
            disableEnableCargar(true);
            prepareFaltantesSpinner();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FrmInventario.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
            if (code == FINALIZAR_CONTEO) {
                try {
                    if (usuario.getCurrConteo() < 3) {
                        SQLiteDatabase db = BaseHelper.getWritable(this);
                        usuario.setCurrConteo(usuario.getCurrConteo() + 1);
                        usuario.updateCurrent(db);
                        db.close();
                        limpiar();
                        Toast.makeText(this, "Conteo acutalizado", Toast.LENGTH_LONG).show();
                    } else {
                        throw new Exception("Se pueden realizar solamente 3 conteos");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG);
                }

            }
            if (code == FINALIZAR_INVENTARIO) {
                try {
                    SQLiteDatabase db = BaseHelper.getWritable(this);
                    new Inventario().insertProductsNotOnInventario(db, usuario.getCurrBodega(), new Date().toString(), usuario.getUsuario());
                    insertResultsOnWebservice();
                    BaseHelper.tryClose(db);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG);
                }
            }
            if (code == CERRAR_SESION) {
                try {
                    SQLiteDatabase db = BaseHelper.getWritable(this);
                    usuario.setCurrBodega(null);
                    usuario.setCurrClase(null);
                    usuario.setCurrConteo(1);
                    usuario.setCurrGrupo(null);
                    usuario.setCurrSubgr(null);
                    usuario.setCurrSubgr2(null);
                    usuario.setCurrSubgr3(null);
                    new Inventario().delete(db);
                    new Producto().delete(db);
                    Intent i = new Intent(FrmInventario.this, FrmOpciones.class);
                    startActivityForResult(i, 1);
                    finish();

                    BaseHelper.tryClose(db);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG);
                }
            }
        }
    }

    public void updateCantidad(int cant) {
        SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
        System.out.println("////CURRENT CONTEO" + usuario.getCurrConteo());


        if (usuario.getCurrConteo() == 1) {
            int cantidadConteo = currentInventario.getConteo1() == null ? 0 : currentInventario.getConteo1();
            currentInventario.setConteo1(cantidadConteo + cant);
            currentInventario.setUsuario1(usuario.getUsuario());
            System.out.println("////currentInventario.getConteo1()" + currentInventario.getConteo1());
        } else if (usuario.getCurrConteo() == 2) {
            int cantidadConteo = currentInventario.getConteo2() == null ? 0 : currentInventario.getConteo2();
            currentInventario.setConteo2(cantidadConteo + cant);
            currentInventario.setUsuario2(usuario.getUsuario());
            System.out.println("////currentInventario.getConteo2()" + currentInventario.getConteo2());
        } else {
            int cantidadConteo = currentInventario.getConteo3() == null ? 0 : currentInventario.getConteo3();
            System.out.println("////currentInventario.getConteo3()" + currentInventario.getConteo3());
            currentInventario.setConteo3(cantidadConteo + cant);
            currentInventario.setUsuario3(usuario.getUsuario());
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

    private void insertResultsOnWebservice() throws Exception {
        SQLiteDatabase db = BaseHelper.getWritable(this);
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                Toast.makeText(FrmInventario.this, "El inventario finalizó exitosamente", Toast.LENGTH_LONG).show();
            }
        };
        remote.setContext(this);

        ArrayList queryDatos = new ArrayList();
        ArrayList<Inventario> inventarios = new Inventario().selectInventarios(db);
        db.close();

        for (int i = 0; i < inventarios.size(); i++) {
            Inventario inventario = inventarios.get(i);

            //System.out.println(DateFormat.getDateInstance().format(date));

            String query = "UPDATE referencias_fis SET " +
                    "toma_1=" + inventario.getConteo1() + ", " +
                    "usu_toma_1=UPPER('" + inventario.getUsuario1() + "'), " +
                    "toma_2=" + inventario.getConteo2() + ", " +
                    "usu_toma_2=UPPER('" + inventario.getUsuario2() + "'), " +
                    "toma_3=" + inventario.getConteo3() + ", " +
                    "usu_toma_3=UPPER('" + inventario.getUsuario3() + "'), " +
                    "fecha_ultima=GETDATE() " +
                    "WHERE codigo='" + inventario.getProducto() + "' " +
                    "AND bodega='" + inventario.getBodega() + "' ";
            queryDatos.add(query);
            System.out.println("QUERYYYYYY///" + query);
        }

        remote.setQuery(queryDatos);
        remote.execute();
        getWebserviceProducts(db);

    }

    private void getWebserviceProducts(final SQLiteDatabase db) throws Exception {
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                ArrayList resultsDatos = (ArrayList) object;
                System.out.println("productos1" + resultsDatos.get(0));
                ArrayList rawProductos = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), null, FrmInventario.this);
                if (resultsDatos.get(0).equals("[]")) {
                    dialogUtils.dissmissDialog();
                    BaseHelper.tryClose(db);
                    Toast.makeText(FrmInventario.this, "No se ha podido finalizar el inventario, intente nuevamente", Toast.LENGTH_LONG).show();
                } else {
                    SQLiteDatabase db = BaseHelper.getWritable(FrmInventario.this);
                    ArrayList<Inventario> inventarios = new Inventario().selectInventarios(db);

                    boolean validar = true;

                    for (int j = 0; j < inventarios.size(); j++) {
                        for (int i = 0; i < rawProductos.size(); i++) {
                            Inventario inventario = inventarios.get(j);
                            JSONObject rawProducto = ((JSONObject) rawProductos.get(i));
                            if (rawProducto.getString("codigo") == inventario.getProducto()) {
                                if (rawProducto.getString("toma_1") != inventario.getConteo1().toString() ||
                                        rawProducto.getString("usu_toma_1") != inventario.getConteo1().toString() ||
                                        rawProducto.getString("toma_2") != inventario.getConteo1().toString() ||
                                        rawProducto.getString("usu_toma_2") != inventario.getConteo1().toString() ||
                                        rawProducto.getString("toma_3") != inventario.getConteo1().toString() ||
                                        rawProducto.getString("fecha_ultima") != inventario.getConteo1().toString()) {
                                    validar = false;
                                }
                            }
                        }
                    }
                    if (validar == true) {
                        new Usuario().delete(db);
                        new Inventario().delete(db);
                        new Bodega().delete(db);
                        new Producto().delete(db);
                        new Grupo().delete(db);
                        new Subgrupo().delete(db);
                        new Subgrupo2().delete(db);
                        new Subgrupo3().delete(db);
                        new Clase().delete(db);
                        //dialogUtils.dissmissDialog();

                        Intent i = new Intent(FrmInventario.this, FrmLogin.class);
                        startActivityForResult(i, 1);
                        BaseHelper.tryClose(db);
                        Toast.makeText(FrmInventario.this, "El inventario ha finalizado correctamente", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(FrmInventario.this, "No se ha podido finalizar el inventario, intente nuevamente", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        remote.setContext(FrmInventario.this);

        ArrayList queryDatos = new ArrayList();

        String query = "SELECT rf.codigo,rf.toma_1,rf.usu_toma_1,rf.toma_2,rf.usu_toma_2,rf.toma_3,rf.usu_toma_3,rf.fecha_ultima " +
                "FROM referencias_fis rf " +
                "JOIN referencias r on r.codigo=rf.codigo " +
                "AND bodega='" + usuario.getCurrBodega() + "' ";
        if (usuario.getCurrGrupo() != null) {
            query += "AND r.grupo='" + usuario.getCurrGrupo() + "' ";
        }
        if (usuario.getCurrSubgr() != null) {
            query += "AND r.subgrupo='" + usuario.getCurrSubgr() + "' ";
        }
        if (usuario.getCurrSubgr2() != null) {
            query += "AND r.subgrupo2='" + usuario.getCurrSubgr2() + "' ";
        }
        if (usuario.getCurrSubgr3() != null) {
            query += "AND r.subgrupo3='" + usuario.getCurrSubgr3() + "' ";
        }
        if (usuario.getCurrClase() != null) {
            query += "AND r.clase='" + usuario.getCurrClase() + "'";
        }

        queryDatos.add(query);
        System.out.println("QUERYYYYYY///" + query);
        remote.setQuery(queryDatos);
        remote.execute();
    }

}
