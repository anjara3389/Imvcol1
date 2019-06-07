package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imvcol.Utils.DialogUtils;
import com.example.imvcol.Utils.NetUtils;
import com.example.imvcol.WebserviceConnection.ExecuteRemoteQuery;
import com.example.imvcol.com.google.zxing.integration.android.IntentIntegrator;
import com.example.imvcol.com.google.zxing.integration.android.IntentResult;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.bouncycastle.asn1.esf.ESFAttributes;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FrmInventario extends AppCompatActivity implements YesNoDialogFragment.MyDialogDialogListener {

    private EditText producto, numero, cantidad;
    private TextView info;
    private Button btnCargar, btnAceptar, btnCancelar;
    private RadioButton rbCodigo;
    private RadioButton rbLectura;
    private Object[] selectedProduct;
    private Object[][] productos;
    private ListView listaProductos;
    private static final int SUMAR_CANTIDAD = 1;
    private static final int CAMBIAR_CONTEO = 2;
    private static final int FINALIZAR_INVENTARIO = 3;
    private static final int LIBERAR_SELECCION = 4;
    private static final int ENVIAR_DATOS = 5;
    private static final int LIBERAR_SELECCION_CONTRASENIA = 6;
    private Usuario usuario;
    private Inventario currentInventario;
    private DialogUtils dialogUtils;
    private Spinner spnFaltantes;
    private HashMap<Integer, String> mapFaltantes;
    private CardView card;

    private Object[][] wholeFaltantes;
    private ArrayList rawFaltantes;
    private String[] dataSpnFaltantes;
    private ArrayAdapter<String> adapterFaltantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_inventario);
        try {
            producto = findViewById(R.id.frm_inventario_txt_producto);
            numero = findViewById(R.id.frm_inventario_txt_numero);
            btnCargar = findViewById(R.id.frm_inventario_btn_cargar);
            btnAceptar = findViewById(R.id.frm_inventario_btn_aceptar);
            btnCancelar = findViewById(R.id.frm_inventario_btn_cancelar);
            rbCodigo = findViewById(R.id.frm_inventario_rbtn_codigo);
            rbLectura = findViewById(R.id.frm_inventario_rbtn_lectura);
            spnFaltantes = findViewById(R.id.frm_inventario_spn_faltantes);
            card = findViewById(R.id.frm_inventario_card);
            info = findViewById(R.id.frm_inventario_lbl_info);


            listaProductos = findViewById(R.id.frm_inventario_lst);
            listaProductos.setClickable(true);
            listaProductos.setVisibility(View.GONE);

            cantidad = findViewById(R.id.frm_inventario_txt_cantidad);
            cantidad.setRawInputType(Configuration.KEYBOARD_12KEY);
            cantidad.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == 5) {
                        btnAceptar.performClick();
                    }
                    return false;
                }
            });
            producto.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    System.out.println("EVEEEEENT" + keyCode);
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        btnCargar.performClick();
                        return false;
                    }
                    return false;
                }
            });

            producto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    System.out.println("EVEEEEENT" + event);
                    System.out.println("ACTIIIIION" + actionId);
                    if (actionId == 5) {
                        btnCargar.performClick();
                    }
                    return false;
                }
            });
            numero.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == 5) {
                        btnCargar.performClick();
                    }
                    return false;
                }
            });


            disableEnableCantidad(false);
            SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
            usuario = new Usuario().selectUsuario(db);

            info.setText("Bodega: " + usuario.getCurrBodega() + "   Grupo: " + usuario.getCurrGrupo() + "   Subgrupo: " + usuario.getCurrSubgr() +
                    "   Subgrupo2: " + usuario.getCurrSubgr2() + "   Subgrupo3: " + usuario.getCurrSubgr3() + "   Clase: " + usuario.getCurrClase() +
                    "   Ubicación: " + usuario.getCurrUbicacion());

            if (usuario.getModo() == usuario.MODO_BARRAS) {
                spnFaltantes.setVisibility(View.GONE);
                findViewById(R.id.frm_inventario_lbl_faltantes).setVisibility(View.GONE);
            } else {
                prepareFaltantesSpinner();
            }

            BaseHelper.tryClose(db);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FrmInventario.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                        productos = new Producto().selectProductsByDescripcion(db,
                                producto.getText().toString(),
                                usuario.getCurrGrupo(),
                                usuario.getCurrSubgr(),
                                usuario.getCurrSubgr2(),
                                usuario.getCurrSubgr3(),
                                usuario.getCurrClase(),
                                usuario.getCurrUbicacion());
                        if (productos == null || productos.length == 0) {
                            throw new Exception("El producto no exíste dentro del grupo seleccionado");
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
                            card.setVisibility(View.GONE);
                        }

                    } else if (numero.toString() != "" && numero.getText().length() != 0) {
                        int code = rbCodigo.isChecked() ? 0 : 1;
                        selectProductoWithNumber(code);
                        if (spnFaltantes != null && mapFaltantes != null) {
                            spnFaltantes.setEnabled(mapFaltantes.get(spnFaltantes.getSelectedItemPosition()) != "-1");
                        }
                    } else {
                        throw new Exception("Introduzca un valor");
                    }
                } catch (Exception e) {
                    Toast.makeText(FrmInventario.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                    card.setVisibility(View.VISIBLE);

                    producto.setText(selectedProduct[1].toString());
                    numero.setText(selectedProduct[code == 0 ? 0 : 3].toString());

                    disableEnableCargar(false);
                    if (spnFaltantes != null && mapFaltantes != null) {
                        spnFaltantes.setEnabled(mapFaltantes.get(spnFaltantes.getSelectedItemPosition()) != "-1");
                    }
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
                        //cantidad.getText().toString()
                        Double total = operarEnLaCantidad(db);

                        if (total != null) {
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

    private Double operarEnLaCantidad(SQLiteDatabase db) {
        try {
            Cursor c = db.rawQuery("SELECT " + cantidad.getText().toString(), null);
            if (c.moveToFirst()) {
                return c.getDouble(0);
            }
        } catch (Exception e) {
            Toast.makeText(FrmInventario.this, "Error: Hay un error en la operación introducida en cantidad.", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private void selectProductoWithNumber(int code) throws Exception {
        SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
        selectedProduct = new Producto().selectProductByNumber(db,
                numero.getText().toString(),
                code,
                usuario.getCurrGrupo(),
                usuario.getCurrSubgr(),
                usuario.getCurrSubgr2(),
                usuario.getCurrSubgr3(),
                usuario.getCurrClase(),
                usuario.getCurrUbicacion());
        BaseHelper.tryClose(db);
        if (selectedProduct == null) {
            throw new Exception("El producto no exíste dentro del grupo seleccionado");
        } else {
            producto.setText(selectedProduct[1].toString());
            numero.setText(selectedProduct[code == 0 ? 0 : 3].toString());

            disableEnableCargar(false);
        }
    }

    private void prepareFaltantesSpinner() throws Exception {
        final SQLiteDatabase db = BaseHelper.getReadable(this);
        wholeFaltantes = new Producto().selectProductsNotOnInventario(db,
                usuario.getCurrConteo(),
                usuario.getCurrGrupo(),
                usuario.getCurrSubgr(),
                usuario.getCurrSubgr2(),
                usuario.getCurrSubgr3(),
                usuario.getCurrClase(),
                usuario.getCurrUbicacion());
        if (wholeFaltantes == null) {
            wholeFaltantes = new Object[1][2];
            wholeFaltantes[0][0] = "-1";
            wholeFaltantes[0][1] = "No hay productos";
        }
        //Object[][] falta = new Producto().selectProductsNotOnInventario(db, usuario.getCurrConteo(), usuario.getCurrGrupo(), usuario.getCurrSubgr(), usuario.getCurrSubgr2(), usuario.getCurrSubgr3(), usuario.getCurrClase());
        // wholeFaltantes = new Object[falta != null ? falta.length : 0][2];
        //wholeFaltantes[0][0] = "-1";
        //wholeFaltantes[0][1] = "Seleccione un producto";
/*
        if (falta != null) {
            for (int i = 0; i < falta.length; i++) {
                for (int j = 0; j < falta[0].length; j++) {
                    wholeFaltantes[i][j] = falta[i][j];
                }
            }
        }*/

        rawFaltantes = ArrayUtils.mapObjects(wholeFaltantes);

        mapFaltantes = (HashMap<Integer, String>) rawFaltantes.get(1);
        dataSpnFaltantes = (String[]) rawFaltantes.get(0);

        adapterFaltantes = new ArrayAdapter(getApplicationContext(), R.layout.spinner_item, R.id.spinner_item_text, dataSpnFaltantes) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) v.findViewById(R.id.spinner_item_text)).setSingleLine(false);
                    }
                });
                return v;
            }
        };
        ;
        //adapterFaltantes = ArrayAdapter.createFromResource(getApplicationContext(),dataSpnFaltantes,R.layout.spinner_item);

        adapterFaltantes.setDropDownViewResource(R.layout.spinner_item);
        spnFaltantes.setAdapter(adapterFaltantes);
        BaseHelper.tryClose(db);

        spnFaltantes.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        try {
                            if (mapFaltantes.get(spnFaltantes.getSelectedItemPosition()) != "-1") {
                                SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
                                selectedProduct = new Producto().selectProductByNumber(db,
                                        mapFaltantes.get(spnFaltantes.getSelectedItemPosition()),
                                        0,
                                        usuario.getCurrGrupo(),
                                        usuario.getCurrSubgr(),
                                        usuario.getCurrSubgr2(),
                                        usuario.getCurrSubgr3(),
                                        usuario.getCurrClase(),
                                        usuario.getCurrUbicacion());
                                //producto.setText(selectedProduct[1].toString());
                                //numero.setText(selectedProduct[0].toString());
                                BaseHelper.tryClose(db);
                                disableEnableCantidad(true);


                                // disableEnableCargar(false);
                            } else {
                                //disableEnableCargar(true);
                                selectedProduct = null;
                                //producto.setText("");
                                //numero.setText("");
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
        menu.findItem(R.id.action_habilitar_bodegas).setVisible(false);
        //menu.findItem(R.id.)
        setTitle("INVFISCOL 3.3");
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
                try {
                    if (usuario.getCurrConteo() != 3) {
                        SQLiteDatabase db = BaseHelper.getWritable(this);
                        Object[][] inventarios = new Inventario().selectInventariosTotales(db, true, usuario.getCurrGrupo(),
                                usuario.getCurrSubgr(), usuario.getCurrSubgr2(), usuario.getCurrSubgr3(), usuario.getCurrClase(),
                                usuario.getCurrUbicacion());
                        boolean answ = true;
                        for (int f = 0; f < inventarios.length; f++) {
                            if ((usuario.getCurrConteo() == 1 && inventarios[f][3] == null) || (usuario.getCurrConteo() == 2 && inventarios[f][4] == null)) {
                                answ = false;
                            }
                        }
                        if (answ) {
                            YesNoDialogFragment dial = new YesNoDialogFragment();
                            dial.setInfo(FrmInventario.this, FrmInventario.this, "Finalizar conteo", "¿Está seguro de cambiar el conteo?", CAMBIAR_CONTEO);
                            dial.show(getSupportFragmentManager(), "MyDialog");
                        } else {
                            Toast.makeText(this, "Aún faltan productos por contar. No se puede cambiar conteo.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        YesNoDialogFragment dial = new YesNoDialogFragment();
                        dial.setInfo(FrmInventario.this, FrmInventario.this, "Finalizar conteo", "¿Está seguro de cambiar el conteo?", CAMBIAR_CONTEO);
                        dial.show(getSupportFragmentManager(), "MyDialog");
                    }
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }



           /* case R.id.action_generar_reporte:
                try {
                    generarReporte();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;*/
            case R.id.action_enviar_datos:
                try {
                    SQLiteDatabase db = BaseHelper.getWritable(this);
                    Object[][] inventarios = new Inventario().selectInventariosTotales(db, false, usuario.getCurrGrupo(),
                            usuario.getCurrSubgr(),
                            usuario.getCurrSubgr2(),
                            usuario.getCurrSubgr3(),
                            usuario.getCurrClase(),
                            usuario.getCurrUbicacion());

                    boolean answ = true;
                    for (int f = 0; f < inventarios.length; f++) {
                        if (inventarios[f][3] == null) {
                            answ = false;
                        }
                    }
                    if (answ) {
                        BaseHelper.tryClose(db);
                        YesNoDialogFragment dial3 = new YesNoDialogFragment();
                        dial3.setInfo(FrmInventario.this, FrmInventario.this, "Enviar datos", "¿Está seguro de enviar los datos?", ENVIAR_DATOS);
                        dial3.show(getSupportFragmentManager(), "MyDialog");
                    } else {
                        Toast.makeText(this, "Aún faltan productos por contar. No se pueden enviar los datos.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_finalizar_inventario:
                try {
                    SQLiteDatabase db = BaseHelper.getReadable(this);
                    if (usuario.getDatosEnviados() || new Inventario().countInventarios(db) == 0) {
                        YesNoDialogFragment dial2 = new YesNoDialogFragment();
                        dial2.setInfo(FrmInventario.this, FrmInventario.this, "Finalizar inventario", "¿Está seguro de finalizar el inventario? Los datos que no se hayan enviado se perderán. ", FINALIZAR_INVENTARIO);
                        dial2.show(getSupportFragmentManager(), "MyDialog");
                    } else {
                        throw new Exception("No se puede finalizar el inventario sin enviar los datos existentes.");
                    }
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.action_liberar_seleccion:
                YesNoDialogFragment dia = new YesNoDialogFragment();
                dia.setInfo(FrmInventario.this, FrmInventario.this, "Liberar selección", "¿Está seguro de liberar selección?", LIBERAR_SELECCION);
                dia.show(getSupportFragmentManager(), "MyDialog");
                break;
            case R.id.action_liberar_con_contrasenia:
                YesNoDialogFragment dia3 = new YesNoDialogFragment();
                dia3.setInfo(FrmInventario.this, FrmInventario.this, "Liberar selección", "¿Está seguro de liberar selección?, perderá todos sus datos", LIBERAR_SELECCION_CONTRASENIA);
                dia3.show(getSupportFragmentManager(), "MyDialog");
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

        spnFaltantes.setEnabled(enable);


        numero.setFocusable(enable);
        numero.setFocusableInTouchMode(enable);
        numero.setClickable(enable);

        //rbCodigo.setFocusable(enable);
        //rbCodigo.setFocusableInTouchMode(enable);
        rbCodigo.setClickable(enable);

        //rbLectura.setFocusable(enable);
        //rbLectura.setFocusableInTouchMode(enable);
        rbLectura.setClickable(enable);

        btnCargar.setEnabled(enable);
        btnCargar.setFocusable(enable);
        btnCargar.setFocusableInTouchMode(enable);
        btnCargar.setClickable(enable);

        disableEnableCantidad(!enable);
    }

    private void disableEnableCantidad(boolean enable) {
        cantidad.setEnabled(enable);
        btnAceptar.setEnabled(enable);

        cantidad.setFocusable(enable);
        cantidad.setFocusableInTouchMode(enable);
        cantidad.setClickable(enable);
        cantidad.requestFocus();

        btnAceptar.setFocusable(enable);
        btnAceptar.setFocusableInTouchMode(enable);
        btnAceptar.setClickable(enable);
    }

    public void generarReporte() throws Exception {
        /*File directory = getFilesDir();
        //Create file path for Pdf
        String fpath = "difer" + ".pdf";
        File file = new File(directory, fpath);
        if (!file.exists()) {
            file.createNewFile();
        }*/

        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "");
        if (!directory.exists()) {
            directory.mkdir();
            Log.i("creat", "Pdf Directory created");
        }

        File file = new File(directory + "/" + "difer" + ".pdf");


        System.out.println("ESTA ES LA RUTA  " + file.getPath());

        Date date = new Date();
        SQLiteDatabase db = BaseHelper.getReadable(this);
        Object[][] inventarios = new Inventario().selectInventariosTotales(db, true, usuario.getCurrGrupo(), usuario.getCurrSubgr(),
                usuario.getCurrSubgr2(), usuario.getCurrSubgr3(), usuario.getCurrClase(), usuario.getCurrUbicacion());
        BaseHelper.tryClose(db);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
        document.open();
        document.add(new Paragraph("Diferencias Bodega " + usuario.getCurrBodega()));
        document.add(new Paragraph("Grupo " + usuario.getCurrGrupo()));
        document.add(new Paragraph("Subgrupo " + usuario.getCurrSubgr()));

        if (usuario.getCurrSubgr2() != null) {
            document.add(new Paragraph("Subgrupo2 " + usuario.getCurrSubgr2()));
        }
        if (usuario.getCurrSubgr3() != null) {
            document.add(new Paragraph("Subgrupo3 " + usuario.getCurrSubgr3()));
        }
        if (usuario.getCurrClase() != null) {
            document.add(new Paragraph("Clase " + usuario.getCurrClase()));
        }
        if (usuario.getCurrUbicacion() != null) {
            document.add(new Paragraph("Ubicacion " + usuario.getCurrUbicacion()));
        }
        document.add(new Paragraph("Conteo " + usuario.getCurrConteo()));
        document.add(new Paragraph("Fecha de generación: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date)));
        document.add(new Paragraph(""));
        document.add(new Paragraph(""));

        for (int i = 0; i < inventarios.length; i++) {
            document.add(new Paragraph(inventarios[i][0] + "-" + (inventarios[i][1] == null ? "Sin nombre" : inventarios[i][1]) + "   " +
                    inventarios[i][usuario.getCurrConteo() + 2]));
        }
        document.close();


        Toast.makeText(FrmInventario.this, "Archivo generado", Toast.LENGTH_LONG).show();



        /*





        //using add method in document to insert a paragraph
        document.add(new Paragraph("My First Pdf !"));
        document.add(new Paragraph("Hello World"));
        // close document








        //---------------------------------------------------
        // create folder to save the pdf

        // new FileOutputStream("/Internal storage/")





        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "FolderName");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.i("creat", "Pdf Directory created");
        }

        // end creat folder

        // create file name using todaydate.

        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        //File myFile = new File(pdfFolder + "/" + timeStamp + ".pdf");

        //OutputStream output = new FileOutputStream(myFile);
        SQLiteDatabase db = BaseHelper.getReadable(this);
        Object[][] inventarios = new Inventario().selectInventariosTotales(db, true, usuario.getCurrGrupo(), usuario.getCurrSubgr(), usuario.getCurrSubgr2(), usuario.getCurrSubgr3(), usuario.getCurrClase());
        BaseHelper.tryClose(db);

        PdfWriter writer = new PdfWriter(pdfFolder + "/" + timeStamp + ".pdf");
        com.itextpdf.kernel.pdf.PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("Diferencias Bodega " + usuario.getCurrBodega()));
        document.add(new Paragraph("Grupo " + usuario.getCurrGrupo()));
        document.add(new Paragraph("Subgrupo " + usuario.getCurrSubgr()));

        if (usuario.getCurrSubgr2() != null) {
            document.add(new Paragraph("Subgrupo2 " + usuario.getCurrSubgr2()));
        }
        if (usuario.getCurrSubgr3() != null) {
            document.add(new Paragraph("Subgrupo3 " + usuario.getCurrSubgr3()));
        }
        if (usuario.getCurrClase() != null) {
            document.add(new Paragraph("Clase " + usuario.getCurrClase()));
        }
        document.add(new Paragraph("Conteo " + usuario.getCurrConteo()));
        document.add(new Paragraph("Fecha de generación: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date)));
        document.add(new Paragraph(""));
        document.add(new Paragraph(""));

        for (int i = 0; i < inventarios.length; i++) {
            document.add(new Paragraph(inventarios[i][0] + "-" + (inventarios[i][1] == null ? "Sin nombre" : inventarios[i][1]) + "   " +
                    inventarios[i][usuario.getCurrConteo() + 2]));
        }
        document.close();*/
    }

    @Override
    public void onFinishDialog(boolean ans, int code) {
        if (ans == true) {
            if (code == SUMAR_CANTIDAD && ans) {
                SQLiteDatabase db = BaseHelper.getReadable(this);
                updateCantidad(operarEnLaCantidad(db));
                BaseHelper.tryClose(db);
            }
            if (code == CAMBIAR_CONTEO && ans) {
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
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            if (code == ENVIAR_DATOS && ans) {
                try {
                    SQLiteDatabase db = BaseHelper.getWritable(this);

                    dialogUtils = new DialogUtils(this, "Cargando");
                    dialogUtils.showDialog(this.getWindow());
                    new Inventario().insertProductsNotOnInventario(db, usuario.getCurrBodega(),
                            new Date().toString(),
                            usuario.getUsuario(),
                            usuario.getCurrGrupo(),
                            usuario.getCurrSubgr(),
                            usuario.getCurrSubgr2(),
                            usuario.getCurrSubgr3(),
                            usuario.getCurrClase(),
                            usuario.getCurrUbicacion());
                    insertResultsOnWebservice();
                    BaseHelper.tryClose(db);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            if (code == FINALIZAR_INVENTARIO && ans) {
                try {
                    SQLiteDatabase db = BaseHelper.getWritable(this);
                    if (new Inventario().countInventarios(db) == 0) {
                        throw new Exception("Debe liberar la selección");//CONTINUAR A
                    } else {
                        dialogUtils = new DialogUtils(this, "Cargando");
                        dialogUtils.showDialog(this.getWindow());

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
                        dialogUtils.dissmissDialog();

                        Intent i = new Intent(FrmInventario.this, FrmLogin.class);
                        startActivityForResult(i, 1);
                        BaseHelper.tryClose(db);
                        Toast.makeText(FrmInventario.this, "El inventario finalizó exitosamente", Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            if (code == LIBERAR_SELECCION && ans) {
                try {
                    SQLiteDatabase db = BaseHelper.getReadable(this);
                    if (new Inventario().countInventarios(db) == 0) {
                        dialogUtils = new DialogUtils(this, "Cargando");
                        dialogUtils.showDialog(this.getWindow());

                        freeWebserviceFisicos();
                    } else {
                        throw new Exception("No se puede liberar la selección debido a que ya tiene productos inventariados.");
                    }
                    BaseHelper.tryClose(db);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            if (code == LIBERAR_SELECCION_CONTRASENIA && ans) {
                try {
                    Intent i = new Intent(FrmInventario.this, FrmLiberarSeleccion.class);
                    startActivityForResult(i, 1);
                    dialogUtils.dissmissDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void updateCantidad(Double cant) {
        SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
        if (usuario.getCurrConteo() == 1) {
            Double cantidadConteo = currentInventario.getConteo1() == null ? 0 : currentInventario.getConteo1();
            currentInventario.setConteo1(cantidadConteo + cant);
            currentInventario.setUsuario1(usuario.getUsuario());
        } else if (usuario.getCurrConteo() == 2) {
            Double cantidadConteo = currentInventario.getConteo2() == null ? 0 : currentInventario.getConteo2();
            currentInventario.setConteo2(cantidadConteo + cant);
            currentInventario.setUsuario2(usuario.getUsuario());
        } else {
            Double cantidadConteo = currentInventario.getConteo3() == null ? 0 : currentInventario.getConteo3();
            currentInventario.setConteo3(cantidadConteo + cant);
            currentInventario.setUsuario3(usuario.getUsuario());
        }

        currentInventario.updateCurrent(db, usuario.getCurrConteo(), selectedProduct[0].toString());
        Toast.makeText(FrmInventario.this, "Registro actualizado", Toast.LENGTH_LONG).show();
        BaseHelper.tryClose(db);
        limpiar();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            System.out.println("CODIGO REQ" + requestCode);
            System.out.println("CODIGO REQ" + resultCode);
            if (requestCode == 49374) {
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                if (scanningResult != null && scanningResult.getContents() != "") {
                    numero.setText(scanningResult.getContents());
                    selectProductoWithNumber(1);
                    if (spnFaltantes != null && mapFaltantes != null) {
                        spnFaltantes.setEnabled(mapFaltantes.get(spnFaltantes.getSelectedItemPosition()) != "-1");
                    }
                } else {
                    throw new Exception("No se recibieron datos del lector de código de barras");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void insertResultsOnWebservice() throws Exception {
        if (!NetUtils.isOnlineNet(FrmInventario.this)) {
            dialogUtils.dissmissDialog();
            throw new Exception("No hay conexión a internet");
        } else {
            final SQLiteDatabase db = BaseHelper.getWritable(this);
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    checkWebserviceResults(db);
                }
            };
            remote.setContext(this);

            ArrayList queryDatos = new ArrayList();
            ArrayList<Inventario> inventarios = new Inventario().selectInventarios(db);
            db.close();

            for (int i = 0; i < inventarios.size(); i++) {
                Inventario inventario = inventarios.get(i);
                String query = "UPDATE f " +
                        "SET toma_1=" + inventario.getConteo1() + ", " +
                        "usu_toma_1=UPPER('" + inventario.getUsuario1() + "'), " +
                        "toma_2=" + inventario.getConteo2() + ", " +
                        "usu_toma_2=UPPER('" + inventario.getUsuario2() + "'), " +
                        "toma_3=" + inventario.getConteo3() + ", " +
                        "usu_toma_3=UPPER('" + inventario.getUsuario3() + "'), " +
                        "fecha_ultima=GETDATE() " +
                        "FROM referencias_fis f " +
                        "JOIN referencias r on r.codigo=f.codigo " +
                        "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                        "WHERE f.bodega='" + usuario.getCurrBodega() + "' " +
                        "AND s.ano=YEAR(getdate()) " +
                        "AND s.mes=MONTH(getdate()) " +
                        "AND f.codigo='" + inventario.getProducto() + "' ";
                queryDatos.add(query);
                System.out.println("QUERYYYYYY///" + query);
            }
            remote.setQuery(queryDatos);
            remote.execute();
        }
    }

    private void checkWebserviceResults(final SQLiteDatabase db) throws Exception {
        if (!NetUtils.isOnlineNet(FrmInventario.this)) {
            dialogUtils.dissmissDialog();
            throw new Exception("No hay conexión a internet");
        } else {
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    ArrayList resultsDatos = (ArrayList) object;

                    ArrayList rawProductos = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), FrmInventario.this);
                    if (resultsDatos.get(0).equals("[]")) {
                        dialogUtils.dissmissDialog();
                        BaseHelper.tryClose(db);
                        Toast.makeText(FrmInventario.this, "No se han podido enviar los datos, intente nuevament", Toast.LENGTH_LONG).show();
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
                            new Producto().updateProductosOnInventario(db);
                            new Inventario().delete(db);
                            usuario.setCurrConteo(1);
                            usuario.setDatosEnviados(true);
                            usuario.updateCurrent(db);

                            dialogUtils.dissmissDialog();

                            Intent i = new Intent(FrmInventario.this, FrmOpciones.class);
                            startActivityForResult(i, 1);
                            BaseHelper.tryClose(db);
                            Toast.makeText(FrmInventario.this, "Se enviaron los datos exitosamente", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(FrmInventario.this, "No se han podido enviar los datos, intente nuevamente", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            };
            remote.setContext(FrmInventario.this);

            ArrayList queryDatos = new ArrayList();

            String query = "SELECT f.codigo,f.toma_1,f.usu_toma_1,f.toma_2,f.usu_toma_2,f.toma_3,f.usu_toma_3,f.fecha_ultima " +
                    "FROM referencias_fis f " +
                    "JOIN referencias r on r.codigo=f.codigo " +
                    "WHERE bodega='" + usuario.getCurrBodega() + "' ";
            query += usuario.getFilterQueryForWebservice();

            queryDatos.add(query);
            System.out.println("QUERYYYYYY///" + query);
            remote.setQuery(queryDatos);
            remote.execute();
        }
    }

    private void freeWebserviceFisicos() throws Exception {
        if (!NetUtils.isOnlineNet(FrmInventario.this)) {
            dialogUtils.dissmissDialog();
            throw new Exception("No hay conexión a internet");
        } else {
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    checkWebserviceFisicos();
                }
            };
            ArrayList queryDatos = new ArrayList();
            remote.setContext(this);
            String query = "UPDATE f SET fisico=0 " +
                    "FROM referencias_fis f " +
                    "JOIN referencias r on r.codigo=f.codigo " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                    "WHERE f.bodega='" + usuario.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate()) " +
                    "AND f.fisico=1  ";

            query += usuario.getFilterQueryForWebservice();
            queryDatos.add(query);
            System.out.println("QUERYYYYYY///" + query);
            remote.setQuery(queryDatos);
            remote.execute();
        }
    }

    private void checkWebserviceFisicos() throws Exception {
        if (!NetUtils.isOnlineNet(FrmInventario.this)) {
            dialogUtils.dissmissDialog();
            throw new Exception("No hay conexión a internet");
        } else {
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    SQLiteDatabase db = BaseHelper.getWritable(FrmInventario.this);
                    ArrayList resultsDatos = (ArrayList) object;

                    ArrayList rawResults = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), FrmInventario.this);
                    if (resultsDatos.get(0).equals("[]")) {
                        dialogUtils.dissmissDialog();
                        BaseHelper.tryClose(db);
                        Toast.makeText(FrmInventario.this, "No se han podido liberar selección, intente nuevamente", Toast.LENGTH_LONG).show();
                    } else {
                        boolean validar = true;

                        for (int i = 0; i < rawResults.size(); i++) {
                            JSONObject fisico = ((JSONObject) rawResults.get(i));
                            if (fisico.getInt("fisico") == 1) {
                                validar = false;
                            }
                        }

                        if (validar == true) {
                            db = BaseHelper.getWritable(FrmInventario.this);

                            usuario.setCurrGrupo(null);
                            usuario.setCurrSubgr(null);
                            usuario.setCurrSubgr2(null);
                            usuario.setCurrSubgr3(null);
                            usuario.setCurrClase(null);
                            usuario.setCurrConteo(1);
                            usuario.updateCurrent(db);

                            new Inventario().delete(db);
                            Intent i = new Intent(FrmInventario.this, FrmOpciones.class);
                            startActivityForResult(i, 1);
                            Toast.makeText(FrmInventario.this, "Se ha liberado la selección exitosamente", Toast.LENGTH_LONG).show();
                            dialogUtils.dissmissDialog();
                            finish();
                            db.close();

                        } else {
                            Toast.makeText(FrmInventario.this, "No se han podido liberar selección, intente nuevamente", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            };
            remote.setContext(FrmInventario.this);

            ArrayList queryDatos = new ArrayList();

            String query = "SELECT fisico " +
                    "FROM referencias_fis f " +
                    "JOIN referencias r on r.codigo=f.codigo " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                    "WHERE f.bodega='" + usuario.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate()) ";

            query += usuario.getFilterQueryForWebservice();

            queryDatos.add(query);
            System.out.println("QUERYYYYYY///" + query);
            remote.setQuery(queryDatos);
            remote.execute();
        }
    }

}
