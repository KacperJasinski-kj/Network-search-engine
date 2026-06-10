package ventana;

import model.WifiNetwork;
import service.WifiScanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class tablaRedes extends JFrame {

    WifiScanner wifiScanner = new WifiScanner();

    public tablaRedes(List<WifiNetwork> networks) {

        setTitle("Redes WiFi");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnas = {
                "SSID",
                "Autenticación",
                "Canal",
                "Señal",
                "Banda"
        };

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        for (WifiNetwork wifi : networks) {
            modelo.addRow(new Object[]{
                    wifi.getSSID(),
                    wifi.getAutenticacion(),
                    wifi.getCanal(),
                    wifi.getSeñal(),
                    wifi.getBanda()
            });
        }


        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        add(scroll);
    }
}