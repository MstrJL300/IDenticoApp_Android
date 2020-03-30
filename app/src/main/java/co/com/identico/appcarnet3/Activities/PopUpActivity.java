package co.com.identico.appcarnet3.Activities;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.com.identico.appcarnet3.Models.Carnet;
import co.com.identico.appcarnet3.Models.CarnetSQLiteHelper;
import co.com.identico.appcarnet3.R;
import co.com.identico.appcarnet3.helpers.TodoHelper;

public class PopUpActivity extends AppCompatActivity {
    //region VALUES
    TextView vt;
    TextView DataCarnet;
    private CarnetSQLiteHelper conn;
    private SQLiteDatabase db;
    ImageView imageQr,
            imageQr2;//TEST
    LinearLayout linearLayoutQr,
            linearLayoutQr2; //TEST
    LinearLayout linearLayoutLite;
    LinearLayout linearLayoutPro;
    Button buttonQr;
    Button buttonInfo;
    TextView textDataCarnet;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
        //
        bindUI();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.widthPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * 1.2));

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        String fecha = " Hora: " + today.hour + ":" + today.minute + ":" + today.second;
        vt = (TextView) findViewById(R.id.fecha);
        DataCarnet = (TextView) findViewById(R.id.textDataCarnet);
        //vt.setText(new Date().getMinutes().toString());
        vt.setText(fecha);
        SimpleDateFormat formateador = new SimpleDateFormat(
                "MMMM dd '-' hh:mm a", new Locale("ES"));
        Date fechaDate = new Date();
        String fecha2 = formateador.format(fechaDate);
        vt.setText((fecha2.substring(0, 1).toUpperCase() + fecha2.substring(1)));
        // cargarData("migue.forero@gmail.com","80851095","1-249255");
        //recibe data
        Bundle objetoEnviado = getIntent().getExtras();
        Carnet carnet = null;

        if (objetoEnviado != null) {
            carnet = (Carnet) objetoEnviado.getSerializable("carnet");

            if (carnet.getIdenticoTypeLicense().equals("1")) {
//                linearLayoutQr.setVisibility(View.GONE);//TEST
                linearLayoutPro.setVisibility(View.GONE);
            } else {
                // textDataCarnet.setVisibility(View.GONE);
//                linearLayoutQr.setVisibility(View.GONE);//TEST
                buttonInfo.setVisibility(View.GONE); //TEST
                linearLayoutLite.setVisibility(View.GONE);
            }
            //ArreglarCadena(carnet);
            DataCarnet = (TextView) findViewById(R.id.textDataCarnet);
            DataCarnet.setText(ArreglarCadena(carnet));
        }

        //QR
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            //region QR IDENTICO DOCUMENTO
            //encode
            String text = carnet.getIdenticoDocumento();
            //Agregar "0" al documento si su longitud es menor a 10.
            if(text.length() < 10){
                int max = 10 - text.length();

                for(int i = 0; i < max; i++){
                    text = 0 + text;
                }
            }
            //end encode
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageQr.setImageBitmap(bitmap);
            //endregion
            //region QR SITIO WEB
            text = carnet.getIdenticoTabla() + ":" + carnet.getCarnetId();
            byte[] data = text.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            String urlEncode = TodoHelper.BASE_URL +"carnetUsers/Index?val=" + base64;
            //end encode
            bitMatrix = multiFormatWriter.encode(urlEncode, BarcodeFormat.QR_CODE, 200, 200);
            barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageQr2.setImageBitmap(bitmap);
            //endregion
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //END QR
    }

    private void bindUI() {
        linearLayoutQr = (LinearLayout) findViewById(R.id.linearLayoutQr);
        linearLayoutQr2 = (LinearLayout) findViewById(R.id.linearLayoutQr2); //TEST
        linearLayoutLite = (LinearLayout) findViewById(R.id.LinearLite);
        linearLayoutPro = (LinearLayout) findViewById(R.id.LinearPro);
        imageQr = (ImageView) findViewById(R.id.imageQr);
        imageQr2 = (ImageView) findViewById(R.id.imageQr2); //TEST
        buttonQr = (Button) findViewById(R.id.buttonIzquierdaQr);
        buttonInfo = (Button) findViewById(R.id.buttonIzquierdaInfo);
        textDataCarnet = (TextView) findViewById(R.id.textDataCarnet);
    }

    private String ArreglarCadena(Carnet carnet) {
        TodoHelper helper = new TodoHelper();
        String cadenaFinal = null;
        cadenaFinal = "EMAIL: \n" + carnet.getIdenticoEmail() + "\n";
        cadenaFinal += "\nDOCUMENTO: \n" + carnet.getIdenticoDocumento() + "\n";
        cadenaFinal += "\nVENCE: \n" + carnet.getIdenticoFechaVencimiento().substring(0, 10) + "\n";

        String[] array = helper.cadenaSplit(carnet.getData(), ",");
        String[] arrayPuntos;
        String[] arrayData;

        for (int i = 0; i < array.length; i++) {
            arrayPuntos = helper.cadenaSplit(array[i], ":");
            arrayData = helper.cadenaSplit(arrayPuntos[0], "_");
            if (arrayPuntos.length > 1) {
                Log.i("mike_PUA", arrayPuntos[1]);
                if (!arrayPuntos[1].contentEquals(" ")) {
                    cadenaFinal += "\n";
                    for (int j = 0; j < arrayData.length; j++) {
                        cadenaFinal += arrayData[j].toUpperCase() + " ";
                    }
                    cadenaFinal += ":";
                    if (arrayPuntos.length > 1) {
                        cadenaFinal += "\n" + arrayPuntos[1] + "\n";
                    } else {
                        cadenaFinal += "\n \n";
                    }
                }
            }
        }

        return cadenaFinal;
    }

    public void info(View view) {
        linearLayoutQr.setVisibility(View.VISIBLE); //TEST
        linearLayoutQr2.setVisibility(View.GONE);   //TEST
        buttonInfo.setVisibility(View.GONE);        //TEST
        buttonQr.setVisibility(View.VISIBLE);
        textDataCarnet.setVisibility(View.VISIBLE);
    }

    public void qr(View view) {
        textDataCarnet.setVisibility(View.GONE);
        linearLayoutQr.setVisibility(View.GONE);     //TEST
        linearLayoutQr2.setVisibility(View.VISIBLE); //TEST
        buttonQr.setVisibility(View.GONE);
        buttonInfo.setVisibility(View.VISIBLE);
    }

    public void cerrar(View view) {
        finish();
    }
}
