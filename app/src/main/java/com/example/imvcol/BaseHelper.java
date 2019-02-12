package com.example.imvcol;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

public class BaseHelper extends SQLiteOpenHelper {

    private static BaseHelper sInstance;

    private static final String DATABASE_NAME = "invfiscol";

    public static final int VERSION = 11;
    String bodega = "CREATE TABLE bodega(bodega TEXT,descripcion TEXT)";
    String grupo = "CREATE TABLE grupo(grupo TEXT,descripcion TEXT)";
    String clase = "CREATE TABLE clase(clase TEXT,descripcion TEXT)";
    String usuario = "CREATE TABLE usuario(usuario TEXT,clave TEXT,curr_bodega TEXT,curr_grupo TEXT,curr_subgr TEXT,curr_subgr2 TEXT,curr_subgr3 TEXT,curr_clase TEXT,curr_conteo TEXT,modo INT,datos_enviados INT)";
    String subgrupo = "CREATE TABLE subgrupo(subgrupo TEXT, descripcion TEXT,grupo TEXT)";
    String subgrupo2 = "CREATE TABLE subgrupo2(subgrupo2 TEXT, descripcion TEXT,grupo TEXT,subgrupo TEXT)";
    String subgrupo3 = "CREATE TABLE subgrupo3(subgrupo3 TEXT, descripcion TEXT,grupo TEXT,subgrupo TEXT,subgrupo2 TEXT)";
    String producto = "CREATE TABLE producto(producto TEXT,descripcion TEXT, cantidad INTEGER,barras TEXT,grupo TEXT,subgrupo TEXT,subgr2 TEXT,subgr3 TEXT,clase TEXT)";
    String inventario = "CREATE TABLE inventario(fecha TEXT,bodega TEXT,producto TEXT,conteo1 INT,usuario1 TEXT,conteo2 INT,usuario2,conteo3 INT,usuario3)";


    private BaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION, null);
    }

    public static synchronized BaseHelper getInstance(Context context) {
        if (sInstance == null) {
            //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/invcol/";
            //File dir = new File(path);
            //dir.mkdirs();
            //context.getFilesDir().getAbsolutePath()
            sInstance = new BaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(bodega);
        db.execSQL(grupo);
        db.execSQL(subgrupo);
        db.execSQL(subgrupo2);
        db.execSQL(subgrupo3);
        db.execSQL(clase);
        db.execSQL(usuario);
        db.execSQL(producto);
        db.execSQL(inventario);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //en el caso de actualizar la tabla se la borra y se la vuelve a crear
        db.execSQL("DROP TABLE IF EXISTS BODEGA");
        db.execSQL("DROP TABLE IF EXISTS GRUPO");
        db.execSQL("DROP TABLE IF EXISTS SUBGRUPO");
        db.execSQL("DROP TABLE IF EXISTS SUBGRUPO2");
        db.execSQL("DROP TABLE IF EXISTS SUBGRUPO3");
        db.execSQL("DROP TABLE IF EXISTS CLASE");
        db.execSQL("DROP TABLE IF EXISTS USUARIO");
        db.execSQL("DROP TABLE IF EXISTS PRODUCTO");
        db.execSQL("DROP TABLE IF EXISTS INVENTARIO");
        onCreate(db);
    }

    public static SQLiteDatabase getWritable(Context ctx) {
        return getInstance(ctx).getWritableDatabase();
    }

    public static SQLiteDatabase getReadable(Context ctx) {
        return getInstance(ctx).getReadableDatabase();
    }

    public static void tryClose(SQLiteDatabase db) {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}

