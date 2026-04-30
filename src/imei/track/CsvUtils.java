package imei.track;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV import and export for phone records.
 *
 * Export format (header + one row per phone):
 *   owner,imei,history,created_at,updated_at,last_seen_cell,status
 *
 * Import: reads owner, imei, history (columns 1-3).
 *   - Rows with invalid IMEI (Luhn check) are rejected.
 *   - Rows whose IMEI already exists in memory are skipped as duplicates.
 *   - Valid new rows are batch-inserted via MainFrame.batchAddToDB().
 */
public class CsvUtils {

    public static void exportPhones(List<Phones> phones, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("owner,imei,history,created_at,updated_at,last_seen_cell,status");
            for (Phones p : phones) {
                pw.println(
                    escape(p.getName()) + ","
                    + escape(p.getImei()) + ","
                    + escape(p.getHistory()) + ","
                    + escape(safe(p.getCreatedAt())) + ","
                    + escape(safe(p.getUpdatedAt())) + ","
                    + escape(safe(p.getLastSeenCell())) + ","
                    + escape(safe(p.getStatus()))
                );
            }
        }
    }

    public static ImportResult importPhones(File file, MainFrame mainFrame) throws IOException {
        int skipped = 0, invalid = 0;
        List<String[]> toInsert = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] fields = parseLine(line);
                if (fields.length < 3) { invalid++; continue; }
                String owner = fields[0].trim();
                String imei  = fields[1].trim();
                String hist  = fields[2].trim();
                if (!ImeiUtils.isValidImei(imei)) { invalid++;  continue; }
                if (mainFrame.imeiExistsInMemory(imei))  { skipped++; continue; }
                toInsert.add(new String[]{owner, imei, hist});
            }
        }

        if (!toInsert.isEmpty()) {
            mainFrame.batchAddToDB(toInsert);
        }
        return new ImportResult(toInsert.size(), skipped, invalid);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String escape(String s) {
        if (s == null || s.isEmpty()) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /** RFC-4180 compliant CSV line parser (handles quoted fields with embedded commas/quotes). */
    private static String[] parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else if (c == '"') {
                    inQuotes = false;
                } else {
                    sb.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }

    // -------------------------------------------------------------------------
    // Result type
    // -------------------------------------------------------------------------

    public static class ImportResult {
        public final int imported, skipped, invalid;
        ImportResult(int imported, int skipped, int invalid) {
            this.imported = imported;
            this.skipped  = skipped;
            this.invalid  = invalid;
        }
        @Override
        public String toString() {
            return imported + " imported, " + skipped + " duplicate(s) skipped, "
                    + invalid + " invalid/rejected.";
        }
    }
}
