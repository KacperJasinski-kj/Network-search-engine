package main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.WifiNetwork;
import service.WifiAnalyzer;
import service.WifiScanner;
import ventana.tablaRedes;

import javax.swing.*;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        WifiScanner wifiScanner = new WifiScanner();
        WifiAnalyzer wifiAnalyzer = new WifiAnalyzer();

        try {
            List<WifiNetwork> networks = wifiScanner.scan();

            if (networks.isEmpty()) {
                System.out.println("No se encontraron redes.");
                return;
            } else if (wifiScanner.getRedesInvalidas() >= 1) {
                System.out.println("Redes invalidas: " + wifiScanner.getRedesInvalidas());
            }


            mostrarRedes(networks);

            Map<Integer, Integer> saturation = new TreeMap<>(wifiAnalyzer.contarRedesPorCanal(networks));

            mostrarSaturacion(saturation);

            int canalRecomendado = wifiAnalyzer.recomendarCanal(networks);

            System.out.println("\n-----------------------------------------------------------------------------");
            System.out.println("\nCanal recomendado para 2.4 GHz: " + canalRecomendado);
            pasarCSV(networks);
            System.out.println("\n-----------------------------------------------------------------------------");
            System.out.println("Geolocalizador de IPV4 publica");
            buscadorIP();

            //-------------------------------------------------------------------
            //                              interfaz
            //-------------------------------------------------------------------

            SwingUtilities.invokeLater(() -> {
                tablaRedes ventana = new tablaRedes(networks);
                ventana.setVisible(true);
            });


        } catch (Exception e) {
            System.err.println("Error al analizar redes WiFi: " + e.getMessage());
        }
    }

    private static void mostrarRedes(List<WifiNetwork> networks) {
        networks.sort(Comparator.comparing(WifiNetwork::getSeñal).reversed());
        System.out.printf("%-25s %-8s %-8s %-20s %-8s%n", "SSID", "Canal", "Señal", "Seguridad", "Banda");
        System.out.println("-----------------------------------------------------------------------------");

        for (WifiNetwork network : networks) {
            System.out.printf("%-25s %-8s %-8s %-20s %-8s%n",
                    network.getSSID(), network.getCanal(), network.getSeñal() + "%", network.getAutenticacion(), network.getBanda());
        }
    }

    private static void mostrarSaturacion(Map<Integer, Integer> saturation) {
        List<Map.Entry<Integer, Integer>> canales = new ArrayList<>(saturation.entrySet());
        canales.sort(Map.Entry.comparingByValue());

        System.out.println("\n-----------------------------------------------------------------------------");
        System.out.println("\nSaturación por canal:");

        for (Map.Entry<Integer, Integer> entry : canales) {
            System.out.println("Canal " + entry.getKey() + " -- " + entry.getValue() + " redes");
        }
    }

    private static void pasarCSV(List<WifiNetwork> networks) {
        System.out.println("\n-----------------------------------------------------------------------------");
        System.out.println("\nRedes guardadas en archivo CSV");
        File file = new File("redesguardadas.csv");
        try (PrintWriter oos = new PrintWriter(file, "UTF-8")) {
            for (WifiNetwork network : networks) {
                oos.println(network);
            }
        } catch (FileNotFoundException e) {
            //error si no se puede crear el fichero
            System.out.println("Error: no se pudo crear el fichero.");
        } catch (IOException e) {
            //error al guardar el registro
            System.out.println("Error guardando: " + e.getMessage());
        }
    }

    public static void buscadorIP() throws IOException, InterruptedException {

        // Debe ser una IP pública
        String ip = "8.8.8.8";

        String url = "http://ip-api.com/json/" + ip;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        System.out.println(json.toPrettyString());

        String status = json.path("status").asText();

        if (!"success".equals(status)) {
            System.out.println("Error: " + json.path("message").asText());
            return;
        }

    }
}