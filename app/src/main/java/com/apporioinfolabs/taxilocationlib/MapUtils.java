package com.apporioinfolabs.taxilocationlib;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapUtils {


    public static Marker adddot(Context context, GoogleMap mMap, LatLng markerLatLng) {
        View mView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marke_layout, null);
        Marker customMarker = mMap.addMarker(new MarkerOptions()
                .position(markerLatLng)
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, mView))));
        return customMarker;
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    public static void animateMarker(final LatLng toPosition, GoogleMap googleMap, final Marker m, float bearingfactor) {
        Location startingLocation = new Location("starting point");
        startingLocation.setLatitude(m.getPosition().latitude);
        startingLocation.setLongitude(m.getPosition().longitude);
        Location endingLocation = new Location("ending point");
        endingLocation.setLatitude(toPosition.latitude);
        endingLocation.setLongitude(toPosition.longitude);
        float targetBearing = startingLocation.bearingTo(endingLocation);


        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(m.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {

                try {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                    double lat = t * toPosition.latitude + (1 - t)
                            * startLatLng.latitude;
                    m.setPosition(new LatLng(lat, lng));

                    if (t < 1.0) {
                        handler.postDelayed(this, 16);
                    }
                } catch (Exception e) {

                }
            }
        });
    }
}
