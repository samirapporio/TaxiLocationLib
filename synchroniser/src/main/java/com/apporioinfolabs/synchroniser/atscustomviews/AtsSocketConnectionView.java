package com.apporioinfolabs.synchroniser.atscustomviews;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.apporioinfolabs.synchroniser.AtsApplication;
import com.apporioinfolabs.synchroniser.AtsEventBus;
import com.apporioinfolabs.synchroniser.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AtsSocketConnectionView extends LinearLayout {

    private Context mContext  = null  ;
    private ImageView imageView ;
    private static final String TAG = "AtsSocketConnectionView";

    public AtsSocketConnectionView(Context context) {
        super(context);
        initializeViews(context);
    }

    public AtsSocketConnectionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public AtsSocketConnectionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AtsSocketConnectionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ats_connection_detector_view, this);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        try{
            imageView = findViewById(R.id.socket_image);
        }catch (Exception e){
            Log.e(TAG , ""+e.getMessage());
        }
    }


    public void setImageAccordingToConnection(boolean val){
        if(val){
            imageView.setImageResource(R.drawable.ic_socket_connected_vector);
        }else{
            imageView.setImageResource(R.drawable.ic_socket_disconnected_vector);
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
        setImageAccordingToConnection(AtsApplication.isSocketConnected());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(String event) {
        if(event.equals(""+AtsEventBus.SOCKET_CONNECTED)){
            setImageAccordingToConnection(true);
        }else{
            setImageAccordingToConnection(false);
        }
    };

}
