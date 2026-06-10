package model;

import java.io.Serializable;

public class WifiNetwork {
    private String SSID;
    private String autenticacion;
    private int canal;
    private int señal;
    private String banda;

    public WifiNetwork() {

    }

    public WifiNetwork(String SSID, String autenticacion, int canal, int señal, String banda) {
        this.SSID = SSID;
        this.autenticacion = autenticacion;
        this.canal = canal;
        this.señal = señal;
        this.banda = banda;
    }

    public String getBanda() { return banda; }

    public void setBanda(String banda) { this.banda = banda; }

    public String getSSID() { return SSID; }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getAutenticacion() {
        return autenticacion;
    }

    public void setAutenticacion(String autenticacion) {
        this.autenticacion = autenticacion;
    }

    public int getCanal() {
        return canal;
    }

    public void setCanal(int canal) {
        this.canal = canal;
    }

    public int getSeñal() {
        return señal;
    }

    public void setSeñal(int señal) {
        this.señal = señal;
    }

    @Override
    public String toString() {
        return "WifiNetwork{" +
                "SSID='" + SSID + '\'' +
                ", autenticacion='" + autenticacion + '\'' +
                ", canal=" + canal +
                ", señal=" + señal +
                ", banda='" + banda + '\'' +
                '}';
    }
}
