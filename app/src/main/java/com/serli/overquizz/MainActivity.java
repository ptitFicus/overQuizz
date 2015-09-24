package com.serli.overquizz;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.serli.overquizz.metier.ClosestThingFinder;
import com.serli.overquizz.metier.DataExtractor;
import com.serli.overquizz.metier.QuizzData;
import com.serli.overquizz.metier.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @Bind(R.id.start) Button btn_start;
    @Bind(R.id.score) ImageButton btn_score;


    private GoogleApiClient gapiClient;
    private final String fileName = "patrimoine_table.csv";
    private Session session;
    private Location lastLocation;
    List<QuizzData> fullData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        gapiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        gapiClient.connect();
        try {
            InputStream asset = getAssets().open(fileName);
            fullData = DataExtractor.getData(asset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.start)
    public void onClickOnStart() {
        session = new Session();
        session.setDatas(ClosestThingFinder.retrieveClosest(lastLocation.getLatitude(),
                lastLocation.getLongitude(), fullData));
        Intent quizz = new Intent(MainActivity.this, QuizzActivity.class);
        quizz.putExtra("session", session);
        startActivity(quizz);
    }

    @OnClick(R.id.score)
    public void onClickOnScore() {
        Intent score = new Intent(MainActivity.this, ScoreActivity.class);
        startActivity(score);
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("Google API connected");
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(gapiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("MainActivity", "Connection suspended");
        gapiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("MainActivity", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }
}
