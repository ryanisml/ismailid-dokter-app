package id.ismail.dokterapps;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import id.ismail.dokterapps.lib.SharedDokter;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    SharedDokter sharedPref;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mContext = this;
        sharedPref = new SharedDokter(mContext);

        int splashInterval = 1500;
        new Handler().postDelayed(() -> {
            SplashScreenActivity.this.finish();
            if (sharedPref.getSpSudahLogin()) {
                startActivity(new Intent(mContext, MainActivity.class));
            }else{
                startActivity(new Intent(mContext, LoginActivity.class));
            }
        }, splashInterval);
    }
}