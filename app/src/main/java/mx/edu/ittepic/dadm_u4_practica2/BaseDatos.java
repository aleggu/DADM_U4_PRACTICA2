package mx.edu.ittepic.dadm_u4_practica2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatos extends SQLiteOpenHelper {
    public BaseDatos(Context context,String name,SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE PROPIETARIO (IDP INTEGER PRIMARY KEY NOT NULL, NOMBRE VARCHAR(200), DOMICILIO VARCHAR (500), TELEFONO VARCHAR(50))");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE INMUEBLE (ID_INMUEBLE INTEGER PRIMARY KEY NOT NULL, DOMICILIO VARCHAR(200), PRECIOVENTA FLOAT, PRECIORENTA FLOAT, FECHA VARCHA(15), IDP INTEGER, FOREIGN KEY (IDP) REFERENCES PROPIETARIO (IDP))");

    }
}
