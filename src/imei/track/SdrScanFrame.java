package imei.track;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 * GSM cell tower scanner using RTL-SDR V3/V4.
 *
 * Tool priority (auto-detected at runtime):
 *   1. grgsm_scanner  — full decode: ARFCN, MCC, MNC, LAC, Cell ID, power
 *   2. kalibrate-rtl  — ARFCN, frequency, power
 *   3. rtl_power      — frequency sweep, ARFCN computed from frequency
 *
 * Install on Debian/Ubuntu:
 *   sudo apt install rtl-sdr          (provides rtl_power)
 *   sudo apt install gr-gsm           (provides grgsm_scanner)
 */
public class SdrScanFrame extends JFrame {

    private static final String TOOL_GRGSM     = "grgsm_scanner";
    private static final String TOOL_KALIBRATE = "kalibrate-rtl";
    private static final String TOOL_RTL_POWER = "rtl_power";

    private static final Pattern PAT_GRGSM = Pattern.compile(
            "ARFCN:\\s*(\\d+),\\s*Freq:\\s*([\\d.]+)M,\\s*CID:\\s*(\\d+),"
            + "\\s*LAC:\\s*(\\d+),\\s*MCC:\\s*(\\d+),\\s*MNC:\\s*(\\d+),\\s*Pwr:\\s*([\\d.-]+)");

    private static final Pattern PAT_KALIBRATE = Pattern.compile(
            "arfcn:\\s*(\\d+),\\s*freq:\\s*([\\d.]+)\\s*MHz,\\s*power:\\s*([\\d.-]+)\\s*dBm");

    private final MainFrame mainFrame;
    private final String detectedTool;
    private volatile Process scanProcess;
    private SwingWorker<Void, String> scanWorker;

    private JSpinner deviceSpinner;
    private JComboBox<String> bandCombo;
    private JSpinner gainSpinner;
    private JCheckBox biasTCheck;
    private JButton startButton;
    private JButton stopButton;
    private JButton findInDbButton;
    private JLabel toolLabel;
    private JLabel statusLabel;
    private DefaultTableModel tableModel;
    private JTable resultsTable;
    private JTextArea logArea;

    public SdrScanFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setTitle("GSM Cell Tower Scanner — RTL-SDR V3/V4");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detectedTool = detectTool();
        buildUI();
        configureTableColumns();
        refreshToolLabel();
        pack();
        setLocationRelativeTo(null);
    }

    // -------------------------------------------------------------------------
    // UI construction
    // -------------------------------------------------------------------------

    private void buildUI() {
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

        configPanel.add(new JLabel("Device:"));
        deviceSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 3, 1));
        ((JSpinner.DefaultEditor) deviceSpinner.getEditor()).getTextField().setColumns(2);
        configPanel.add(deviceSpinner);

        configPanel.add(new JLabel("Band:"));
        bandCombo = new JComboBox<>(new String[]{"GSM-900", "GSM-1800", "Both"});
        configPanel.add(bandCombo);

        configPanel.add(new JLabel("Gain (dB):"));
        gainSpinner = new JSpinner(new SpinnerNumberModel(40, 0, 50, 1));
        ((JSpinner.DefaultEditor) gainSpinner.getEditor()).getTextField().setColumns(3);
        configPanel.add(gainSpinner);

        biasTCheck = new JCheckBox("Bias-T (V3/V4)");
        biasTCheck.setToolTipText(
                "Enable 4.5V bias-tee for active antennas. Supported on RTL-SDR V3/V4.");
        configPanel.add(biasTCheck);

        startButton = new JButton("Start Scan");
        stopButton  = new JButton("Stop");
        stopButton.setEnabled(false);
        startButton.addActionListener(e -> startScan());
        stopButton.addActionListener(e -> stopScan());
        configPanel.add(startButton);
        configPanel.add(stopButton);

        toolLabel = new JLabel();
        toolLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        configPanel.add(toolLabel);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        resultsTable = new JTable(tableModel);
        resultsTable.setAutoCreateRowSorter(true);
        JScrollPane tableScroll = new JScrollPane(resultsTable);
        tableScroll.setPreferredSize(new Dimension(760, 200));

        // "Find in Database" is only meaningful when grgsm gives us full cell identity
        findInDbButton = new JButton("Find in Database");
        findInDbButton.setEnabled(false);
        findInDbButton.setToolTipText(
                "Look up phones whose last-seen cell matches the selected row (requires grgsm_scanner).");
        findInDbButton.addActionListener(e -> findInDatabase());

        resultsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    findInDbButton.setEnabled(
                            TOOL_GRGSM.equals(detectedTool)
                            && resultsTable.getSelectedRow() >= 0);
                }
            }
        });

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionPanel.add(findInDbButton);

        logArea = new JTextArea(8, 80);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        JScrollPane logScroll = new JScrollPane(logArea);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, logScroll);
        split.setResizeWeight(0.6);

        statusLabel = new JLabel("Ready.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(actionPanel, BorderLayout.NORTH);
        centerPanel.add(split, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(configPanel,   BorderLayout.NORTH);
        getContentPane().add(centerPanel,   BorderLayout.CENTER);
        getContentPane().add(statusLabel,   BorderLayout.SOUTH);
    }

    private void configureTableColumns() {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        if (TOOL_GRGSM.equals(detectedTool)) {
            for (String col : new String[]{"ARFCN", "Freq (MHz)", "MCC", "MNC", "LAC", "Cell ID", "Power (dBm)"}) {
                tableModel.addColumn(col);
            }
        } else {
            for (String col : new String[]{"ARFCN", "Freq (MHz)", "Power (dBm)"}) {
                tableModel.addColumn(col);
            }
        }
    }

    private void refreshToolLabel() {
        if (detectedTool == null) {
            toolLabel.setText(
                    "<html><font color='red'><b>No SDR tool found.</b> "
                    + "Install rtl-sdr (apt install rtl-sdr) or gr-gsm.</font></html>");
            startButton.setEnabled(false);
        } else {
            toolLabel.setText(
                    "<html><font color='green'>Tool: <b>" + detectedTool + "</b></font></html>");
        }
    }

    // -------------------------------------------------------------------------
    // Tool detection
    // -------------------------------------------------------------------------

    private static String detectTool() {
        for (String tool : new String[]{TOOL_GRGSM, TOOL_KALIBRATE, TOOL_RTL_POWER}) {
            try {
                Process p = new ProcessBuilder("which", tool)
                        .redirectErrorStream(true).start();
                p.waitFor();
                if (p.exitValue() == 0) return tool;
            } catch (Exception ignored) {}
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Scan lifecycle
    // -------------------------------------------------------------------------

    private void startScan() {
        if (detectedTool == null) return;
        tableModel.setRowCount(0);
        logArea.setText("");
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        findInDbButton.setEnabled(false);

        int     device = (Integer) deviceSpinner.getValue();
        int     gain   = (Integer) gainSpinner.getValue();
        boolean biasT  = biasTCheck.isSelected();
        String  band   = (String)  bandCombo.getSelectedItem();

        List<List<String>> commands = buildCommands(band, device, gain, biasT);
        statusLabel.setText("Scanning " + band + " …");

        scanWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (List<String> cmd : commands) {
                    if (isCancelled()) break;
                    publish("[CMD] " + String.join(" ", cmd));
                    ProcessBuilder pb = new ProcessBuilder(cmd);
                    pb.redirectErrorStream(true);
                    scanProcess = pb.start();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(scanProcess.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (isCancelled()) break;
                            publish(line);
                        }
                    }
                    scanProcess.waitFor();
                }
                return null;
            }

            @Override
            protected void process(List<String> lines) {
                for (String line : lines) {
                    logArea.append(line + "\n");
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                    parseLine(line);
                }
            }

            @Override
            protected void done() {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                scanProcess = null;
                statusLabel.setText("Scan complete. "
                        + tableModel.getRowCount() + " result(s) found.");
            }
        };
        scanWorker.execute();
    }

    private void stopScan() {
        if (scanWorker != null) scanWorker.cancel(true);
        Process p = scanProcess;
        if (p != null) {
            p.destroyForcibly();
            scanProcess = null;
        }
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        statusLabel.setText("Scan stopped.");
    }

    // -------------------------------------------------------------------------
    // Command construction
    // -------------------------------------------------------------------------

    private List<List<String>> buildCommands(String band, int device, int gain, boolean biasT) {
        List<List<String>> result = new ArrayList<>();
        if ("Both".equals(band)) {
            result.add(buildSingleCommand("GSM-900",  device, gain, biasT));
            result.add(buildSingleCommand("GSM-1800", device, gain, biasT));
        } else {
            result.add(buildSingleCommand(band, device, gain, biasT));
        }
        return result;
    }

    private List<String> buildSingleCommand(String band, int device, int gain, boolean biasT) {
        List<String> cmd = new ArrayList<>();
        String bandToken = band.replace("-", "");

        if (TOOL_GRGSM.equals(detectedTool)) {
            cmd.add("grgsm_scanner");
            cmd.add("-b"); cmd.add(bandToken);
            cmd.add("-g"); cmd.add(String.valueOf(gain));

        } else if (TOOL_KALIBRATE.equals(detectedTool)) {
            cmd.add("kalibrate-rtl");
            cmd.add("-s"); cmd.add(bandToken);
            cmd.add("-d"); cmd.add(String.valueOf(device));

        } else {
            String freqRange = "GSM-900".equals(band)
                    ? "935M:960M:200k"
                    : "1805M:1880M:200k";
            cmd.add("rtl_power");
            cmd.add("-f"); cmd.add(freqRange);
            cmd.add("-i"); cmd.add("5");
            cmd.add("-1");
            cmd.add("-g"); cmd.add(String.valueOf(gain));
            cmd.add("-d"); cmd.add(String.valueOf(device));
            if (biasT) cmd.add("-T");
            cmd.add("-");
        }
        return cmd;
    }

    // -------------------------------------------------------------------------
    // Output parsers
    // -------------------------------------------------------------------------

    private void parseLine(String line) {
        if (TOOL_GRGSM.equals(detectedTool)) {
            Matcher m = PAT_GRGSM.matcher(line);
            if (m.find()) {
                // columns: ARFCN, Freq, MCC, MNC, LAC, Cell ID, Power
                tableModel.addRow(new Object[]{
                    m.group(1), m.group(2),
                    m.group(5), m.group(6), m.group(4), m.group(3),
                    m.group(7)
                });
            }
        } else if (TOOL_KALIBRATE.equals(detectedTool)) {
            Matcher m = PAT_KALIBRATE.matcher(line);
            if (m.find()) {
                tableModel.addRow(new Object[]{m.group(1), m.group(2), m.group(3)});
            }
        } else {
            parseRtlPowerLine(line);
        }
    }

    /**
     * Parses one CSV line from rtl_power stdout.
     * Format: date, time, hz_low, hz_high, hz_step, samples, db0, db1, ...
     */
    private void parseRtlPowerLine(String line) {
        if (line.startsWith("[CMD]") || line.trim().isEmpty()) return;
        String[] parts = line.split(",\\s*");
        if (parts.length < 7) return;
        try {
            long hzLow  = Long.parseLong(parts[2].trim());
            long hzStep = Long.parseLong(parts[4].trim());
            int  numBins = parts.length - 6;
            for (int i = 0; i < numBins; i++) {
                long freq  = hzLow + (long) i * hzStep;
                int  arfcn = freqHzToArfcn(freq);
                if (arfcn < 0) continue;
                double freqMHz = freq / 1_000_000.0;
                double power   = Double.parseDouble(parts[6 + i].trim());
                tableModel.addRow(new Object[]{
                    arfcn,
                    String.format("%.1f", freqMHz),
                    String.format("%.1f", power)
                });
            }
        } catch (NumberFormatException ignored) {}
    }

    /**
     * Converts a downlink frequency (Hz) to a GSM ARFCN.
     * Returns -1 if the frequency does not fall in a known GSM downlink band.
     *
     * GSM-900  downlink: 935.2–959.8 MHz → ARFCN  1–124
     * GSM-1800 downlink: 1805.2–1879.8 MHz → ARFCN 512–885
     */
    private static int freqHzToArfcn(long hz) {
        double mhz = hz / 1_000_000.0;
        if (mhz >= 935.2 && mhz <= 959.8) {
            return (int) Math.round((mhz - 935.0) / 0.2);
        }
        if (mhz >= 1805.2 && mhz <= 1879.8) {
            return 512 + (int) Math.round((mhz - 1805.2) / 0.2);
        }
        return -1;
    }

    // -------------------------------------------------------------------------
    // Database cross-reference
    // -------------------------------------------------------------------------

    private void findInDatabase() {
        int row = resultsTable.getSelectedRow();
        if (row < 0 || !TOOL_GRGSM.equals(detectedTool)) return;
        // grgsm columns: ARFCN(0), Freq(1), MCC(2), MNC(3), LAC(4), Cell ID(5), Power(6)
        String mcc    = String.valueOf(tableModel.getValueAt(row, 2));
        String mnc    = String.valueOf(tableModel.getValueAt(row, 3));
        String lac    = String.valueOf(tableModel.getValueAt(row, 4));
        String cellId = String.valueOf(tableModel.getValueAt(row, 5));
        String cellKey = mcc + ":" + mnc + ":" + lac + ":" + cellId;
        List<Phones> matches = mainFrame.findByCell(cellKey);
        showCrossReferenceDialog(cellKey, matches);
    }

    private void showCrossReferenceDialog(String cellKey, List<Phones> matches) {
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No phones in the database were last seen at cell " + cellKey + ".",
                    "Cross-Reference", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder(
                "<html><b>" + matches.size() + " phone(s) last seen at cell " + cellKey + ":</b><br><br>");
        for (Phones p : matches) {
            sb.append("&nbsp;&nbsp;IMEI: <b>").append(p.getImei()).append("</b>")
              .append(" &mdash; Owner: ").append(p.getName())
              .append("<br>");
        }
        sb.append("</html>");
        JOptionPane.showMessageDialog(this, sb.toString(),
                "Cross-Reference — " + matches.size() + " match(es)",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
