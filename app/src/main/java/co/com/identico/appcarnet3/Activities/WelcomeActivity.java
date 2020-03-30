package co.com.identico.appcarnet3.Activities;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.com.identico.appcarnet3.R;
import co.com.identico.appcarnet3.helpers.TodoHelper;

public class WelcomeActivity extends AppCompatActivity {
    private Button btnVolver;
    private TextView tvIntro;
    private static final int REQUEST_CODE = 1;
    private static final String[] PERMISOS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int leer = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int leer1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (leer == PackageManager.PERMISSION_DENIED || leer1 == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, PERMISOS, REQUEST_CODE);
        }

        TodoHelper TH= new TodoHelper();
        if(TH.CountCarnets(this)!=0){
            Intent intent= new Intent(this, MainActivity.class);
             startActivity(intent); finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        bindUI();
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
        tvIntro.setText(Html.fromHtml(getResources().getString(R.string.welcome)));
    }

    private void bindUI(){
        btnVolver = (Button) findViewById(R.id.buttonVolver);
        tvIntro = (TextView) findViewById(R.id.tvIntro);
    }

    private void goToLogin(){
        Intent intent = new Intent(this, LoginCarnetActivity.class);
        finish();
        startActivity(intent);
    }
}
