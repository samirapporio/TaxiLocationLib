package com.apporioinfolabs.taxilocationlib;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;


@Layout(R.layout.custom_list_item)
public class HolderDevices {

    @View(R.id.device_name) TextView device_name ;
    @View(R.id.device_udid) TextView device_udid ;
    @View(R.id.time_txt) TextView time_txt ;
    @View(R.id.location_image) ImageView location_image ;

    private OnClickListener onClickListener;
    private ModelConnectedSockets.ConnectedSocketsBean data ;
    private Context context;



    public HolderDevices(Context context, OnClickListener onClickListener, ModelConnectedSockets.ConnectedSocketsBean connectedSocketsBean){
        this.onClickListener = onClickListener ;
        this.data = connectedSocketsBean ;
        this.context = context ;
    }

    @Resolve
    public void setDataAccordingly(){
        device_name.setText(""+data.getModel());
        device_udid.setText(""+data.getDevice_id());
        time_txt.setText(""+data.getDevice_timestamp());

        if(data.isLocation_permission()){
            location_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_location_permited));
        }else{
            location_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_location_not_permited));
        }
    }

    @Click(R.id.root)
    public void onRooTclick(){
        this.onClickListener.OnItemClick(this.data);
    }




    public interface OnClickListener{
        void OnItemClick(ModelConnectedSockets.ConnectedSocketsBean data);
    }



}
