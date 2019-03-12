package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imvcol.Utils.DialogUtils;
import com.example.imvcol.Utils.NetUtils;
import com.example.imvcol.WebserviceConnection.ExecuteRemoteQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FrmOpciones extends AppCompatActivity implements YesNoDialogFragment.MyDialogDialogListener {

    DialogUtils dialogUtils;

    private Spinner spnGrupo;
    private Spinner spnSubgrupo;
    private Spinner spnSubgrupo3;
    private Spinner spnSubgrupo2;
    private Spinner spnClase;
    private TextView lblSubgrupo2;
    private TextView lblSubgrupo3;
    private TextView lblClase;
    private Object[][] wholeGrupos, wholeSubgrupos, wholeSubgrupos3, wholeSubgrupos2, wholeClases;
    private ArrayList rawGrupos, rawSubgrupos, rawSubgrupos3, rawSubgrupos2, rawClases;
    private Usuario usuario;
    private static final int FINALIZAR_INVENTARIO = 3;
    private static final int SELECCION_ANTERIOR = 0;
    HashMap<Integer, String> mapGrupos;
    HashMap<Integer, String> mapSubgrupos;
    HashMap<Integer, String> mapSubgrupos2;
    HashMap<Integer, String> mapSubgrupos3;
    HashMap<Integer, String> mapClases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_opciones);
        try {
            SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
            usuario = new Usuario().selectUsuario(db);
            BaseHelper.tryClose(db);

            Button btnAceptar = findViewById(R.id.frm_opciones_btn_aceptar);
            spnGrupo = findViewById(R.id.frm_opciones_spn_grupo);
            spnSubgrupo = findViewById(R.id.frm_opciones_spn_subgrupo);
            spnSubgrupo2 = findViewById(R.id.frm_opciones_spn_subgrupo_2);
            spnSubgrupo3 = findViewById(R.id.frm_opciones_spn_subgrupo_3);
            spnClase = findViewById(R.id.frm_opciones_spn_clase);
            lblClase = findViewById(R.id.frm_opciones_lbl_clase_);
            lblSubgrupo2 = findViewById(R.id.frm_opciones_lbl_subgrupo_2);
            lblSubgrupo3 = findViewById(R.id.frm_opciones_lbl_subgrupo_3);

            if (usuario.getModo() == usuario.MODO_BARRAS) {
                spnSubgrupo2.setVisibility(View.GONE);
                spnSubgrupo3.setVisibility(View.GONE);
                spnClase.setVisibility(View.GONE);
                lblClase.setVisibility(View.GONE);
                lblSubgrupo2.setVisibility(View.GONE);
                lblSubgrupo3.setVisibility(View.GONE);
            }

            //dialogUtils = new DialogUtils(FrmOpciones.this, "Cargando");
            //dialogUtils.showDialog(getWindow());

            db = BaseHelper.getReadable(FrmOpciones.this);
            wholeGrupos = new Grupo().selectGrupos(db);
            wholeSubgrupos = new Subgrupo().selectSubgrupos(db);
            if (usuario.getModo() == usuario.MODO_LISTA) {
                wholeSubgrupos2 = new Subgrupo2().selectSubgrupos2(db);
                wholeSubgrupos3 = new Subgrupo3().selectSubgrupos3(db);
                wholeClases = new Clase().selectClases(db);

                System.out.println("SUBGRUPOS3/////" + wholeSubgrupos3);
            }

            if (wholeGrupos != null && (wholeSubgrupos != null || usuario.getModo() == usuario.MODO_BARRAS)) {
                if (usuario.getModo() == usuario.MODO_LISTA) {
                    prepareClases();
                }
                prepareGrupos();
            }
            BaseHelper.tryClose(db);

            mapGrupos = (HashMap<Integer, String>) rawGrupos.get(1);

            continuarSeleccion();

            btnAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //mapGrupos = (HashMap<Integer, String>) rawGrupos.get(1);
                        mapSubgrupos = (HashMap<Integer, String>) rawSubgrupos.get(1);
                        if (usuario.getModo() == usuario.MODO_LISTA) {
                            mapSubgrupos2 = (HashMap<Integer, String>) rawSubgrupos2.get(1);
                            mapSubgrupos3 = (HashMap<Integer, String>) rawSubgrupos3.get(1);
                            mapClases = (HashMap<Integer, String>) rawClases.get(1);
                        }
                        if (changeValue(mapGrupos.get(spnGrupo.getSelectedItemPosition())) == null) {
                            throw new Exception("Debe seleccionar un grupo");
                        } else if (changeValue(mapSubgrupos.get(spnSubgrupo.getSelectedItemPosition())) == null) {
                            throw new Exception("Debe seleccionar un subgrupo");
                        } else {
                            dialogUtils = new DialogUtils(FrmOpciones.this, "Cargando");
                            dialogUtils.showDialog(getWindow());
                            usuario.setCurrGrupo(changeValue(mapGrupos.get(spnGrupo.getSelectedItemPosition())));
                            usuario.setCurrSubgr(changeValue(mapSubgrupos.get(spnSubgrupo.getSelectedItemPosition())));
                            if (usuario.getModo() == usuario.MODO_LISTA) {
                                usuario.setCurrSubgr2(changeValue(mapSubgrupos2.get(spnSubgrupo2.getSelectedItemPosition())));
                                usuario.setCurrSubgr3(changeValue(mapSubgrupos3.get(spnSubgrupo3.getSelectedItemPosition())));
                                usuario.setCurrClase(changeValue(mapClases.get(spnClase.getSelectedItemPosition())));
                            }
                            countWebserviceFisicos(v);
                        }
                    } catch (Exception e) {
                        Toast.makeText(FrmOpciones.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            if (dialogUtils != null) {
                dialogUtils.dissmissDialog();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            if (dialogUtils != null) {
                dialogUtils.dissmissDialog();
            }
        }
    }

    private String changeValue(Object object) {
        if (object.toString().equals("-1")) {
            return null;
        } else {
            return object.toString();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);

        menu.findItem(R.id.action_diferencias).setVisible(false);
        menu.findItem(R.id.action_enviar_datos).setVisible(false);
        menu.findItem(R.id.action_finalizar_conteo).setVisible(false);
        menu.findItem(R.id.action_liberar_seleccion).setVisible(false);
        menu.findItem(R.id.action_totales).setVisible(false);
        setTitle("INVFISCOL 1.1");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_finalizar_inventario:
                try {
                    SQLiteDatabase db = BaseHelper.getReadable(this);
                    if (usuario.getDatosEnviados() || new Inventario().countInventarios(db) == 0) {
                        YesNoDialogFragment dial2 = new YesNoDialogFragment();
                        dial2.setInfo(FrmOpciones.this, FrmOpciones.this, "Finalizar inventario", "¿Está seguro de finalizar el inventario? Los datos que no se hayan enviado se perderán. ", FINALIZAR_INVENTARIO);
                        dial2.show(getSupportFragmentManager(), "MyDialog");
                    } else {
                        throw new Exception("No se puede finalizar el inventario sin enviar los datos existentes.");
                    }
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(FrmOpciones.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void continuarSeleccion() {
        boolean validation = false;
        for (int i = 0; i < wholeGrupos.length; i++) {
            System.out.println("wholegrupos " + wholeGrupos[i][0]);
            System.out.println("wholegruposxxx " + usuario.getCurrGrupo());

            if (wholeGrupos[i][0].equals(usuario.getCurrGrupo())) {//si el grupo existe
                validation = true;
                if (usuario.getCurrSubgr2() != null) {
                    boolean validation2 = false;

                    for (int j = 0; j < wholeSubgrupos.length; j++) {
                        if (wholeSubgrupos[j][0].equals(usuario.getCurrSubgr())) {
                            validation2 = true;

                            System.out.println("wholesubgrupos " + wholeSubgrupos[j][0]);
                            System.out.println("wholeSUBgruposxxx " + usuario.getCurrSubgr());

                            if (usuario.getCurrSubgr3() != null) {
                                boolean validation3 = false;
                                for (int k = 0; k < wholeSubgrupos2.length; k++) {
                                    System.out.println("whole2grupos " + wholeSubgrupos2[k][0]);
                                    System.out.println("wholeSUBgruposxxx " + usuario.getCurrSubgr2());

                                    if (wholeSubgrupos2[k][0].equals(usuario.getCurrSubgr2())) {
                                        validation3 = true;
                                    }
                                }
                                validation = validation && validation3;
                            }
                        }
                    }
                    validation = validation && validation2;
                }
            }
        }
        System.out.println("validation " + validation);
        if (validation) {
            YesNoDialogFragment dial = new YesNoDialogFragment();
            dial.setInfo(FrmOpciones.this, FrmOpciones.this, "Continuar con selección", "¿Desea continuar con la selección anterior?", SELECCION_ANTERIOR);
            dial.show(getSupportFragmentManager(), "MyDialog");
        } else {
           /* SQLiteDatabase db = BaseHelper.getReadable(FrmOpciones.this);
            usuario.setCurrGrupo(null);
            usuario.setCurrSubgr(null);
            usuario.setCurrSubgr2(null);
            usuario.setCurrSubgr3(null);
            usuario.setCurrClase(null);
            usuario.updateCurrent(db);
            BaseHelper.tryClose(db);*/
        }
    }

    private void prepareGrupos() {
        rawGrupos = ArrayUtils.mapObjects(wholeGrupos);
        String[] dataSpnGrupos = (String[]) rawGrupos.get(0);

        ArrayAdapter<String> adapterGrupos = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnGrupos);
        adapterGrupos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGrupo.setAdapter(adapterGrupos);

        spnGrupo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        prepareSubgrupos(((HashMap<Integer, String>) rawGrupos.get(1)).get(spnGrupo.getSelectedItemPosition()));
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });
    }

    private void prepareSubgrupos(final String grupoId) {

        System.out.println("GRUPO ID//" + grupoId);
        ArrayList selected = new ArrayList();

        for (int i = 0; i < wholeSubgrupos.length; i++) {

            Object[] subgrupo = wholeSubgrupos[i];
            System.out.println("GRUPO IDxxxxx//" + subgrupo[2]);
            if (subgrupo[0].equals("-1") || subgrupo[2].equals(grupoId)) {
                selected.add(subgrupo);
            }
        }
        System.out.println("selected subgrupos//" + selected);

        rawSubgrupos = ArrayUtils.mapObjects(selected);
        String[] dataSpnSubgrupos = (String[]) rawSubgrupos.get(0);

        ArrayAdapter<String> adapterSubgrupos = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnSubgrupos);
        adapterSubgrupos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSubgrupo.setAdapter(adapterSubgrupos);

        spnSubgrupo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        if (usuario.getModo() == usuario.MODO_LISTA) {
                            prepareSubgrupos2(grupoId, ((HashMap<Integer, String>) rawSubgrupos.get(1)).get(spnSubgrupo.getSelectedItemPosition()));
                        }
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });
    }

    private void prepareSubgrupos2(final String grupoId, final String subgrupoId) {
        ArrayList selected = new ArrayList();
        for (int i = 0; i < wholeSubgrupos2.length; i++) {
            Object[] subgrupo2 = wholeSubgrupos2[i];
            if (subgrupo2[0].equals("-1") || (subgrupo2[2].equals(grupoId) && subgrupo2[3].equals(subgrupoId))) {
                selected.add(subgrupo2);
            }
        }

        rawSubgrupos2 = ArrayUtils.mapObjects(selected);
        String[] dataSpnSubgrupos2 = (String[]) rawSubgrupos2.get(0);

        ArrayAdapter<String> adapterSubgrupos2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnSubgrupos2);
        adapterSubgrupos2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSubgrupo2.setAdapter(adapterSubgrupos2);

        spnSubgrupo2.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                        prepareSubgrupos3(grupoId, subgrupoId, ((HashMap<Integer, String>) rawSubgrupos2.get(1)).get(spnSubgrupo2.getSelectedItemPosition()));
                    }

                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });
    }

    private void prepareSubgrupos3(String grupoId, String subgrupoId, String subgrupo2Id) {
        ArrayList selected = new ArrayList();

        for (int i = 0; i < wholeSubgrupos3.length; i++) {
            Object[] subgrupo3 = wholeSubgrupos3[i];
            if (subgrupo3[0].equals("-1") || (subgrupo3[2].equals(grupoId) && (subgrupo3[3].equals(subgrupoId) && subgrupo3[4].equals(subgrupo2Id)))) {
                selected.add(subgrupo3);
            }
        }
        rawSubgrupos3 = ArrayUtils.mapObjects(selected);
        String[] dataSpnSubgrupos3 = (String[]) rawSubgrupos3.get(0);

        ArrayAdapter<String> adapterSubgrupos3 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnSubgrupos3);
        adapterSubgrupos3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSubgrupo3.setAdapter(adapterSubgrupos3);
    }

    private void prepareClases() {
        rawClases = ArrayUtils.mapObjects(wholeClases);
        String[] dataSpnClases = (String[]) rawClases.get(0);

        ArrayAdapter<String> adapterClases = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnClases);
        adapterClases.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnClase.setAdapter(adapterClases);
    }

    private void countWebserviceFisicos(final View v) throws Exception {
        if (!NetUtils.isOnlineNet(FrmOpciones.this)) {
            dialogUtils.dissmissDialog();
            throw new Exception("No hay conexión a internet");
        } else {
            final SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    ArrayList resultsDatos = (ArrayList) object;
                    JSONObject rawResult = (JSONObject) ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), FrmOpciones.this).get(0);
                    int cantidadFisicos = Integer.parseInt(rawResult.getString("computed"));

                    if (resultsDatos.get(0).equals("[]")) {
                        dialogUtils.dissmissDialog();
                        BaseHelper.tryClose(db);
                        Toast.makeText(v.getContext(), "No se han podido cargar los datos, intente nuevamente", Toast.LENGTH_LONG).show();
                    } else if (cantidadFisicos > 0) {
                        System.out.println("////////////////////cantidadFisicos" + cantidadFisicos);
                        Toast.makeText(v.getContext(), "No se pueden cargar los productos correspondientes a los valores seleccionados ya que hay productos que ya han sido tomados para inventario por otra persona", Toast.LENGTH_LONG).show();
                        dialogUtils.dissmissDialog();
                    } else {
                        updateWebserviceFisicos();
                    }
                }
            };
            remote.setContext(v.getContext());

            ArrayList queryDatos = new ArrayList();

            String query = "SELECT COUNT(*) " +
                    "FROM referencias_fis f " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                    "JOIN referencias r on r.codigo=s.codigo " +
                    "WHERE f.bodega='" + usuario.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate()) " +
                    "AND f.fisico<>0  ";

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

    private void updateWebserviceFisicos() throws Exception {
        if (!NetUtils.isOnlineNet(FrmOpciones.this)) {
            dialogUtils.dissmissDialog();
            throw new Exception("No hay conexión a internet");
        } else {
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) {
                    checkWebserviceFisicos();
                }
            };
            ArrayList queryDatos = new ArrayList();
            remote.setContext(this);
            String query = "UPDATE F SET fisico=1 " +
                    "FROM referencias_fis F " +
                    "JOIN referencias r on r.codigo=F.codigo " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                    "WHERE F.bodega='" + usuario.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate()) " +
                    "AND f.fisico=0  ";

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

    private void checkWebserviceFisicos() {
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                SQLiteDatabase db = BaseHelper.getWritable(FrmOpciones.this);
                ArrayList resultsDatos = (ArrayList) object;

                ArrayList rawResults = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), FrmOpciones.this);
                if (resultsDatos.get(0).equals("[]")) {
                    dialogUtils.dissmissDialog();
                    BaseHelper.tryClose(db);
                    Toast.makeText(FrmOpciones.this, "No se han podido seleccionar fisicos, intente nuevamente", Toast.LENGTH_LONG).show();
                } else {
                    boolean validar = true;

                    for (int i = 0; i < rawResults.size(); i++) {
                        JSONObject fisico = ((JSONObject) rawResults.get(i));
                        if (fisico.getInt("fisico") == 0) {
                            validar = false;
                        }
                    }

                    if (validar == true) {
                        db = BaseHelper.getWritable(FrmOpciones.this);
                        usuario.setDatosEnviados(false);
                        usuario.updateCurrent(db);
                        dialogUtils.dissmissDialog();
                        Intent i = new Intent(FrmOpciones.this, FrmInventario.class);
                        startActivityForResult(i, 1);
                        finish();

                    } else {
                        Toast.makeText(FrmOpciones.this, "No se han podido seleccionar fisicos, intente nuevamente", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        remote.setContext(FrmOpciones.this);

        ArrayList queryDatos = new ArrayList();

        String query = "SELECT fisico " +
                "FROM referencias_fis F " +
                "JOIN referencias r on r.codigo=F.codigo " +
                "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                "WHERE F.bodega='" + usuario.getCurrBodega() + "' " +
                "AND s.ano=YEAR(getdate()) " +
                "AND s.mes=MONTH(getdate()) ";

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


    @Override
    public void onFinishDialog(boolean ans, int code) {
        if (code == FINALIZAR_INVENTARIO && ans) {
            try {
                SQLiteDatabase db = BaseHelper.getWritable(this);
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

                Intent i = new Intent(FrmOpciones.this, FrmLogin.class);
                startActivityForResult(i, 1);
                BaseHelper.tryClose(db);
                Toast.makeText(FrmOpciones.this, "El inventario finalizó exitosamente", Toast.LENGTH_LONG).show();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG);
            }
        }
        if (code == SELECCION_ANTERIOR) {
            if (ans) {
                if (usuario.getCurrSubgr() != null) {
                    for (Map.Entry<Integer, String> grupoEntry : mapGrupos.entrySet()) {
                        if (grupoEntry.getValue().equals(usuario.getCurrGrupo())) {
                            spnGrupo.setSelection(grupoEntry.getKey());
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    mapSubgrupos = (HashMap<Integer, String>) rawSubgrupos.get(1);
                                    if (usuario.getCurrSubgr2() != null) {
                                        for (Map.Entry<Integer, String> subgrupoEntry : mapSubgrupos.entrySet()) {
                                            if (subgrupoEntry.getKey().toString().equals(usuario.getCurrSubgr())) {
                                                spnSubgrupo.setSelection(subgrupoEntry.getKey());
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    public void run() {
                                                        mapSubgrupos2 = (HashMap<Integer, String>) rawSubgrupos2.get(1);
                                                        if (usuario.getCurrSubgr3() != null) {
                                                            for (Map.Entry<Integer, String> subgrupo2entry : mapSubgrupos2.entrySet()) {
                                                                if (subgrupo2entry.getKey().toString().equals(usuario.getCurrSubgr2())) {
                                                                    spnSubgrupo2.setSelection(subgrupo2entry.getKey());
                                                                }
                                                            }
                                                        }
                                                    }
                                                }, 1000);
                                            }
                                        }
                                    }
                                }
                            }, 500);
                        }
                    }
                }
            } else {
                usuario.setCurrGrupo(null);
                usuario.setCurrSubgr(null);
                usuario.setCurrSubgr2(null);
                usuario.setCurrSubgr3(null);
                usuario.setCurrClase(null);
                SQLiteDatabase db = BaseHelper.getWritable(this);
                usuario.updateCurrent(db);
                BaseHelper.tryClose(db);
            }
        }
    }
}
