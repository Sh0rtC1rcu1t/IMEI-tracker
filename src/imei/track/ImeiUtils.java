package imei.track;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * IMEI validation (Luhn algorithm) and offline TAC manufacturer/model lookup.
 *
 * TAC data is loaded from tac_data.csv bundled in the JAR (src/imei/track/).
 * To update: replace tac_data.csv with a current export from the GSMA TAC database.
 */
public class ImeiUtils {

    private static final Map<String, String[]> TAC_DB = new HashMap<>();

    static {
        loadTacDatabase();
    }

    private static void loadTacDatabase() {
        InputStream is = ImeiUtils.class.getResourceAsStream("tac_data.csv");
        if (is == null) {
            System.err.println("WARNING: tac_data.csv not found in classpath.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] parts = line.split(",", 3);
                if (parts.length >= 2) {
                    String tac   = parts[0].trim();
                    String mfr   = parts[1].trim();
                    String model = parts.length > 2 ? parts[2].trim() : "";
                    TAC_DB.put(tac, new String[]{mfr, model});
                }
            }
        } catch (IOException e) {
            System.err.println("TAC database load failed: " + e.getMessage());
        }
    }

    /**
     * Validates a 15-digit IMEI using the Luhn algorithm.
     */
    public static boolean isValidImei(String imei) {
        if (imei == null || !imei.matches("\\d{15}")) return false;
        int sum = 0;
        int len = imei.length();
        for (int i = 0; i < len; i++) {
            int d = imei.charAt(i) - '0';
            // positions at odd distance from right (1, 3, 5, …) are doubled
            if ((len - 1 - i) % 2 == 1) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            sum += d;
        }
        return sum % 10 == 0;
    }

    /**
     * Returns [manufacturer, model] for the TAC (first 8 digits of IMEI),
     * or null if not found in the bundled database.
     */
    public static String[] tacLookup(String imei) {
        if (imei == null || imei.length() < 8) return null;
        return TAC_DB.get(imei.substring(0, 8));
    }

    /**
     * Returns a human-readable TAC description, or "Unknown device" if not found.
     */
    public static String tacDescription(String imei) {
        String[] entry = tacLookup(imei);
        if (entry == null) return "Unknown device";
        return entry[1].isEmpty() ? entry[0] : entry[0] + " " + entry[1];
    }
}
