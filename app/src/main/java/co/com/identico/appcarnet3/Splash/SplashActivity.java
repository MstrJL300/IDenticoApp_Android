package co.com.identico.appcarnet3.Splash;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import co.com.identico.appcarnet3.Activities.WelcomeActivity;
import co.com.identico.appcarnet3.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AsyncToast asyncToast = new AsyncToast();
        asyncToast.execute();
    }

    private class AsyncToast extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            SystemClock.sleep(2000);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent= new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}