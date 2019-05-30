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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_liberar_seleccion);

        try {
            contrasenia = findViewById(R.id.frm_liberar_seleccion_contrasenia);
            aceptar = findViewById(R.id.frm_liberar_seleccion_btn_aceptar);


            SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
            usuario = new Usuario().selectUsuario(db);
            BaseHelper.tryClose(db);

            aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(contrasenia.getText());
                    if (contrasenia.getText().toString().equals("inv48")) {
                        try {
                            dialogUtils = new DialogUtils(FrmLiberarSeleccion.this, "Cargando");
                            dialogUtils.showDialog(FrmLiberarSeleccion.this.getWindow());
                            FrmLiberarSeleccion.this.freeWebserviceFisicosTotal();
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
            String query = "UPDATE F SET fisico=0, " +
                    "toma_1=NULL, " +
                    "toma_2=NULL, " +
                    "toma_3=NULL, " +
                    "usu_toma_1=NULL, " +
                    "usu_toma_2=NULL, " +
                    "usu_toma_3=NULL, " +
                    "fecha_ultima=NULL " +
                    "FROM referencias_fis F " +
                    "JOIN referencias r on r.codigo=F.codigo " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                    "WHERE F.bodega='" + usuario.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate()) " +
                    "AND f.fisico=1  ";

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
                        dialogUtils.dissmissDialog();
                        BaseHelper.tryClose(db);
                        Toast.makeText(FrmLiberarSeleccion.this, "No se han podido liberar selección, intente nuevamente", Toast.LENGTH_LONG).show();
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
    }
}
