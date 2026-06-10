package service;

import model.WifiNetwork;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WifiScanner {
    private int redesInvalidas = 0;

    public int getRedesInvalidas() {
        return redesInvalidas;
    }

    public List<WifiNetwork> scan() throws Exception {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return scanWindows();
        } else if (os.contains("linux")) {
            return scanLinux();
        } else {
            throw new UnsupportedOperationException("Sistema operativo no soportado.");
        }
    }

    private List<WifiNetwork> scanWindows() throws Exception {
        List<String> output = ejecutarComando("netsh", "wlan", "show", "networks", "mode=bssid");
        List<WifiNetwork> networks = new ArrayList<>();

        String ssid = "";
        String autenticacion = "";
        int canal = 0;
        int señal = 0;
        String banda = "";

        for (String line : output) {
            line = line.trim();

            if (line.startsWith("SSID ")) {
                ssid = valores(line);
            } else if (line.startsWith("Autenticación")) {
                autenticacion = valores(line);
            } else if (line.startsWith("Señal")) {
                señal = formatearProcentaje(valores(line));
            } else if (line.startsWith("Canal")) {
                canal = formatearInt(valores(line));
            } else if (line.startsWith("Banda")) {
                banda = valores(line);

                if (!validaciones(ssid, autenticacion, señal, canal)) {
                    redesInvalidas++;
                }else {
                    networks.add(new WifiNetwork(ssid, autenticacion, canal, señal,banda));
                }
            }
        }

        return networks;
    }

    private List<WifiNetwork> scanLinux() throws Exception {
        List<String> output = ejecutarComando("nmcli", "-f", "IN-USE,SSID,CHAN,SIGNAL,SECURITY", "dev", "wifi");
        List<WifiNetwork> networks = new ArrayList<>();

        for (int i = 1; i < output.size(); i++) {
            String line = output.get(i).trim();

            String[] parts = line.split("\\s{2,}");

            if (parts.length < 4) {
                continue;
            }

            int offset = parts[0].equals("*") ? 1 : 0;

            try {
                String ssid = parts[offset];
                int canal = Integer.parseInt(parts[offset + 1]);
                int señal = Integer.parseInt(parts[offset + 2]);
                String autenticacion = parts.length > offset + 3 ? parts[offset + 3] : "Open";
                String banda = parts.length > offset + 4 ? parts[offset + 4] : "";

                networks.add(new WifiNetwork(ssid, autenticacion, canal, señal,banda));
            } catch (Exception e) {
                // Ignora líneas mal formateadas
            }
        }

        return networks;
    }


    private List<String> ejecutarComando(String... commando) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(commando);
        builder.redirectErrorStream(true);

        Process process = builder.start();

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("El comando falló. Código: " + exitCode);
        }

        return lines;
    }

    private String valores(String line) {
        int index = line.indexOf(":");
        return index >= 0 ? line.substring(index + 1).trim() : "";
    }

    private int formatearProcentaje(String value) {
        return formatearInt(value.replace("%", "").trim());
    }

    private int formatearInt(String value) {
        try {
            return Integer.parseInt(value.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean validaciones(String ssid, String autenticacion, int señal, int canal) {
        if (ssid.isEmpty() || ssid.length() > 32) {
            //System.out.println("SSID invalido");
            return false;
        } else if (autenticacion.isEmpty()) {
            //System.out.println("Autenticacion invalida");
            return false;
        } else if (señal < 0 || señal > 100) {
            //System.out.println("Señal invalida");
            return false;
        } else if (canal <= 0) {
            //System.out.println("Canal invalido");
            return false;
        }
        return true;
    }
}