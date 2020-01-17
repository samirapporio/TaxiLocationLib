package com.apporioinfolabs.taxilocationlib;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.apporioinfolabs.synchroniser.AtsApplication;
import com.apporioinfolabs.synchroniser.AtsEventBus;
import com.apporioinfolabs.synchroniser.AtsSocket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FullMapViewActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private AtsSocket atsSocket ;
    private String listening_key = "";
    private TextView listening_to_text  ;
    private Marker marker = null;
    private AtsSocket.OnAtsSocketListener mListener = new AtsSocket.OnAtsSocketListener() {
        @Override
        public void onMessageReceived(String message) {

            try{
                ModelLocationIncoming modelLocationIncoming = MainApplication.getGson().fromJson(""+message,ModelLocationIncoming.class);
                setMarkerandMapCamera(modelLocationIncoming.getLocation().getLatitude(), modelLocationIncoming.getLocation().getLongitude());
            }catch (Exception e){
//                    fetched_text.setText(""+message);
            }
        }

        @Override
        public void onSuccessRegistrataion(String message) {
            Toast.makeText(FullMapViewActivity.this, ""+message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailureRegistration(String message) {
            Toast.makeText(FullMapViewActivity.this, ""+message, Toast.LENGTH_SHORT).show();
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_map_view);
        listening_to_text  = findViewById(R.id.listening_to_text);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try{
            listening_key = getIntent().getExtras().getString("LISTENING_KEY");
            listening_to_text.setText("Listening to UUID: "+listening_key);
            changeSocketConnectionColor(AtsApplication.isSocketConnected());

        }catch (Exception e){

        }

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        atsSocket = new AtsSocket();
    }


    @Override
    protected void onResume() {
        super.onResume();
        AtsEventBus.getDefault().register(this);
        if(listening_key.equals("")){
            Toast.makeText(this, "Found No Key To Listen", Toast.LENGTH_SHORT).show();
        }else{
            atsSocket.startListen(""+listening_key, mListener);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AtsEventBus.getDefault().unregister(this);
        if(listening_key.equals("")){
            Toast.makeText(this, "Unable to remove socket listener as no key found in activity", Toast.LENGTH_SHORT).show();
        }else{
            atsSocket.stopListen("" +listening_key, new AtsSocket.OnAtsSocketListener() {
                @Override
                public void onMessageReceived(String message) {
                    Toast.makeText(FullMapViewActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccessRegistrataion(String message) {
                    Toast.makeText(FullMapViewActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailureRegistration(String message) {
                    Toast.makeText(FullMapViewActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAtsConnectionMessage(String atsEvent) {
        if(atsEvent.equals(""+AtsEventBus.SOCKET_CONNECTED)){
            changeSocketConnectionColor(true);
        }else if(atsEvent.equals(""+AtsEventBus.SOCKET_DISCONNECTED)){
            changeSocketConnectionColor(false);
        }

    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        mMap.setTrafficEnabled(true);

    }


    private void changeSocketConnectionColor(boolean value){
//        if(value){
//            socket_connection_state.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_socket_connected_vector));
//        }else {
//            socket_connection_state.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_socket_disconnected_vector));
//        }
    }


    public void setMarkerandMapCamera(double latitude, double longitude){
        if(mMap != null){
//            mMap.clear();
            LatLng coordinate = new LatLng(latitude, longitude);
            if(marker == null){
                marker = MapUtils.adddot(this,mMap, coordinate); ;
            }else{
                MapUtils.animateMarker(coordinate,mMap, marker, 0);
            }


            mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(coordinate, 15));
        }

    }


}
