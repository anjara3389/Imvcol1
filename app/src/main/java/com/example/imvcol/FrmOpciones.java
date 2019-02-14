package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import com.example.imvcol.WebserviceConnection.ExecuteRemoteQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FrmOpciones extends AppCompatActivity implements YesNoDialogFragment.MyDialogDialogListener {

    DialogUtils dialogUtils;

    private Spinner spnGrupo;
    private Spinner spnSubgrupo;
    private Spinner spnSubgrupo3;
    private Spinner spnSubgrupo2;
    private Spinner spnClase;
    private TextView lblSubgrupo;
    private TextView lblSubgrupo2;
    private TextView lblSubgrupo3;
    private TextView lblClase;
    private Object[][] wholeGrupos, wholeSubgrupos, wholeSubgrupos3, wholeSubgrupos2, wholeClases;
    private ArrayList rawGrupos, rawSubgrupos, rawSubgrupos3, rawSubgrupos2, rawClases;
    private Usuario usuario;
    private static final int FINALIZAR_INVENTARIO = 3;
    HashMap<Integer, String> mapGrupo;
    HashMap<Integer, String> mapSubgrupo;
    HashMap<Integer, String> mapSubgrupo2;
    HashMap<Integer, String> mapSubgrupo3;
    HashMap<Integer, String> mapClase;

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
            lblSubgrupo = findViewById(R.id.frm_opciones_lbl_subgrupo);
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

            btnAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapGrupo = (HashMap<Integer, String>) rawGrupos.get(1);
                    mapSubgrupo = (HashMap<Integer, String>) rawSubgrupos.get(1);
                    if (usuario.getModo() == usuario.MODO_LISTA) {
                        mapSubgrupo2 = (HashMap<Integer, String>) rawSubgrupos2.get(1);
                        mapSubgrupo3 = (HashMap<Integer, String>) rawSubgrupos3.get(1);
                        mapClase = (HashMap<Integer, String>) rawClases.get(1);
                    }
                    if (changeValue(mapGrupo.get(spnGrupo.getSelectedItemPosition())) == null) {
                        Toast.makeText(FrmOpciones.this, "Debe seleccionar un grupo", Toast.LENGTH_LONG).show();
                    } else if (changeValue(mapSubgrupo.get(spnSubgrupo.getSelectedItemPosition())) == null) {
                        Toast.makeText(FrmOpciones.this, "Debe seleccionar un subgrupo", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            dialogUtils = new DialogUtils(FrmOpciones.this, "Cargando");
                            dialogUtils.showDialog(getWindow());
                            usuario.setCurrGrupo(changeValue(mapGrupo.get(spnGrupo.getSelectedItemPosition())));
                            usuario.setCurrSubgr(changeValue(mapSubgrupo.get(spnSubgrupo.getSelectedItemPosition())));
                            if (usuario.getModo() == usuario.MODO_LISTA) {
                                usuario.setCurrSubgr2(changeValue(mapSubgrupo2.get(spnSubgrupo2.getSelectedItemPosition())));
                                usuario.setCurrSubgr3(changeValue(mapSubgrupo3.get(spnSubgrupo2.getSelectedItemPosition())));
                                usuario.setCurrClase(changeValue(mapClase.get(spnClase.getSelectedItemPosition())));
                            }
                            countWebserviceFisicos(v);
                        } catch (Exception e) {
                            Toast.makeText(FrmOpciones.this, "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }
            });

            db = BaseHelper.getReadable(this);
            wholeGrupos = new Grupo().selectGrupos(db);
            wholeSubgrupos = new Subgrupo().selectSubgrupos(db);
            if (usuario.getModo() == usuario.MODO_LISTA) {
                wholeSubgrupos2 = new Subgrupo2().selectSubgrupos2(db);
                wholeSubgrupos3 = new Subgrupo3().selectSubgrupos3(db);
                wholeClases = new Clase().selectClases(db);
            }

            if (wholeGrupos != null && (wholeSubgrupos != null || usuario.getModo() == usuario.MODO_BARRAS)) {
                if (usuario.getModo() == usuario.MODO_LISTA) {
                    prepareClases();
                }
                prepareGrupos();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error/" + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error/" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
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
        setTitle("INVFISCOL 1.0");
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
        ArrayList selected = new ArrayList();

        for (int i = 0; i < wholeSubgrupos.length; i++) {
            Object[] subgrupo = wholeSubgrupos[i];
            if (subgrupo[0].equals("-1") || subgrupo[2].equals(grupoId)) {
                selected.add(subgrupo);
            }
        }

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

    private void countWebserviceFisicos(final View v) {
        final SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                ArrayList resultsDatos = (ArrayList) object;
                JSONObject rawResult = (JSONObject) ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), null, FrmOpciones.this).get(0);
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

    private void updateWebserviceFisicos() {
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

    private void checkWebserviceFisicos() {
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                SQLiteDatabase db = BaseHelper.getWritable(FrmOpciones.this);
                ArrayList resultsDatos = (ArrayList) object;

                ArrayList rawResults = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), null, FrmOpciones.this);
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
        if (code == FINALIZAR_INVENTARIO) {
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
                Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG);
            }
        }
    }
}
