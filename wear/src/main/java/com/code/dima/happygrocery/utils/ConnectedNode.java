package com.code.dima.happygrocery.utils;

class ConnectedNode {

    private String connectedNodeID;
    private static ConnectedNode instance;

    private ConnectedNode() {
        this.connectedNodeID = null;
        instance = null;
    }

    static ConnectedNode getInstance() {
        if (instance == null) {
            instance = new ConnectedNode();
        }
        return instance;
    }

    String getConnectedNodeID() {
        return this.connectedNodeID;
    }

    void setConnectedNodeID(String connectedNodeID) {
        this.connectedNodeID = connectedNodeID;
    }
}
