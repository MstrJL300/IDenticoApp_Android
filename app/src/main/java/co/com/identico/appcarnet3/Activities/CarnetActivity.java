package co.com.identico.appcarnet3.Activities;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import co.com.identico.appcarnet3.Models.Carnet;
import co.com.identico.appcarnet3.Models.CarnetSQLiteHelper;
import co.com.identico.appcarnet3.R;
import co.com.identico.appcarnet3.Services.IdenticoRestClient;
import co.com.identico.appcarnet3.helpers.TodoHelper;
import cz.msebera.android.httpclient.Header;

public class CarnetActivity extends AppCompatActivity {
    //region VALORES
    public String nameFoto, nF_Lateral, nF_Frontal;
    private ImageView fotoF, fotoL;
    private Button boton;                             //Boton "Confirmar".
    private FloatingActionButton girar_boton,         //Boton para cambiar la imagen de frontal a lateral.
            recargar_boton;    //Boton para llamar la imagen en línea.
    private CarnetSQLiteHelper conn;
    private SQLiteDatabase db;
    int menu_camera;

    //VALORES DE ANIMACIÓN
    private View mCardFrontLayout, mCardBackLayout;     //Vistas.
    private AnimatorSet mSetRightOut, mSetLeftIn;       //Animación.
    private boolean mIsBackVisible = false;             //Determina si la imagen es visible.
    //endregion

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_carnet);

        //Evita que tome fotos del código QR.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bindUI();

        Bundle objetoEnviado = getIntent().getExtras();
        Carnet carnet = null;

        if (objetoEnviado != null) {
            carnet = (Carnet) objetoEnviado.getSerializable("carnet");
            final Carnet finalCarnet = carnet;
            final String nit[] = carnet.getIdenticoTabla().split("_");
            if (finalCarnet.getIdenticoEstado().equals("1")) {
//                System.out.println("Equals 1"); //FLAG
                boton.setVisibility(View.GONE);
            }
            else {
//                System.out.println("Equals 0"); //FLAG
                boton.setVisibility(View.VISIBLE);
                boton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { Confirmar_SET(finalCarnet, nit); }
                });
            }

            final boolean[] tf_redo = {true};

            nameFoto = "Identico_" + carnet.getID();
            nF_Frontal = nameFoto;  nF_Lateral = nameFoto+"-1";

//            System.out.println("|| Names:\n"+"nF_Frontal: "+nF_Frontal+"\n"+"nF_Lateral: "+ nF_Lateral); //FLAG

            File sd = new File(getFilesDir().getPath() + "/");
            File folder = new File(sd, nF_Frontal + ".jpg");

            String urlF = IdenticoRestClient.BASE_URL + "Documento/" + carnet.getIdenticoTabla() +
                    "/" + carnet.getIdenticoDocumento() + ".jpg";

            //Ponemos Listeners para foto Frontal y Lateral.
            fotoF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup();
                }
            });
            fotoL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup();
                }
            });

            //LOCAL
            //Buscamos imagen frontal en los archivos locales y asignamos la imagen.
            if (folder.exists()) {
                Cargar_Folder(folder, fotoF, finalCarnet);
            }else {
                Guardar_Foto_URL(urlF, fotoF, finalCarnet);
            }

//            Cargar_Foto_URL(urlF, fotoF, false); //NEW

            findViews();
            loadAnimations();
            changeCameraDistance();

            //Boton para llamar la animación que cambia la imagen del carne a frontal o latertal.
            final Carnet finalCarnet1 = carnet; //TEMP
            girar_boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tf_redo[0]){
                        String urlL = IdenticoRestClient.BASE_URL + "Documento/" + finalCarnet1.getIdenticoTabla() +
                                "/" + finalCarnet1.getIdenticoDocumento() + "-1.jpg";

                        File sd = new File(getFilesDir().getPath() + "/");
                        File folder = new File(sd, nF_Lateral + ".jpg");

                        nameFoto = nF_Lateral; //Cambiamos el nombre para "Cargar_Folder".
                        //LOCAL
                        //Buscamos imagen lateral en los archivos locales y asignamos la imagen.
                        if (folder.exists()) {
                            System.out.println("OTRA VEZ - FOLDER"); //FLAG
                            Cargar_Folder(folder, fotoL, finalCarnet);
                        }
                        else {
                            System.out.println("OTRA VEZ - URL");
                            Guardar_Foto_URL(urlL, fotoL, finalCarnet);
                        }
                        tf_redo[0] = false;
                    }
                    flipCard(v);
                }
            });

            recargar_boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mIsBackVisible){
                        String urlL = IdenticoRestClient.BASE_URL + "Documento/" + finalCarnet1.getIdenticoTabla() +
                                "/" + finalCarnet1.getIdenticoDocumento() + "-1.jpg";
                        nameFoto = nF_Lateral; //Cambiamos el nombre para "Cargar_Folder".
                        Cargar_Foto_URL(urlL, fotoL, finalCarnet, true);
                    }
                    else{
                        String urlF = IdenticoRestClient.BASE_URL + "Documento/" + finalCarnet1.getIdenticoTabla() +
                                "/" + finalCarnet1.getIdenticoDocumento() + ".jpg";
                        nameFoto = nF_Frontal; //Cambiamos el nombre para "Cargar_Folder".
                        Cargar_Foto_URL(urlF, fotoF, finalCarnet, true);
                    }
                }
            });
        }
//        boton.setVisibility(View.GONE);
    }

    private void Confirmar_SET(final Carnet finalCarnet, final String nit[]){
        if (!TodoHelper.isOnline(CarnetActivity.this)) {
            Toast.makeText(CarnetActivity.this,
                    "Necesita conexion a internet", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    CarnetActivity.this);

            builder.setMessage("Confirma la informacion del carnet?");

            builder.setTitle("Alerta de Confirmacion");

            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    apiConfirmar(finalCarnet.getIdenticoEmail(), nit[2],
                            nit[1] + "-" + finalCarnet.getIdenticoCodigo(),
                            "si", finalCarnet.getCarnetId(),
                            finalCarnet.getID());
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    apiConfirmar(finalCarnet.getIdenticoEmail(), nit[2],
                            nit[1] + "-" + finalCarnet.getIdenticoCodigo(),
                            "no", finalCarnet.getCarnetId(),
                            finalCarnet.getID());
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //Carga la imagen localmente.
    public void Cargar_Folder(File folder, ImageView foto, Carnet carnet){
        Log.i("mike", "Existe foto" + folder);//FLAG
        Picasso.get().load("file:///" + getFilesDir().getPath() + "/" + nameFoto + ".jpg").into(foto);

        if (carnet.getIdenticoEstado().equals("0")) {
            boton.setVisibility(View.VISIBLE);
        }
    }

    //Cargar el URL de la imagen en "foto".
    public void Cargar_Foto_URL(String currentUrl, ImageView foto, Carnet carnet, final boolean imgTf){
        if(menu_camera == 0){
            menu_camera = R.drawable.ic_menu_camera;    //Primera asignación.
        }

        Log.i("mike", "No existe foto\n"+currentUrl); //FLAG

        final Carnet finalCarnet = carnet;

        Picasso.get()
                .load(currentUrl)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .error(menu_camera)
                .into(foto, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        if(imgTf) {
                            Toast.makeText(CarnetActivity.this, "Imagen Actualizada.", Toast.LENGTH_LONG).show(); //NEW
                            if (finalCarnet.getIdenticoEstado().equals("0")) {
                                boton.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(CarnetActivity.this, "Error Actualizando la Imagen.", Toast.LENGTH_LONG).show(); //NEW
                        boton.setVisibility(View.GONE);
                    }
                });

        Log.i("mike", "INVALIDATE file:///" + getFilesDir().getPath() + "/" + nameFoto + ".jpg");//FLAG
        Picasso.get().invalidate("file:///" + getFilesDir().getPath() + "/" + nameFoto + ".jpg"); //TEST

        Picasso.get()   //Una vez se carga la foto se guarda en el directorio.
                .load(currentUrl)
                .into(target);
    }

    //Guarda el URL de la imagen en "foto".
    public void Guardar_Foto_URL(String currentUrl, ImageView foto, Carnet carnet) {
        if (menu_camera == 0) {
            menu_camera = R.drawable.ic_menu_camera;    //Primera asignación.
        }

        Log.i("mike", "No existe foto\n" + currentUrl);//FLAG

        final Carnet finalCarnet = carnet;

        Picasso.get()
                .load(currentUrl)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .error(menu_camera)
                .into(foto, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        if (finalCarnet.getIdenticoEstado().equals("0")) {
                            boton.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(CarnetActivity.this, "Error guardando la imagen.", Toast.LENGTH_LONG).show(); //NEW
                    }
                });

        Picasso.get()   //Una vez se carga la foto se guarda en el directorio.
                .load(currentUrl)
                .into(target);
    }

    private void apiConfirmar(String email, String nit, String codigo, final String confirmado, final String carnetId, final int Id) {
        RequestParams params = new RequestParams();
        params.add("nit", nit);
        params.add("email", email);
        params.add("codigo", codigo);
        params.add("confirmado", confirmado);
        params.add("carnetid", carnetId);
        IdenticoRestClient.put("CarnetConfirmado/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) {
                    String rest = new String(responseBody);
                    conn = new CarnetSQLiteHelper(CarnetActivity.this, "DB_Carnets", null, 1);
                    db = conn.getWritableDatabase();
                    if (confirmado == "si") {
                        db.execSQL("UPDATE Carnets SET  IdenticoEstado='1',IdenticoConfirmado='1'  WHERE ID=" + Id + "");
                        db.close();
                        boton.setVisibility(View.GONE);
                        Toast.makeText(CarnetActivity.this, "Carné Confirmado", Toast.LENGTH_LONG).show();
                    } else {
                        db.execSQL("DELETE FROM Carnets WHERE  ID=" + Id + "");
                        db.close();
                        Toast.makeText(CarnetActivity.this, "Carné devuelto para confirmación " +
                                        "de la información, acérquese al área encargada de la empresa.",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CarnetActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                        File file = new File(getFilesDir().getPath() + "/" + nF_Frontal + ".jpg");
                        file.delete();
                        file = new File(getFilesDir().getPath() + "/" + nF_Lateral + ".jpg");
                        file.delete();
                        finish();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(CarnetActivity.this, new String(responseBody), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        //END REST
    }

    public void alert(final int ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Carné Vencido, Desea borrarlo?");
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

    //region TARGET
/////////////
/////Target de picassso para manejo de foto SAVE
/////////////
    public Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(getFilesDir().getPath() + "/" + nameFoto + ".jpg");
                    Log.i("mike", "Target "+file.getPath().toString());//FLAG
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                        ostream.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

//        @Override
//        public void onBitmapFailed(Drawable errorDrawable) {
//        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if (placeHolderDrawable != null) {
            }
        }
    };
    /////////////
    /////END Target de picassso para manejo de foto SAVE
    /////////////
    //endregion

    private void bindUI() {
        boton = (Button) findViewById(R.id.buttonConf);
        fotoF = (ImageView) findViewById(R.id.carnet_frontal);
        fotoL = (ImageView) findViewById(R.id.carnet_lateral);
        girar_boton = (FloatingActionButton) findViewById(R.id.flip_button);
        recargar_boton = (FloatingActionButton) findViewById(R.id.photo_button); //NEW
    }

    private void popup() {
        //Recibir data obj
        Bundle objetoEnviado = getIntent().getExtras();
        Carnet carnet = null;

        if (objetoEnviado != null) {
            carnet = (Carnet) objetoEnviado.getSerializable("carnet");
        }
        //end
        Intent intent = new Intent(this, PopUpActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("carnet", carnet);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //TEST
    public void EstadoPopup() {
        Intent intent = new Intent(this, EstadoPopUpActivity.class);
        startActivity(intent);
    }
    //TEST

    //region MENU
    ///////////////////////////
    //Parte del menu
    ///////////////////////////////
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
            goToMain();
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
        //TEST
        else if (id == R.id.action_change_db) {
            System.out.println("\n CHANGE DB \n"); //FLAG
            EstadoPopup();
//            conn = new CarnetSQLiteHelper(CarnetActivity.this, "DB_Carnets", null, 1);
//            db = conn.getWritableDatabase();
//            db.execSQL("UPDATE Carnets SET  IdenticoEstado='1',IdenticoConfirmado='1'  WHERE ID=" + Id + "");
//            db.close();

            return true;
        }
        //TEST
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

    private void goToMain() {
        finish();
    }
    //TEST
    ///////////////////////////
    //End Parte del menu
    ///////////////////////////////
    //endregion

    //FUNCIONES DE ANIMACIÓN
    //Asignar valores de las vistas.
    private void findViews() {
        mCardFrontLayout = findViewById(R.id.carnet_fl_frontal);
        mCardBackLayout = findViewById(R.id.carnet_fl_lateral);
    }

    //Asignar valores de las animación.
    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_animation);
    }

    //Asignar valores de la camara de las vistas.
    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        mCardFrontLayout.setCameraDistance(scale);
        mCardBackLayout.setCameraDistance(scale);
    }

    //Llamada de la animación.
    public void flipCard(View view) {
//        System.out.println("\nmIsBackVisible | " + mIsBackVisible + "\n"); //FLAG
        if (!mIsBackVisible) {
            mSetRightOut.setTarget(mCardFrontLayout);
            mSetLeftIn.setTarget(mCardBackLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = true;
        } else {
            mSetRightOut.setTarget(mCardBackLayout);
            mSetLeftIn.setTarget(mCardFrontLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = false;
        }
    }
}

//CARD FLIP SOURCE: https://www.thedroidsonroids.com/blog/how-to-add-card-flip-animation-to-your-android-app