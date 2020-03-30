package co.com.identico.appcarnet3.Activities;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.com.identico.appcarnet3.R;

public class InformacionActivity extends AppCompatActivity {

    private Button btnVolver;
    private TextView tvIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);
        bindUI();
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
        tvIntro.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        tvIntro.setText(Html.fromHtml(getResources().getString(R.string.informacion)));
    }

    private void bindUI(){
        btnVolver = (Button) findViewById(R.id.buttonVolver);
        tvIntro = (TextView) findViewById(R.id.tvIntro);
    }

    private void goToLogin(){

       // Intent intent = new Intent(this, LoginCarnetActivity.class);
        finish();
       // startActivity(intent);

    }
}
