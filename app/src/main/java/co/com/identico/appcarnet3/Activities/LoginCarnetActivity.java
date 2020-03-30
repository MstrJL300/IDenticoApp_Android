package co.com.identico.appcarnet3.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.com.identico.appcarnet3.Models.CarnetSQLiteHelper;
import co.com.identico.appcarnet3.R;
import co.com.identico.appcarnet3.Services.IdenticoRestClient;
import co.com.identico.appcarnet3.helpers.TodoHelper;
import cz.msebera.android.httpclient.Header;

import static android.os.Build.ID;

public class LoginCarnetActivity extends AppCompatActivity {

    private Button btnLogin;
    private TextView textView;
    private EditText editTextNit;
    private EditText editTextEmail;
    private EditText editTextCodigo;
    private CheckBox checkBoxTerminos;
    private CarnetSQLiteHelper conn;
    private SQLiteDatabase db;
    private TodoHelper todoHelper = new TodoHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_carnet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bindUI();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nit = editTextNit.getText().toString();
                String email = editTextEmail.getText().toString();
                String codigo = editTextCodigo.getText().toString();
                Boolean terminos = (checkBoxTerminos.isChecked());

                // if (!todoHelper.isOnlineNet()) {
                //  Toast.makeText(getApplicationContext(), "No tiene conexion a internet", Toast.LENGTH_LONG).show();
                //} else {
                goToMain(email, nit, codigo, terminos);
                //}
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTerminos();
            }
        });
    }

    private void bindUI() {
        btnLogin = (Button) findViewById(R.id.buttonAgregar);
        textView = (TextView) findViewById(R.id.textViewTerminos);
        editTextNit = (EditText) findViewById(R.id.editTextNit);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextCodigo = (EditText) findViewById(R.id.editCodigo);
        checkBoxTerminos = (CheckBox) findViewById(R.id.checkBoxTerminos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_carnet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_main) {
            goToMainMenu();
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

            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToMainMenu() {
        TodoHelper TH = new TodoHelper();
        if (TH.CountCarnets(this) == 0) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            finish();
            startActivity(intent);
        }
        finish();
    }

    private void goToMain(String email, String nit, String codigo, Boolean terminos) {
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Email no es valido", Toast.LENGTH_LONG).show();
        } else if (!isValidNit(nit)) {
            Toast.makeText(this, "Nit no es valido", Toast.LENGTH_LONG).show();
        } else if (!isValidCodigo(codigo)) {
            Toast.makeText(this, "Codigo de verificacion no es valido", Toast.LENGTH_LONG).show();
        } else if (!terminos) {
            Toast.makeText(this, "Aceptar terminos y condiciones", Toast.LENGTH_LONG).show();
        } else {
            if (!TodoHelper.isOnline(this)) {
                Toast.makeText(this, "Necesita conexion a internet", Toast.LENGTH_SHORT).show();
            } else {
                cargarData(email, nit, codigo);
                // 900593381/luis.vela@systemakers.com/3-742973
            }
        }
        //cargarData("luis.vela@systemakers.com", "900593381", "3-742973");
    }

    public void restartActivity() {
        TodoHelper.dismissDialog();
        TodoHelper TH = new TodoHelper();
        if (TH.CountCarnets(this) == 1) {
            Intent mIntent = getIntent();
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            finish();
        }
    }

    private void cargarData(String email, String nit, String codigo) {
        //REST
        //cargarData("luis.vela@systemakers.com", "900593381", "3-742973");
        //email = "luis.vela@systemakers.com"; nit = "900593381"; codigo = "3-742973";

        TodoHelper.showDialog(this);
        String[] partsCode = codigo.split("-");

        final String tabla = "Carnet_" + partsCode[0] + "_" + nit;

        System.out.println("\nget:\n"+"carnet/" + nit + "/" + email + "/" + codigo); //FLAG

        IdenticoRestClient.get("Carnet/" + nit + "/" + email + "/" + codigo + "", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) {

                    String rest = new String(responseBody);

                    System.out.println("\nREST:\n"+rest); //FLAG 1

                    System.out.println("\nStatus Code:\n"+statusCode); //FLAG
                    System.out.println("\nHeader:\n"+headers); //FLAG
                    System.out.println("\nResponseBody:\n"+responseBody); //FLAG

                    try {
                        JSONObject obj = new JSONObject(rest);

                        System.out.println("\nOBJ:\n"+obj); //FLAG 1

                        JSONArray array = obj.getJSONArray("Carnet");
                        String StrFull = "";
                        String Str = new String(array.getJSONObject(0).toString());
                        String DataGlobal = "";

                        String cId, iTL, iEn, iEs, iCon, iCod, iFI, iFV, iNCli, iNCar, iD, iEmail;

                        cId = array.getJSONObject(0).getString("CarnetId");
                        iTL = array.getJSONObject(0).getString("IdenticoTypeLicense");
                        iEn = array.getJSONObject(0).getString("IdenticoEnviado");
                        iEs = array.getJSONObject(0).getString("IdenticoEstado");
                        iCon = array.getJSONObject(0).getString("IdenticoConfirmado");
                        iCod = array.getJSONObject(0).getString("IdenticoCodigo");
                        iFI = array.getJSONObject(0).getString("IdenticoFechaInicial");
                        iFV = array.getJSONObject(0).getString("IdenticoFechaVencimiento");
                        iNCli = array.getJSONObject(0).getString("IdenticoNombreCliente");
                        iNCar = array.getJSONObject(0).getString("IdenticoNombreCarnet");
                        iD = array.getJSONObject(0).getString("IdenticoDocumento");
                        iEmail = array.getJSONObject(0).getString("IdenticoEmail");

                        ContentValues values = new ContentValues();
                        values.put("CarnetId", cId);                    values.put("IdenticoTypeLicense", iTL);
                        values.put("IdenticoEnviado", iEn);             values.put("IdenticoDescargado", "1");
                        values.put("IdenticoEstado", iEs);              values.put("IdenticoConfirmado", iCon);
                        values.put("IdenticoCodigo", iCod);             values.put("IdenticoFechaInicial", iFI);
                        values.put("IdenticoFechaVencimiento", iFV);    values.put("IdenticoNombreCliente", iNCli);
                        values.put("IdenticoNombreCarnet", iNCar);      values.put("IdenticoDocumento", iD);
                        values.put("IdenticoEmail", iEmail);            values.put("IdenticoTabla", tabla);

                        System.out.println("\nSTR Be:\n"+Str); //FLAG

                        Str = Str.replace("{", "");
                        Str = Str.replace("\"", "");
                        Str = Str.replace('}', ' ');

                        System.out.println("\nSTR Af:\n"+Str); //FLAG

                        String[] parts = Str.split(",");

                        System.out.println("\nPARTS:\n"+parts); //FLAG

                        for (int i = 13; i < parts.length; i++) {
                            StrFull += parts[i];
                            StrFull += '\n';

                            if (i > 12) {
                                DataGlobal += parts[i] + ",";
                            }
                        }
                        values.put("IdenticoTabla", tabla);
                        System.out.println("\nDATA GLOBAL:\n"+DataGlobal); //FLAG
                        values.put("Data", DataGlobal);
                        registrarCarnets(values);
                        restartActivity();
                    } catch (JSONException e) {
                        TodoHelper.dismissDialog();
                        e.printStackTrace();
                        //Toast.makeText(LoginCarnetActivity.this, "No se encontraron datos.", Toast.LENGTH_LONG).show();
                        Toast.makeText(LoginCarnetActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                TodoHelper.dismissDialog();
                try{
                    System.out.println("\nResponse Body:\n"+responseBody); //FLAG
                    Toast.makeText(LoginCarnetActivity.this, new String(responseBody), Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(LoginCarnetActivity.this, "Respuesta nula.", Toast.LENGTH_LONG).show();
                }
            }
        });
        //END REST
    }

    private String cadenaLimpia(String cadena) {
        String[] parts = cadena.split(":");
        return parts[1];
    }

    private void registrarCarnets(ContentValues values) {
        conn = new CarnetSQLiteHelper(this, "DB_Carnets", null, 1);
        db = conn.getWritableDatabase();
        Long idResult = db.insert("Carnets", ID, values);
        Toast.makeText(this, "Carné Agregado.", Toast.LENGTH_LONG).show();
        db.close();
    }

    private void goToTerminos() {
        Intent intent = new Intent(this, TerminosActivity.class);
        //finish();
        startActivity(intent);
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidCodigo(String codigo) {
        return codigo.length() > 4;
    }

    private boolean isValidNit(String nit) {
        return nit.length() > 4;
    }
}