package co.com.identico.appcarnet3.Activities;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import co.com.identico.appcarnet3.Services.IdenticoRestClient;
import co.com.identico.appcarnet3.helpers.TodoHelper;

public class EstadoPopUpActivity extends AppCompatActivity {
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
    Button buttonQr, buttonInfo;
    TextView textDataCarnet;
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_pop_up);

        Button cambiar_btn = (Button) findViewById(R.id.cambiar_btn);
        final EditText editText = (EditText) findViewById(R.id.documento_et);

        cambiar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("\n " + editText.getText() + " \n"); //FLAG
            }
        });
    }
}
