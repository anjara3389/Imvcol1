package com.example.imvcol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imvcol.WebserviceConnection.ExecuteRemoteQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FrmGetInfoCodigo extends AppCompatActivity {

    private EditText codigo;
    private TextView info;
    private Button btnInfo;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_get_info_codigo);
        try {
            codigo = findViewById(R.id.frm_get_info_codigo_txt_codigo);
            info = findViewById(R.id.frm_get_info_codigo_lbl_info);
            btnInfo = findViewById(R.id.frm_get_info_codigo_btn_info);
            SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
            usuario = new Usuario().selectUsuario(db);
            usuario.deleteOldSesion(FrmGetInfoCodigo.this, usuario, this.getWindow());
            BaseHelper.tryClose(db);

            btnInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getInfoReferenciaFromWebservice();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FrmGetInfoCodigo.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
            usuario = new Usuario().selectUsuario(db);
            usuario.deleteOldSesion(FrmGetInfoCodigo.this, this.usuario, this.getWindow());
            BaseHelper.tryClose(db);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FrmGetInfoCodigo.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Consulta grupo,subgr,subgr2,subgr3,clase,ubicación del código de un producto y escribe la información en un Text View.
     */
    private void getInfoReferenciaFromWebservice() {
        @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
            @Override
            public void receiveData(Object object) throws Exception {
                SQLiteDatabase db = BaseHelper.getWritable(FrmGetInfoCodigo.this);
                ArrayList resultsDatos = (ArrayList) object;

                ArrayList rawResults = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(0)), FrmGetInfoCodigo.this);
                if (resultsDatos.get(0).equals("[]")) {
                    BaseHelper.tryClose(db);
                    Toast.makeText(FrmGetInfoCodigo.this, "No se han encontrado datos de ésta referencia.", Toast.LENGTH_LONG).show();

                    info.setText("");
                } else {
                    JSONObject jsonResults = ((JSONObject) rawResults.get(0));
                    info.setText("Grupo: " + (jsonResults.isNull("grupo") ? "n/a" : jsonResults.getString("grupo")) + "\n" +
                            "Subgrupo: " + (jsonResults.isNull("subgrupo") ? "n/a" : jsonResults.getString("subgrupo")) + "\n" +
                            "Subgrupo2: " + (jsonResults.isNull("subgrupo2") ? "n/a" : jsonResults.getString("subgrupo2")) + "\n" +
                            "Subgrupo3: " + (jsonResults.isNull("subgrupo3") ? "n/a" : jsonResults.getString("subgrupo3")) + "\n" +
                            "Clase: " + (jsonResults.isNull("clase") ? "n/a" : jsonResults.getString("clase")) + "\n" +
                            "Ubicación: " + (jsonResults.isNull("ubicacion") ? "n/a" : jsonResults.getString("ubicacion")));
                }
            }
        };
        remote.init(FrmGetInfoCodigo.this, getWindow(),"Cargando");

        ArrayList queryDatos = new ArrayList();

        String query = "SELECT r.grupo,r.subgrupo,r.subgrupo2,r.subgrupo3,r.clase,f.ubicacion " +
                "FROM referencias_fis f " +
                "JOIN referencias r on r.codigo=f.codigo " +
                "JOIN v_referencias_sto s on f.codigo=s.codigo AND f.bodega=s.bodega " +
                "WHERE f.bodega='" + usuario.getCurrBodega() + "' " +
                "AND s.ano=YEAR(getdate()) " +
                "AND s.mes=MONTH(getdate()) " +
                "AND r.codigo='" + codigo.getText() + "'";

        queryDatos.add(query);
        System.out.println("QUERYYYYYY///" + query);
        remote.setQuery(queryDatos);
        remote.execute();
    }
}
