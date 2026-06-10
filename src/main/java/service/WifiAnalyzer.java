package service;

import model.WifiNetwork;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WifiAnalyzer {
    public Map<Integer, Integer> contarRedesPorCanal(List<WifiNetwork> networks) {
        Map<Integer, Integer> result = new HashMap<>();

        for (WifiNetwork network : networks) {
            result.put(network.getCanal(), result.getOrDefault(network.getCanal(), 0) + 1);
        }

        return result;
    }


    public int recomendarCanal(List<WifiNetwork> networks) {
        int[] channels = {1, 6, 11};
        Map<Integer, Integer> saturation = contarRedesPorCanal(networks);

        int mejorCanal = 1;
        int minNetworks = Integer.MAX_VALUE;

        for (int channel : channels) {
            int count = saturation.getOrDefault(channel, 0);

            if (count < minNetworks) {
                minNetworks = count;
                mejorCanal = channel;
            }
        }

        return mejorCanal;
    }
}
