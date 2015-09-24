package com.serli.overquizz;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.serli.overquizz.metier.QuizzData;
import com.serli.overquizz.metier.Session;

import org.w3c.dom.Text;

import butterknife.Bind;

public class QuizzActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static GoogleApiClient gapiClient;
    private Location lastLocation;
    private QuizzData data;

    @Bind(R.id.question)
    TextView question;
    @Bind(R.id.answer)
    EditText answer;
    @Bind(R.id.btn_ok)
    Button btn_ok;
    @Bind(R.id.btn_next)
    Button btn_next;
    @Bind(R.id.history)
    TextView history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Session session = (Session) getIntent().getSerializableExtra("session");

        if (gapiClient==null) {
            gapiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        data = session.getDatas().get(0).getData();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(data.getLatitude(),data.getLongitude()))
                .title(data.getName()));
    }

    @Override
    public void onConnected(Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(gapiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("QuizzActivity", "Connection suspended");
        gapiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("QuizzActivity", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }
}
