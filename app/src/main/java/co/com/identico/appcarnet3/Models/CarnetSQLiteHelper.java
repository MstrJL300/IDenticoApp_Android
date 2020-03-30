package co.com.identico.appcarnet3.Models;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Miguel Forero on 26/01/2018.
 */

public class CarnetSQLiteHelper extends SQLiteOpenHelper {

    //CREATE
     String sqlCreate= "CREATE TABLE Carnets(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
            ",CarnetId TEXT" + ",IdenticoTypeLicense TEXT" + ",IdenticoEnviado TEXT" +
            ",IdenticoDescargado TEXT" + ",IdenticoConfirmado TEXT" + ",IdenticoEstado TEXT" +
            ",IdenticoCodigo TEXT" + ",IdenticoFechaInicial TEXT" + ",IdenticoFechaVencimiento TEXT" +
            ",IdenticoNombreCliente TEXT" + ",IdenticoNombreCarnet TEXT" + ",IdenticoDocumento TEXT" +
            ",IdenticoEmail TEXT" + ",IdenticoTabla TEXT" + ",Data TEXT)";

    public CarnetSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Carnets");
        db.execSQL(sqlCreate);
    }
}