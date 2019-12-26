package com.apporioinfolabs.taxilocationlib;

import java.util.List;

public class ModelConnectedSockets {

    private List<ConnectedSocketsBean> connected_Sockets;

    public List<ConnectedSocketsBean> getConnected_Sockets() {
        return connected_Sockets;
    }

    public void setConnected_Sockets(List<ConnectedSocketsBean> connected_Sockets) {
        this.connected_Sockets = connected_Sockets;
    }

    public static class ConnectedSocketsBean {
        /**
         * device_id : c37325460abf9e5d
         * model : motorola motorola one power
         * socket_id : _gcDHWYMihIQ-q8GAAAA
         * location_permission : true
         * device_timestamp : 13 Dec. Fri 12:52 p.m.
         */

        private String device_id;
        private String model;
        private String socket_id;
        private boolean location_permission;
        private String device_timestamp;

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getSocket_id() {
            return socket_id;
        }

        public void setSocket_id(String socket_id) {
            this.socket_id = socket_id;
        }

        public boolean isLocation_permission() {
            return location_permission;
        }

        public void setLocation_permission(boolean location_permission) {
            this.location_permission = location_permission;
        }

        public String getDevice_timestamp() {
            return device_timestamp;
        }

        public void setDevice_timestamp(String device_timestamp) {
            this.device_timestamp = device_timestamp;
        }
    }
}
