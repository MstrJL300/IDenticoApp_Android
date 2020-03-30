package co.com.identico.appcarnet3.Activities;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import co.com.identico.appcarnet3.Models.Carnet;
import co.com.identico.appcarnet3.Models.CarnetSQLiteHelper;
import co.com.identico.appcarnet3.R;
import co.com.identico.appcarnet3.Services.IdenticoRestClient;
import co.com.identico.appcarnet3.helpers.TodoHelper;
import cz.msebera.android.httpclient.Header;

import static android.os.Build.ID;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<String> names;
    private CarnetSQLiteHelper conn;
    private SQLiteDatabase db;
    private ArrayList<Carnet> Carnets;
    private ArrayAdapter<String> adapter;
    private List<Carnet> listaCarnets;
    private ArrayList<String> listaInformacion;
    private AdView mAdView; //Valor Ad.

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //region CÓDIGO AD
        // Sample AdMob app ID:   ca-app-pub-3940256099942544~3347511713
        // IDéntico AdMob app ID: ca-app-pub-4054881126615976~9705858305
        //                        ca-app-pub-4054881126615976/8692159535
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        // Find Banner ad
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        // Display Banner ad
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                System.out.println("Loaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Se encontro un error", Toast.LENGTH_SHORT).show();
            }
        });
        //endregion
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);

        //registrarCarnets();
        consultarListaCarnets();

        ArrayAdapter adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaCarnets);
        listView.setAdapter(adaptador);

        MyAdapter myAdapter = new MyAdapter(this, R.layout.list_item, listaCarnets);
        listView.setAdapter(myAdapter);

        //region List View Click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String informacion = "ida:" + listaCarnets.get(position).getID() + "/n"
                        + listaCarnets.get(position).getIdenticoNombreCarnet();
                informacion += "ids:" + listaCarnets.get(position).getIdenticoNombreCarnet() + "/n";
                //  Toast.makeText(MainActivity.this, informacion, Toast.LENGTH_SHORT).show();

                Carnet carnet = listaCarnets.get(position);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
                String dateInString = listaCarnets.get(position).getIdenticoFechaVencimiento().substring(0, 10);
                int tempYear = Integer.parseInt(dateInString.substring(0, 4));
                int tempMonth = Integer.parseInt(dateInString.substring(5, 7));
                int tempDay = Integer.parseInt(dateInString.substring(8, 10));
                Calendar C = new GregorianCalendar(tempYear, tempMonth - 1, tempDay + 1);
                Date DD = C.getTime();

                if (new Date().before(DD)) {
                    if (listaCarnets.get(position).getIdenticoTypeLicense().equals("1")) {
                        Intent intent = new Intent(MainActivity.this, CarnetActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("carnet", carnet);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        if (!TodoHelper.isOnline(MainActivity.this)) {
                            Toast.makeText(MainActivity.this, "Necesita conexión a internet", Toast.LENGTH_SHORT).show();
                        } else {
                            confirmCarnetVigente(listaCarnets.get(position));
                        }
                    }
                } else {
                    alert(listaCarnets.get(position).getID());
                    // Toast.makeText(MainActivity.this, "Carnet Vencido, Desea borrarlo?", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //endregion

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginCarnet();
            }
        });
    }

    private void confirmCarnetVigente(final Carnet carnet) {
        RequestParams params = new RequestParams();
        final String nit[] = carnet.getIdenticoTabla().split("_");
        params.add("nit", nit[2]);
        params.add("email", carnet.getIdenticoEmail());
        params.add("codigo", nit[1] + "-" + carnet.getIdenticoCodigo());
        //   params.add("FechaVencimiento",  carnet.getIdenticoFechaVencimiento());
        final Boolean[] flag = {true};

        IdenticoRestClient.post("CarnetState/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) {
                    String rest = new String(responseBody);
                    if (rest.contains("Desactivo")) {
                        alertDesactivado(carnet.getID());
                    } else {
                        Intent intent = new Intent(MainActivity.this, CarnetActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("carnet", carnet);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, new String(responseBody), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void alert(final int ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Carnet Vencido, Desea borrarlo?");
        builder.setTitle("Alerta");
        // Add the buttons
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                borrarCarnets(ID);
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void alertDesactivado(final int ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Carnet Desactivado, Desea borrarlo?");
        builder.setTitle("Alerta");
        // Add the buttons
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                borrarCarnets(ID);


            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void borrarCarnets(int ID) {
        conn = new CarnetSQLiteHelper(this, "DB_Carnets", null, 1);
        db = conn.getWritableDatabase();
        db.execSQL("DELETE FROM Carnets WHERE ID=" + ID + "");
        db.close();
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void consultarListaCarnets() {
        conn = new CarnetSQLiteHelper(this, "DB_Carnets", null, 1);
        db = conn.getReadableDatabase();
        Carnet carnet = null;
        listaCarnets = new ArrayList<Carnet>();

        Cursor cursor = db.rawQuery("select * from Carnets", null);
        while (cursor.moveToNext()) {
            carnet = new Carnet();  carnet.setID(cursor.getInt(0));
            carnet.setIdenticoCodigo(cursor.getString(7));
            carnet.setIdenticoEstado(cursor.getString(6));
            carnet.setIdenticoTypeLicense(cursor.getString(2));
            carnet.setCarnetId(cursor.getString(1));
            carnet.setIdenticoNombreCliente(cursor.getString(10));
            carnet.setIdenticoNombreCarnet(cursor.getString(11));
            carnet.setIdenticoDocumento(cursor.getString(12));
            carnet.setIdenticoEmail(cursor.getString(13));
            carnet.setIdenticoTabla(cursor.getString(14));
            carnet.setData(cursor.getString(15));
            carnet.setIdenticoConfirmado(cursor.getString(5));
            carnet.setIdenticoFechaInicial(cursor.getString(8));
            carnet.setIdenticoFechaVencimiento(cursor.getString(9));
            listaCarnets.add(carnet);
        }
        obtenerLista();
        db.close();
    }

    private void obtenerLista() {
        listaInformacion = new ArrayList<String>();
        for (int i = 0; i < listaCarnets.size(); i++) {
            listaInformacion.add(listaCarnets.get(i).getID() + " - "
                    + listaCarnets.get(i).getData());
        }
    }

    private void registrarCarnets() {
        conn = new CarnetSQLiteHelper(this, "DB_Carnets", null, 1);
        db = conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CarnetId", "aaa");
        values.put("IdenticoEstado", "sss12");
        values.put("Data", "sss12");
        Long idResult = db.insert("Carnets", ID, values);
        Toast.makeText(this, "Id result" + idResult, Toast.LENGTH_LONG).show();
        db.close();
    }

    private void goToLoginCarnet() {
        //finish();
        Intent intent = new Intent(this, LoginCarnetActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_carnet) {
            goToLoginCarnet();
            return true;
        }
        else if (id == R.id.action_qr_scan) {
            Intent intent = new Intent(this, EscanearQRActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_info) {
            Intent intent = new Intent(this, InformacionActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_logout) {
            //finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}