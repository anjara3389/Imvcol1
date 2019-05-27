package com.example.imvcol;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class FrmSelectBodega extends AppCompatActivity implements YesNoDialogFragment.MyDialogDialogListener {
    DialogUtils dialogUtils;
    private TextView txtMensaje;
    private TextView txtFechaGPS;
    private Spinner spnBodega;
    private Spinner spnModo;
    private Spinner spnTipoBodega;
    private Object[][] wholeBodegas;
    private ArrayList rawBodegas;
    private Usuario currUser;
    private String[] dataSpnTipoBodega;
    private String[] dataSpnBodegas;
    private static final int FINALIZAR_INVENTARIO = 3;

    // Time de ultima actualización de localización
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;

    //https://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/
    //https://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/
    // Apis relacionadas con localización
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    private TextView txtTipoBodega;

    // bandera booleana para cambiar la UI
    private Boolean mRequestingLocationUpdates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_select_bodega);
        try {
            //GPS
            ButterKnife.bind(this);
            // inicializa las librerías necesarias para gps

            initGPS();


            txtMensaje = findViewById(R.id.frm_select_bodega_txt_mensaje_gps);
            txtFechaGPS = findViewById(R.id.frm_select_bodega_txt_hora_gps);
            spnBodega = findViewById(R.id.frm_select_bodega_spn_bodega);

            spnModo = findViewById(R.id.frm_select_bodega_spn_modo);
            spnTipoBodega = findViewById(R.id.frm_select_bodega_spn_tipo_bodega);
            txtTipoBodega = findViewById(R.id.frm_select_bodega_lbl_tipo_bodega);

            spnTipoBodega.setVisibility(View.GONE);
            txtTipoBodega.setVisibility(View.GONE);

            Button btnSiguiente = findViewById(R.id.frm_select_bodega_btn_siguiente);


            SQLiteDatabase db = BaseHelper.getReadable(this);
            wholeBodegas = new Bodega().selectBodegas(db);
            BaseHelper.tryClose(db);
            if (wholeBodegas != null) {
                prepareSpinners();
            }

            ArrayList emptyBodega = new ArrayList();
            emptyBodega.add(wholeBodegas[0]);

            fillBodegasSpinner(null, emptyBodega);
            // reestablece los valores de instancia guardada del gps

            //restoreValuesFromBundle(savedInstanceState);

            startLocation();

            /*

            spnTipoBodega.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    filterBodegasSpinner();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });*/

            btnSiguiente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<Integer, String> mapBodega = (HashMap<Integer, String>) rawBodegas.get(1);
                    if (changeValue(mapBodega.get(spnBodega.getSelectedItemPosition())) == null) {
                        Toast.makeText(FrmSelectBodega.this, "Debe seleccionar una bodega", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            dialogUtils = new DialogUtils(FrmSelectBodega.this, "Cargando");
                            dialogUtils.showDialog(getWindow());
                            SQLiteDatabase db = BaseHelper.getReadable(getApplicationContext());
                            currUser = new Usuario().selectUsuario(db);
                            currUser.setCurrBodega(mapBodega.get(spnBodega.getSelectedItemPosition()));
                            currUser.setCurrConteo(1);
                            currUser.setModo(spnModo.getSelectedItemPosition());
                            currUser.setDatosEnviados(false);
                            currUser.updateCurrent(db);
                            db.close();
                            getWebserviceProducts();
                        } catch (Exception e) {
                            Toast.makeText(FrmSelectBodega.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    /**
     * LLena spinner de tipo de bodega y de modo hardcoded
     */
    private void prepareSpinners() {
        /*dataSpnTipoBodega = new String[4];
        dataSpnTipoBodega[0] = "Agros";
        dataSpnTipoBodega[1] = "Puntos";
        dataSpnTipoBodega[2] = "Plantas";
        dataSpnTipoBodega[3] = "Regionales";
        ArrayAdapter<String> adapterTipoBodega = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnTipoBodega);
        adapterTipoBodega.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipoBodega.setAdapter(adapterTipoBodega);*/

        String[] dataSpnModo = new String[2];
        dataSpnModo[0] = "Listado";
        dataSpnModo[1] = "Código de Barras";
        ArrayAdapter<String> adapterModo = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnModo);
        adapterModo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnModo.setAdapter(adapterModo);
    }

    /**
     * Llena el spinner de bodegas con las bodegas dadas como parámetro
     */
    private void fillBodegasSpinner(Object[][] bodegas, ArrayList arrayBodegas) {
        /*ArrayList selectedBodegas = new ArrayList();

        for (int i = 0; i < wholeBodegas.length; i++) {
            if (validarTipoBodega(wholeBodegas[i][0].toString())) {
                selectedBodegas.add(wholeBodegas[i]);
            }
        }*/
        if (bodegas != null) {
            rawBodegas = ArrayUtils.mapObjects(bodegas);
        } else {
            rawBodegas = ArrayUtils.mapObjects(arrayBodegas);
        }

        this.dataSpnBodegas = (String[]) rawBodegas.get(0);
        ArrayAdapter<String> adapterBodegas = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, dataSpnBodegas);
        adapterBodegas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBodega.setAdapter(adapterBodegas);
    }

    /*private void filterBodegasByLocation() {
        ArrayList selectedBodegas = new ArrayList();

        for (int i = 0; i < wholeBodegas.length; i++) {
            if (validarTipoBodega(wholeBodegas[i][0].toString())) {
                selectedBodegas.add(wholeBodegas[i]);
            }
        }
    }*/

    /**
     * Según el tipo de bodega seleccionado, valida si una bodega es de ese tipo de bodega.
     *
     * @param bodega bodega a validar
     * @return true si la bodega es del tipo seleccionado, false si no
     */
    /*private boolean validarTipoBodega(String bodega) {
        int bodegaInt = Integer.parseInt(bodega);
        if ((spnTipoBodega.getSelectedItemPosition() == 0 && bodegaInt > 1500 && bodegaInt < 1600) ||
                (spnTipoBodega.getSelectedItemPosition() == 1 && bodegaInt > 1200 && bodegaInt < 1300) ||
                (spnTipoBodega.getSelectedItemPosition() == 2 && bodegaInt >= 100 && bodegaInt < 400) ||
                (spnTipoBodega.getSelectedItemPosition() == 3 &&
                        ((bodegaInt >= 500 && bodegaInt < 1000) ||
                                (bodegaInt >= 1900 && bodegaInt < 1920)))) {
            return true;
        }
        return false;
    }*/

    /**
     * Da la posición en el spinner a la que corresponde el tipo de bodega de la bodega dada.
     *
     * @param bodega la bodega dada
     * @return la posición en el spinner de tipo bodega
     */
    private int getPositionSpinnerTipoBodega(String bodega) {
        int bodegaInt = Integer.parseInt(bodega);
        if (bodegaInt > 1500 && bodegaInt < 1600) {
            return 0;
        }
        if (bodegaInt > 1200 && bodegaInt < 1300) {
            return 1;
        }
        if (bodegaInt >= 100 && bodegaInt < 400) {
            return 2;
        }
        if ((bodegaInt >= 500 && bodegaInt < 1000) || (bodegaInt >= 1900 && bodegaInt < 1920)) {
            return 3;
        }
        return 0;
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
        menu.findItem(R.id.action_liberar_con_contrasenia).setVisible(false);
        //menu.findItem(R.id.action_generar_reporte).setVisible(false);
        setTitle("INVFISCOL 3.1");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_finalizar_inventario:
                YesNoDialogFragment dial2 = new YesNoDialogFragment();
                dial2.setInfo(FrmSelectBodega.this, FrmSelectBodega.this, "Finalizar inventario", "¿Está seguro de finalizar el inventario? Los datos que no se hayan enviado se perderán. ", FINALIZAR_INVENTARIO);
                dial2.show(getSupportFragmentManager(), "MyDialog");
                break;
            case R.id.action_habilitar_bodegas:
                fillBodegasSpinner(wholeBodegas, null);
                break;
            default:
                break;
        }
        return true;
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

                Intent i = new Intent(FrmSelectBodega.this, FrmLogin.class);
                startActivityForResult(i, 1);
                BaseHelper.tryClose(db);
                Toast.makeText(FrmSelectBodega.this, "El inventario finalizó exitosamente", Toast.LENGTH_LONG).show();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG);
            }
        }
    }

    private void getWebserviceProducts() throws Exception {
        if (!NetUtils.isOnlineNet(FrmSelectBodega.this)) {
            dialogUtils.dissmissDialog();
            throw new Exception("No hay conexión a internet");
        } else {
            @SuppressLint("StaticFieldLeak") ExecuteRemoteQuery remote = new ExecuteRemoteQuery() {
                @Override
                public void receiveData(Object object) throws Exception {
                    ArrayList resultsDatos = (ArrayList) object;
                    System.out.println("productos1" + resultsDatos);
                    ArrayList rawProductos = ArrayUtils.convertToArrayList(new JSONArray((String) resultsDatos.get(1)), FrmSelectBodega.this);
                    if (rawProductos.equals("[]")) {
                        dialogUtils.dissmissDialog();
                        Toast.makeText(FrmSelectBodega.this, "No se han podido cargar los datos, intente nuevamente", Toast.LENGTH_LONG).show();
                    } else {
                        fillProductsOnDatabase(resultsDatos);
                        Intent i = new Intent(FrmSelectBodega.this, FrmOpciones.class);
                        startActivityForResult(i, 1);
                        finish();
                    }
                }
            };
            remote.setContext(FrmSelectBodega.this);
            ArrayList queryDatos = new ArrayList();
            queryDatos.add("SELECT COUNT(*) AS COUNT " +
                    "FROM referencias_fis F " +
                    "JOIN referencias r on r.codigo=F.codigo " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND F.bodega=s.bodega " +
                    "LEFT JOIN referencias_alt a on r.codigo=a.codigo " +
                    "WHERE s.stock<>0 " +
                    "AND s.bodega='" + currUser.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate()) " +
                    "AND (a.cantidad_alt=1 OR a.cantidad_alt IS NULL) " +
                    "AND F.fisico=0");

            String query = "SELECT r.codigo,r.descripcion,s.stock,a.alterno,r.grupo,r.subgrupo,r.subgrupo2,r.subgrupo3,r.clase " +
                    "FROM referencias_fis F " +
                    "JOIN referencias r on r.codigo=F.codigo " +
                    "JOIN v_referencias_sto s on f.codigo=s.codigo AND F.bodega=s.bodega " +
                    "LEFT JOIN referencias_alt a on r.codigo=a.codigo " +
                    "WHERE s.stock<>0 " +
                    "AND s.bodega='" + currUser.getCurrBodega() + "' " +
                    "AND s.ano=YEAR(getdate()) " +
                    "AND s.mes=MONTH(getdate()) " +
                    "AND (a.cantidad_alt=1 OR a.cantidad_alt IS NULL) " +
                    "AND F.fisico=0";
            queryDatos.add(query);
            System.out.println("QUERYYYYYY///" + query);
            remote.setQuery(queryDatos);
            remote.execute();
        }
    }

    private void fillProductsOnDatabase(ArrayList rawResults) throws JSONException {
        SQLiteDatabase db = BaseHelper.getWritable(this);
        //System.out.println("CARGANDOOOOO" + rawResults.get(0));
        new Producto().delete(db);

        ArrayList rawArrCount = ArrayUtils.convertToArrayList(new JSONArray((String) rawResults.get(0)), this);
        ArrayList rawArrProd = ArrayUtils.convertToArrayList(new JSONArray((String) rawResults.get(1)), this);

        int count = ((JSONObject) rawArrCount.get(0)).getInt("COUNT");
        System.out.println("ESTE ES EL QUE SE GRABA COMO COUNT" + count);

        currUser.setnProductos(count);
        currUser.updateCurrent(db);

        //ArrayList rawProductos = (ArrayList) rawResults.get(1);

        for (int i = 0; i < rawArrProd.size(); i++) {
            JSONObject rawProducto = ((JSONObject) rawArrProd.get(i));
            Producto producto = new Producto(rawProducto.getString("codigo"),
                    rawProducto.getString("descripcion"),
                    rawProducto.getString("stock"),
                    rawProducto.getString("alterno"),
                    rawProducto.getString("grupo"),
                    rawProducto.getString("subgrupo"),
                    rawProducto.getString("subgrupo2"),
                    rawProducto.getString("subgrupo3"),
                    rawProducto.getString("clase"),
                    false);
            producto.insert(db);
        }
        BaseHelper.tryClose(db);
    }


    //GPS

    private void initGPS() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());

                filterBodegasByLocation();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    /**
     * Actualiza el spinner de la bodega según los datos de localización GPS
     */
    private void filterBodegasByLocation() {
        if (mCurrentLocation != null) {
            ArrayList selectedBodegas = new ArrayList();
            for (int i = 0; i < wholeBodegas.length; i++) {
                if (wholeBodegas[i][2] != null && !wholeBodegas[i][2].equals("null")) {
//1.2068505,-77.2626163
                    if (Integer.parseInt(wholeBodegas[i][0].toString()) != -1) {
                        String[] latlongBodega = wholeBodegas[i][2].toString().split(",");

                        Double distPuntos = this.getDistanceBetweenTwoPoints(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), Double.parseDouble(latlongBodega[0]), Double.parseDouble(latlongBodega[1]), "K");
                        //Double distPuntos = this.getDistanceBetweenTwoPoints(Double.parseDouble("1.226453"), Double.parseDouble("-77.268917"), Double.parseDouble(latlongBodega[0]), Double.parseDouble(latlongBodega[1]), "K");
                        System.out.println(rawBodegas);
                        System.out.println(wholeBodegas[i][2].toString() + wholeBodegas[i][1].toString() + "Dist Puntos: " + distPuntos);
                        if (distPuntos < 200) {
                            selectedBodegas.add(wholeBodegas[i]);
                        }
                    } else {
                        selectedBodegas.add(wholeBodegas[i]);
                    }
                }
            }
            fillBodegasSpinner(null, selectedBodegas);
            if (selectedBodegas.size() > 1) {
                txtMensaje.setTextColor(Color.GREEN);
                txtMensaje.setText("La sincronización GPS se ha realizado con éxito. Si no encuentra su bodega, habilite todas las bodegas desde el menú.");
                txtFechaGPS.setTextColor(Color.GREEN);
                txtFechaGPS.setText("Última sincronización GPS: " + mLastUpdateTime);
                stopLocationUpdates();
            } else {
                txtMensaje.setTextColor(Color.RED);
                txtMensaje.setText("No se ha encontrado ninguna bodega cercana. Habilite todas las bodegas desde el menú y elija CUIDADOSAMENTE la que necesita.");
                txtFechaGPS.setTextColor(Color.RED);
                txtFechaGPS.setText("Última sincronización GPS: " + mLastUpdateTime);
                //spnBodega.setEnabled(true);
                spnBodega.setSelection(0);
            }
        } else {
            localizacionGPSFallida();
        }

    }

    private void localizacionGPSFallida() {
        txtMensaje.setTextColor(Color.RED);
        txtMensaje.setText("No se puede obtener información GPS del dispositivo. Asegurese que el GPS esté encendido o habilite todas las bodegas desde el menú y elija CUIDADOSAMENTE la que necesita.");
        txtFechaGPS.setTextColor(Color.RED);
        txtFechaGPS.setText("Última sincronización GPS: " + mLastUpdateTime);
        //spnBodega.setEnabled(true);
        spnBodega.setSelection(0);
    }


    private static double getDistanceBetweenTwoPoints(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit == "K") {//metros
                dist = dist * 1.609344 * 1000;
            } else if (unit == "N") {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

    /**
     * Inicializa la actualización de localización
     * Revisa si la configuración de locación son correctas
     * Después se pedirá por actualizacines de localización
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        System.out.println("Todas las configuraciones de localización han sido activadas");
                        //dialogUtils = new DialogUtils(FrmSelectBodega.this, "Sincronización GPS");
                        //dialogUtils.showDialog(getWindow());

                        //Toast.makeText(getApplicationContext(), "Inicia la actualización de la localización!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        filterBodegasByLocation();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                //dialogUtils.dissmissDialog();
                                Toast.makeText(getApplicationContext(), "Error: Configuraciòn GPS incorrecta.", Toast.LENGTH_SHORT).show();
                                System.out.println("Configuraciòn GPS incorrecta.");
                                localizacionGPSFallida();
                                try {
                                    // Muestra el dialogo llamando startResolutionForResult()
                                    // y verificando el resultado en onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(FrmSelectBodega.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Toast.makeText(getApplicationContext(), "Error: PendingIntent incapaz de ejecutar la petición.", Toast.LENGTH_SHORT).show();
                                    System.out.println(("PendingIntent unable to execute request."));
                                    localizacionGPSFallida();
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Error: Configuración GPS incorrecta. Encienda el GPS en su dispositivo.";
                                System.out.println(errorMessage);
                                localizacionGPSFallida();
                                // dialogUtils.dissmissDialog();

                                Toast.makeText(FrmSelectBodega.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }

                        filterBodegasByLocation();
                    }
                });

    }

    /**
     * Inicia localizaciòn (ON CLICK)
     */
    public void startLocation() {
        // Pide ACCESS_FINE_LOCATION usando Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // abre la confguraciòn del dispositivo cuando los permisos son negados permanentemente
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /**
     * Para la actualización de la localización
     */
    public void stopLocationUpdates() {
        mRequestingLocationUpdates = false;
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(getApplicationContext(), "Acualizaciones de localización no disponibles!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Verifica el codigo entero dado a startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        System.out.println("Usuario acepta hacer cambios en la configuración de localización.");
                        break;
                    case Activity.RESULT_CANCELED:
                        System.out.println("Usuario rechaza hacer cambios en la configuración de localización.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Continúa con las actualizaciones de locaciòn dependiendo de los permisos autorizados.
       /* if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        startLocationUpdates();*/
        startLocation();

        //updateLocationUI();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausa las actualizaciones de localización
            stopLocationUpdates();
        }
    }
}
