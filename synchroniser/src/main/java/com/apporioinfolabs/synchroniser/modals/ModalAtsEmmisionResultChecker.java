package com.apporioinfolabs.synchroniser.modals;

public class ModalAtsEmmisionResultChecker {

    /**
     * nameValuePairs : {"result":1,"message":"SUCCESS"}
     */

    private NameValuePairsBean nameValuePairs;

    public NameValuePairsBean getNameValuePairs() {
        return nameValuePairs;
    }

    public void setNameValuePairs(NameValuePairsBean nameValuePairs) {
        this.nameValuePairs = nameValuePairs;
    }

    public static class NameValuePairsBean {
        /**
         * result : 1
         * message : SUCCESS
         */

        private int result;
        private String message;

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
