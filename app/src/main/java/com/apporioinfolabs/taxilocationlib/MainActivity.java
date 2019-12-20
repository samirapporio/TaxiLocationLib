package com.apporioinfolabs.taxilocationlib;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.apporioinfolabs.synchroniser.AtsApplication;
import com.apporioinfolabs.synchroniser.AtsEventBus;
import com.apporioinfolabs.synchroniser.AtsSocket;
import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseActivity implements  OnMapReadyCallback {


    EditText editText , edt_listen_box , log_input_edt;
    TextView fetched_text , unique_no_text, device_fetch_text, log_level_text ;
    ImageView  socket_connection_state , device_image ;
    ProgressBar device_progress_fetch ;
    LinearLayout select_log_type_layout ;
    private final String [] log_levels = {"Debug","Warning","Error","Verbose","Information"};
    private int selectedlogLevel = 0;
    private final String TAG = "MainActivity";
    private AtsSocket atsSocket ;
    private GoogleMap mMap = null;
    private Gson gson ;
    boolean SocketConnectionState = false  ;
    RequestQueue queue ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gson = new GsonBuilder().create();
        editText = findViewById(R.id.edittext);
        log_input_edt = findViewById(R.id.log_input_edt);
        edt_listen_box = findViewById(R.id.edt_listen_box);
        unique_no_text = findViewById(R.id.unique_no_text);
        fetched_text = findViewById(R.id.fetched_text);
        select_log_type_layout = findViewById(R.id.select_log_type_layout);
        log_level_text = findViewById(R.id.log_level_text);
        device_fetch_text = findViewById(R.id.device_fetch_text);
        device_image = findViewById(R.id.device_image);
        device_progress_fetch = findViewById(R.id.device_progress_fetch);


        socket_connection_state = findViewById(R.id.socket_connection_state);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        queue = Volley.newRequestQueue(this);

        atsSocket = new AtsSocket();
        setLogLevelTextAccordingToLevel();



        unique_no_text.setText("Device UUID: "+Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));





        select_log_type_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertForSelection();
            }
        });


        findViewById(R.id.phone_state).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    AtsApplication.syncPhoneState();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



        findViewById(R.id.extra_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if(editText.getText().toString().equals("")){
                 Toast.makeText(MainActivity.this, "Please Add some data to identify your device", Toast.LENGTH_SHORT).show();
             }   else{
                 AtsApplication.setExtraData(""+editText.getText().toString());
             }
            }
        });




        findViewById(R.id.ok_listen_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenToTheEnteredKey();
            }
        });


        findViewById(R.id.stop_listen_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_listen_box.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Key is not yet added", Toast.LENGTH_SHORT).show();
                }else{
                   atsSocket.stopAllListenersListen(""+edt_listen_box.getText().toString());
                }
            }
        });


        findViewById(R.id.remove_current_listener).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_listen_box.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Enter some key that you want to remove for listen. ", Toast.LENGTH_SHORT).show();
                }else{
                    atsSocket.stopListen("" + edt_listen_box.getText().toString(), new AtsSocket.OnAtsSocketListener() {
                        @Override
                        public void onMessageReceived(String message) {
                            Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccessRegistrataion(String message) {
                            Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                            edt_listen_box.setText("");
                        }

                        @Override
                        public void onFailureRegistration(String message) {
                            Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        findViewById(R.id.connected_devices_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, DeviceListActivity.class), 111);
            }
        });

        findViewById(R.id.add_log_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(log_input_edt.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Please input Some Value as log", Toast.LENGTH_SHORT).show();
                }else{
                    switch (""+log_levels[selectedlogLevel]){
                        case "Debug":
                            APPORIOLOGS.debugLog(TAG,log_input_edt.getText().toString());
                            break;
                        case "Warning":
                            APPORIOLOGS.warningLog(TAG,log_input_edt.getText().toString());
                            break;
                        case "Error":
                            APPORIOLOGS.errorLog(TAG,log_input_edt.getText().toString());
                            break;
                        case "Verbose":
                            APPORIOLOGS.verboseLog(TAG,log_input_edt.getText().toString());
                            break;
                        case "Information":
                            APPORIOLOGS.informativeLog(TAG,log_input_edt.getText().toString());
                            break;

                    }
                    log_input_edt.setText("");
                }
            }
        });


        findViewById(R.id.fl_screen_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_listen_box.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Please add device unique no to listen it ", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(MainActivity.this, FullMapViewActivity.class)
                            .putExtra("LISTENING_KEY",""+edt_listen_box.getText().toString())
                            .putExtra("SOCKET_STATE",SocketConnectionState));
                }

            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        AtsEventBus.getDefault().register(this);
        setConnectivityStatusView();
        listenToTheEnteredKey();
        checkLocationPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AtsEventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAtsMessage(String atsEvent) {
        changeSocketConnectionColor(atsEvent);
    };


    private void showAlertForSelection (){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select log Level")
                .setItems(log_levels, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedlogLevel = which ;
                        setLogLevelTextAccordingToLevel();
                    }
                }).create().show();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void setLogLevelTextAccordingToLevel(){
        log_level_text.setText(""+log_levels[selectedlogLevel]);
    }


    public void setMarkerandMapCamera(double latitude, double longitude){
        if(mMap != null){
            mMap.clear();
            LatLng coordinate = new LatLng(latitude, longitude);
            MapUtils.adddot(this,mMap, coordinate);
            mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(coordinate, 15));
        }

    }

    private void startLocationService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, UpdateServiceClass.class));
        } else { // normal
            startService(new Intent(this, UpdateServiceClass.class));
        }
    }


    private void listenToTheEnteredKey(){
        if(edt_listen_box.getText().toString().equals("")){
//            Toast.makeText(MainActivity.this, "Please add key first", Toast.LENGTH_SHORT).show();
        }else{
            atsSocket.startListen(""+edt_listen_box.getText().toString(), new AtsSocket.OnAtsSocketListener() {
                @Override
                public void onMessageReceived(String message) {
                    Log.d("*******",""+message);
                    fetched_text.setText(""+message);
                    try{
                        ModelLocationIncoming modelLocationIncoming = gson.fromJson(""+message,ModelLocationIncoming.class);
                        fetched_text.setText(""+modelLocationIncoming.getLocation().getLatitude()+", "+modelLocationIncoming.getLocation().getLongitude());
                        setMarkerandMapCamera(modelLocationIncoming.getLocation().getLatitude(), modelLocationIncoming.getLocation().getLongitude());
                    }catch (Exception e){
                        fetched_text.setText(""+message);
                    }
                }

                @Override
                public void onSuccessRegistrataion(String message) {
                    Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailureRegistration(String message) {
                    Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setConnectivityStatusView(){
        if(AtsApplication.IS_SOCKET_CONNECTED){socket_connection_state.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_socket_connected_vector));}
        else{ socket_connection_state.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_socket_disconnected_vector));}

    }

    private void changeSocketConnectionColor(String val){
        if(val.equals(""+AtsEventBus.SOCKET_CONNECTED)){
            SocketConnectionState = true ;
            socket_connection_state.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_socket_connected_vector));
        }else if(val.equals(""+AtsEventBus.SOCKET_DISCONNECTED)){
            SocketConnectionState = false ;
            socket_connection_state.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_socket_disconnected_vector));
        }
    }

    private void checkLocationPermission (){
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1122);
        }else{
            startLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1122 && grantResults[0]== 0){
            startLocationService();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 111){
            if(data != null){
                Toast.makeText(this, ""+data.getExtras().getString("MESSAGE"), Toast.LENGTH_SHORT).show();
                edt_listen_box.setText(""+data.getExtras().getString("MESSAGE"));
                listenToTheEnteredKey();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
