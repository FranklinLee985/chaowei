package com.classroomsdk.bean;

public class WhiteBoardFinshBean {
    String protocal;
    String hostname;
    String port;

    public WhiteBoardFinshBean(String protocal, String hostname, String port) {
        this.protocal = protocal;
        this.hostname = hostname;
        this.port = port;
    }

    public String getProtocal() {
        return protocal;
    }

    public void setProtocal(String protocal) {
        this.protocal = protocal;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "WhiteBoardFinshBean{" +
                "protocal='" + protocal + '\'' +
                ", hostname='" + hostname + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
