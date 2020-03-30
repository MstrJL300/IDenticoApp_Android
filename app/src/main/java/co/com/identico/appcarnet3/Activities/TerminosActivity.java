package co.com.identico.appcarnet3.Activities;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.com.identico.appcarnet3.R;

public class TerminosActivity extends AppCompatActivity {
    private Button btnVolver;
    private TextView tvTerminos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos);
        bindUI();
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
        tvTerminos.setText(Html.fromHtml(getResources().getString(R.string.terminos)));
    }

    private void bindUI(){
        btnVolver = (Button) findViewById(R.id.buttonVolver);
        tvTerminos = (TextView) findViewById(R.id.tvTerminos);
    }

    private void goToLogin(){
        finish();
    }
}