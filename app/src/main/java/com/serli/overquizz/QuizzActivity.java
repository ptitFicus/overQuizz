package com.serli.overquizz;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.serli.overquizz.metier.QuizzData;
import com.serli.overquizz.metier.QuizzDataDistanceWrapper;
import com.serli.overquizz.metier.Session;

import org.w3c.dom.Text;

import java.util.LinkedList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizzActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static GoogleApiClient gapiClient;
    private Location lastLocation;
    private QuizzData data;
    private Session session;
    private GoogleMap map;

    @Bind(R.id.question)
    TextView question;
    @Bind(R.id.description)
    TextView description;
    @Bind(R.id.answer)
    EditText answer;
    @Bind(R.id.btn_ok)
    Button btn_ok;
    @Bind(R.id.btn_next)
    Button btn_next;
    @Bind(R.id.history)
    TextView history;
    @Bind(R.id.btn_quit)
    Button btn_quit;
    @Bind(R.id.vrai)
    TextView vrai;
    @Bind(R.id.faux)
    TextView faux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        ButterKnife.bind(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        session = (Session) getIntent().getSerializableExtra("session");

        if (gapiClient==null) {
            gapiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        gapiClient.connect();

        setNextQuestion();

    }

    @OnClick(R.id.btn_ok)
    public void onClickOnOk() {
        checkAnswer(answer.getText().toString());
        history.setVisibility(View.VISIBLE);
    }

    private void checkAnswer(String text) {
        Integer answer;
        if (text!=null || !text.equals("")) {
            answer = Integer.parseInt(text);
        } else {
            answer = 0;
        }
        if (answer<=data.getAnswer().getEndYear() && answer>=data.getAnswer().getStartYear()) {
            session.addGoodAnswer();
            vrai.setVisibility(View.VISIBLE);
        } else {
            faux.setVisibility(View.VISIBLE);
        }
        session.addAnsweredQuestion(data);
    }

    @OnClick(R.id.btn_next)
    public void onClickOnNext() {
        System.out.println("ok next");
        setNextQuestion();
    }

    private void setNextQuestion() {
        if(session.getDatas().size()>0) {
            session.getDatas().remove(0);
            data = session.getDatas().get(0).getData();
            history.setVisibility(View.GONE);
            vrai.setVisibility(View.GONE);
            faux.setVisibility(View.GONE);
            history.setText(data.getHistoric());
            question.setText("Estimez l'année de construction de " + data.getName());
            description.setText("Description : " + data.getDescription());
            if (map != null) {
                LatLng position = new LatLng(data.getLatitude(), data.getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(position)
                        .title(data.getName()));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,14));
            }
        }else {
            quit();
        }
    }

    @OnClick(R.id.btn_quit)
    public void onClickOnQuit() {
        quit();
    }

    private void quit() {
        Intent score = new Intent(QuizzActivity.this, ScoreActivity.class);
        session.setDatas(new LinkedList<QuizzDataDistanceWrapper>());
        score.putExtra("session", session);
        startActivity(score);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
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
