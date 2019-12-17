package com.apporioinfolabs.taxilocationlib;

public class ModelLocationIncoming {

    /**
     * location : {"latitude":28.4123607,"longitude":77.0441051,"accuracy":17.68899917602539,"bearing":82.58280181884766,"device_time":1576065290038}
     */

    private LocationBean location;

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public static class LocationBean {
        /**
         * latitude : 28.4123607
         * longitude : 77.0441051
         * accuracy : 17.68899917602539
         * bearing : 82.58280181884766
         * device_time : 1576065290038
         */

        private double latitude;
        private double longitude;
        private double accuracy;
        private double bearing;
        private long device_time;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(double accuracy) {
            this.accuracy = accuracy;
        }

        public double getBearing() {
            return bearing;
        }

        public void setBearing(double bearing) {
            this.bearing = bearing;
        }

        public long getDevice_time() {
            return device_time;
        }

        public void setDevice_time(long device_time) {
            this.device_time = device_time;
        }
    }
}
