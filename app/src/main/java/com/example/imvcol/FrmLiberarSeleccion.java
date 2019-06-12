package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imvcol.Utils.DialogUtils;
import com.example.imvcol.Utils.NetUtils;
import com.example.imvcol.WebserviceConnection.ExecuteRemoteQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FrmLiberarSeleccion extends AppCompatActivity {
    private EditText contrasenia;
    private Button aceptar;

    private Usuario usuario;
    private DialogUtils dialogUtils;

    private String grupo;
    private String subgrupo;
    private String subgr2;
    private String subgr3;
    private String clase;
    private String ubicacion;
    private String desdeOpciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_liberar_seleccion);
        try {
            SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
            usuario = new Usuario().selectUsuario(db);
            usuario.deleteOldSesion(FrmLiberarSeleccion.this);
            BaseHelper.tryClose(db);
            Bundle bundle = getIntent().getExtras();

            if (bundle != null) {
                grupo = bundle.get("grupo") != null ? bundle.getString("grupo") : null;
                subgrupo = bundle.get("subgrupo") != null ? bundle.getString("subgrupo") : null;
                subgr2 = bundle.get("subgr2") != null ? bundle.getString("subgr2") : null;
                subgr3 = bundle.get("subgr3") != null ? bundle.getString("subgr3") : null;
                clase = bundle.get("clase") != null ? bundle.getString("clase") : null;
                ubicacion = bundle.get("ubicacion") != null ? bundle.getString("ubicacion") : null;
                desdeOpciones = bundle.get("desdeOpciones") != null ? bundle.getString("desdeOpciones") : null;

                System.out.println("//////grupo" + grupo);
                System.out.println("//////subgrupo" + subgrupo);
                System.out.println("//////subgr2" + subgr2);
                System.out.println("//////subgr3" + subgr3);
                System.out.println("//////clase" + clase);
                System.out.println("//////ubicacion" + ubicacion);
                System.out.println("//////desdeOpciones" + desdeOpciones);
            }

            contrasenia = findViewById(R.id.frm_liberar_seleccion_contrasenia);
            aceptar = findViewById(R.id.frm_liberar_seleccion_btn_aceptar);

            aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(contrasenia.getText());
                    if (contrasenia.getText().toString().equals("inv48")) {
                        try {
                            dialogUtils = new DialogUtils(FrmLiberarSeleccion.this, "Cargando");
                            dialogUtils.showDialog(FrmLiberarSeleccion.this.getWindow());
                            if (desdeOpciones == null) {
                                FrmLiberarSeleccion.this.freeWebserviceFisicosTotal();
                            } else {
                                countWebserviceConDatos();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(FrmLiberarSeleccion.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(FrmLiberarSeleccion.this, "Error: " + "Contraseña incorrecta", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FrmLiberarSeleccion.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        setTitle("COL 3.2");
        return true;
    }

    /**
     * Cuenta las referencias que ya tengan al menos conteo 1 en la base de datos por medio del webservice.
     *
     * @throws Exception
     */
    private void countWebserviceConDatos() throws Exception {
        System.out.println("//////PASÒ POR COUNT WEBSERVICE DATOS");
        if (!NetUtils.isOnlineNet(FrmLiberarSeleccion.this)) {
            dialogUtils.dissmissDialog();
            throw new Exception("No hay conexión a internet");
        } else {
            final SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    ArrayList resultsDatos = (ArrayList) object;
                    JSONObject rawResult = (JSONObject) ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), FrmLiberarSeleccion.this).get(0);
                    int cantidadFisicos = Integer.parseInt(rawResult.getString("computed"));

                    if (resultsDatos.get(0).equals("[]")) {
                        dialogUtils.dissmissDialog();
                        BaseHelper.tryClose(db);
                        Toast.makeText(FrmLiberarSeleccion.this, "No se han podido cargar los datos, intente nuevamente", Toast.LENGTH_LONG).show();
                    } else if (cantidadFisicos > 0) {
                        System.out.println("////////////////////cantidadFisicos" + cantidadFisicos);
                        Toast.makeText(FrmLiberarSeleccion.this, "No se pueden liberar los productos. La referencias seleccionadas ya tienen registrado al menos el primer conteo en la base de datos.", Toast.LENGTH_LONG).show();
                        dialogUtils.dissmissDialog();
                    } else {
                        freeWebserviceFisicosTotal();
                    }
                }
            };
            remote.setContext(FrmLiberarSeleccion.this);

            ArrayList queryDatos = new ArrayList();

            String query = "SELECT COUNT(*) " +
                    "FROM referencias_fis f " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                    "JOIN referencias r on r.codigo=s.codigo " +
                    "WHERE f.bodega='" + usuario.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate()) " +
                    "AND toma_1 IS NOT NULL ";
            //"AND f.fisico<>0  ";

            if (grupo != null) {
                query += "AND r.grupo='" + grupo + "' ";
            }
            if (subgrupo != null) {
                query += "AND r.subgrupo='" + subgrupo + "' ";
            }
            if (subgr2 != null) {
                query += "AND r.subgrupo2='" + subgr2 + "' ";
            }
            if (subgr3 != null) {
                query += "AND r.subgrupo3='" + subgr3 + "' ";
            }
            if (clase != null) {
                query += "AND r.clase='" + clase + "'";
            }
            if (usuario.getCurrUbicacion() != null) {
                query += "AND f.ubicacion='" + usuario.getCurrUbicacion() + "'";
            }

            queryDatos.add(query);
            System.out.println("QUERYYYYYY///" + query);
            remote.setQuery(queryDatos);
            remote.execute();
        }
    }

    /**
     * Libera las referencias físicas.
     *
     * @throws Exception
     */
    private void freeWebserviceFisicosTotal() throws Exception {
        if (!NetUtils.isOnlineNet(FrmLiberarSeleccion.this)) {
            dialogUtils.dissmissDialog();
            throw new Exception("No hay conexión a internet");
        } else {
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    checkWebserviceFisicosTotal();
                }
            };
            ArrayList queryDatos = new ArrayList();
            remote.setContext(this);
            String query = "UPDATE f SET fisico=0, " +
                    "toma_1=NULL, " +
                    "toma_2=NULL, " +
                    "toma_3=NULL, " +
                    "usu_toma_1=NULL, " +
                    "usu_toma_2=NULL, " +
                    "usu_toma_3=NULL, " +
                    "fecha_ultima=NULL " +
                    "FROM referencias_fis f " +
                    "JOIN referencias r on r.codigo=f.codigo " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                    "WHERE f.bodega='" + usuario.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate())";
            //"AND f.fisico=1  ";

            if (desdeOpciones == null) {
                query += usuario.getFilterQueryForWebservice();
            } else {
                if (grupo != null) {
                    query += "AND r.grupo='" + grupo + "' ";
                }
                if (subgrupo != null) {
                    query += "AND r.subgrupo='" + subgrupo + "' ";
                }
                if (subgr2 != null) {
                    query += "AND r.subgrupo2='" + subgr2 + "' ";
                }
                if (subgr3 != null) {
                    query += "AND r.subgrupo3='" + subgr3 + "' ";
                }
                if (clase != null) {
                    query += "AND r.clase='" + clase + "'";
                }
                if (ubicacion != null) {
                    query += "AND r.ubicacion='" + ubicacion + "'";
                }
            }
            queryDatos.add(query);
            System.out.println("QUERYYYYYY///" + query);
            remote.setQuery(queryDatos);
            remote.execute();
        }
    }

    private void checkWebserviceFisicosTotal() throws Exception {
        if (!NetUtils.isOnlineNet(FrmLiberarSeleccion.this)) {
            dialogUtils.dissmissDialog();
            throw new Exception("No hay conexión a internet");
        } else {
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    SQLiteDatabase db = BaseHelper.getWritable(FrmLiberarSeleccion.this);
                    ArrayList resultsDatos = (ArrayList) object;

                    ArrayList rawResults = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), FrmLiberarSeleccion.this);
                    if (resultsDatos.get(0).equals("[]")) {
                        BaseHelper.tryClose(db);
                        Toast.makeText(FrmLiberarSeleccion.this, "No se han podido liberar selección, intente nuevamente", Toast.LENGTH_LONG).show();
                        dialogUtils.dissmissDialog();
                    } else {
                        boolean validar = true;

                        for (int i = 0; i < rawResults.size(); i++) {
                            JSONObject fisico = ((JSONObject) rawResults.get(i));
                            if (fisico.getInt("fisico") == 1
                                    || !fisico.isNull("toma_1")
                                    || !fisico.isNull("toma_2")
                                    || !fisico.isNull("toma_3")
                                    || !fisico.isNull("usu_toma_1")
                                    || !fisico.isNull("usu_toma_2")
                                    || !fisico.isNull("usu_toma_3")
                                    || !fisico.isNull("fecha_ultima")) {
                                validar = false;
                            }
                        }

                        if (validar == true) {
                            db = BaseHelper.getWritable(FrmLiberarSeleccion.this);
                            new Inventario().delete(db);

                            usuario.setCurrGrupo(null);
                            usuario.setCurrSubgr(null);
                            usuario.setCurrSubgr2(null);
                            usuario.setCurrSubgr3(null);
                            usuario.setCurrClase(null);
                            usuario.setCurrUbicacion(null);
                            usuario.setCurrConteo(1);
                            usuario.updateCurrent(db);

                            new Inventario().delete(db);
                            Intent i = new Intent(FrmLiberarSeleccion.this, FrmOpciones.class);
                            startActivityForResult(i, 1);
                            Toast.makeText(FrmLiberarSeleccion.this, "Se ha liberado la selección exitosamente", Toast.LENGTH_LONG).show();
                            dialogUtils.dissmissDialog();
                            finish();
                            db.close();

                        } else {
                            Toast.makeText(FrmLiberarSeleccion.this, "No se han podido liberar selección, intente nuevamente", Toast.LENGTH_LONG).show();
                            dialogUtils.dissmissDialog();
                        }
                    }
                }
            };
            remote.setContext(FrmLiberarSeleccion.this);

            ArrayList queryDatos = new ArrayList();

            String query = "SELECT fisico, " +
                    "toma_1, " +
                    "toma_2, " +
                    "toma_3, " +
                    "usu_toma_1, " +
                    "usu_toma_2, " +
                    "usu_toma_3, " +
                    "fecha_ultima " +
                    "FROM referencias_fis f " +
                    "JOIN referencias r on r.codigo=f.codigo " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                    "WHERE f.bodega='" + usuario.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate()) ";

            if (desdeOpciones == null) {
                query += usuario.getFilterQueryForWebservice();

            } else {
                if (grupo != null) {
                    query += "AND r.grupo='" + grupo + "' ";
                }
                if (subgrupo != null) {
                    query += "AND r.subgrupo='" + subgrupo + "' ";
                }
                if (subgr2 != null) {
                    query += "AND r.subgrupo2='" + subgr2 + "' ";
                }
                if (subgr3 != null) {
                    query += "AND r.subgrupo3='" + subgr3 + "' ";
                }
                if (clase != null) {
                    query += "AND r.clase='" + clase + "'";
                }
                if (ubicacion != null) {
                    query += "AND r.ubicacion='" + ubicacion + "'";
                }
            }

            queryDatos.add(query);
            System.out.println("QUERYYYYYY///" + query);
            remote.setQuery(queryDatos);
            remote.execute();
        }
    }
}
