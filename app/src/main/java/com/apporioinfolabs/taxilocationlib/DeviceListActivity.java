package com.apporioinfolabs.taxilocationlib;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mindorks.placeholderview.PlaceHolderView;

import java.util.List;

public class DeviceListActivity extends Activity implements HolderDevices.OnClickListener{

    PlaceHolderView placeholder ;
    ProgressBar progress_circular ;
    LinearLayout retry_layout ;
    TextView problem_text , retry_txt_btn;
    private Gson gson ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        gson = new GsonBuilder().create();
        placeholder = findViewById(R.id.placeholder);
        progress_circular = findViewById(R.id.progress_circular);
        retry_layout = findViewById(R.id.retry_layout);
        problem_text = findViewById(R.id.problem_text);
        retry_txt_btn = findViewById(R.id.retry_txt_btn);

        try{placeholder.removeAllViews();}catch (Exception e){}
        fetchDevices();

    }



    private void setitemsInPlaceHolder(List<ModelConnectedSockets.ConnectedSocketsBean> connected_sockets){
        for(int i =0 ; i < connected_sockets.size() ; i++){
            placeholder.addView(new HolderDevices(this, this, connected_sockets.get(i)));
        }
    }


    private void fetchDevices (){
        placeholder.setVisibility(View.GONE);
        retry_layout.setVisibility(View.GONE);
        progress_circular.setVisibility(View.VISIBLE);

        AndroidNetworking.get(""+Endpoints.GET_ALL_CONNECTED_DEVICES).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {

                try{
                    ModelConnectedSockets modelConnectedSockets = gson.fromJson(response,ModelConnectedSockets.class);
                    if(modelConnectedSockets.getConnected_Sockets().size() > 0){

                        retry_layout.setVisibility(View.GONE);
                        progress_circular.setVisibility(View.GONE);
                        placeholder.setVisibility(View.VISIBLE);
                        setitemsInPlaceHolder(modelConnectedSockets.getConnected_Sockets());
                    }else{
                        placeholder.setVisibility(View.GONE);
                        retry_layout.setVisibility(View.VISIBLE);
                        problem_text.setText("No Device Connected on ATS Server");
                        progress_circular.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    placeholder.setVisibility(View.GONE);
                    retry_layout.setVisibility(View.VISIBLE);
                    problem_text.setText(""+e.getMessage());
                    progress_circular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(ANError anError) {
                placeholder.setVisibility(View.GONE);
                retry_layout.setVisibility(View.VISIBLE);
                problem_text.setText(""+anError.getMessage());
                progress_circular.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        finaliseActivity(null);
        super.onDestroy();
    }

    public void finaliseActivity(ModelConnectedSockets.ConnectedSocketsBean data){
        Intent intent=new Intent();
        if(data == null){
            intent.putExtra("MESSAGE","");
            setResult(2,intent);
            finish();
        }else{
            intent.putExtra("MESSAGE",""+data.getDevice_id());
            setResult(2,intent);
            finish();
        }


    }


    @Override
    public void OnItemClick(ModelConnectedSockets.ConnectedSocketsBean data) {
        finaliseActivity(data);
    }
}
