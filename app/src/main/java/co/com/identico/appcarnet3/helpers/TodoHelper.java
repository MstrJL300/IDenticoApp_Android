package co.com.identico.appcarnet3.helpers;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import co.com.identico.appcarnet3.Models.CarnetSQLiteHelper;
import co.com.identico.appcarnet3.R;

/**
 * Created by Miguel Forero on 27/01/2018.
 */

public class TodoHelper {
    //Esta dirección actualmente apunta al servidor que contiene la base de datos "184.168.194.55" (Eventualmente va a cambiar el servidor al que apunta. 17/05/19
    public static final String BASE_URL = "http://app.identico.com.co/";
//    public static final String BASE_URL = "http://181.48.173.68/";
//    public static final String BASE_URL = "http://192.168.100.117/";
    private CarnetSQLiteHelper conn;
    private SQLiteDatabase db;
    private static ProgressDialog progress;

    public Boolean isOnlineNet() {
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 www.google.es");
            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();        }
        return false;
    }

    public String domainUrl() {return BASE_URL;}
//    public String domainUrl() {return "http://181.48.173.68/";}
//    public String domainUrl() {return "http://192.168.100.20/";}
//    public String domainUrl() {
//        return "ID-CO-MS";
//    }

    /* public String cadenaLimpia(String cadena) {
         String[] parts = cadena.split(":");
         return parts[1];
     }
 */
    public String[] cadenaSplit(String cadena, String splitDato) {
        String[] array = cadena.split(splitDato);
        return array;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public int CountCarnets(Context context) {
        conn = new CarnetSQLiteHelper(context, "DB_Carnets", null, 1);
        db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Carnets", null);
        return cursor.getCount();
    }

    public static void showDialog(Context context) {
        progress = new ProgressDialog(context);
        progress.setTitle(context.getString(R.string.label_loading_tittle));
        progress.setMessage(context.getString(R.string.label_loading_message));
        progress.setCancelable(true);
        progress.show();
    }

    public static void dismissDialog() {
        if (progress != null) {
            progress.dismiss();
            progress.hide();
            progress = null;
        }
    }
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
}