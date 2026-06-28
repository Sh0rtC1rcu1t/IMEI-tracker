package imei.track;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends javax.swing.JFrame {

    private String _imeiData;
    private String _createdAt;
    private String _updatedAt;
    private String _lastSeenCell;
    private String _status;
    private static char[] password;
    private String name;
    private String history;
    private static ArrayList<Phones> _phonesArr = new ArrayList<>();
    private static PasswordPrompt pPrompt;

    private static final String DEFAULT_DB_URL  = "jdbc:mysql://ultifix.com:3306/ultifixc_imei";
    private static final String DEFAULT_DB_USER = "ultifixc_admin";
    private static final Properties DB_PROPS = new Properties();

    static {
        loadDbProps();
    }

    private static void loadDbProps() {
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            DB_PROPS.load(fis);
        } catch (IOException e) {
            System.err.println("WARNING: db.properties not found; using hardcoded defaults. ("
                    + e.getMessage() + ")");
            DB_PROPS.setProperty("db.url",  DEFAULT_DB_URL);
            DB_PROPS.setProperty("db.user", DEFAULT_DB_USER);
        }
    }

    public MainFrame() {
        initComponents();
        jButton2.setVisible(false);
        jButton4.setVisible(false);
        postInit();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        statusFilterCombo = new javax.swing.JComboBox<>(
                new String[]{"All", "ACTIVE", "STOLEN", "FOUND"});
        statusStatsLabel = new javax.swing.JLabel(
                "Total: 0  |  Active: 0  |  Stolen: 0  |  Found: 0");
        statusStatsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanel2 = new javax.swing.JPanel();

        // table setup — 7 columns
        tableModel = new javax.swing.table.DefaultTableModel(
                new Object[]{"Owner", "IMEI", "Repair History", "Created", "Updated", "Last Cell", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        jTable1 = new javax.swing.JTable(tableModel);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.setFillsViewportHeight(true);
        jTable1.setAutoCreateRowSorter(true);
        jScrollPane1 = new javax.swing.JScrollPane(jTable1);

        jButton2 = new javax.swing.JButton();
        jButton2.setText("EDIT");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4 = new javax.swing.JButton();
        jButton4.setText("DELETE");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int row = jTable1.getSelectedRow();
                    if (row >= 0) {
                        int modelRow  = jTable1.convertRowIndexToModel(row);
                        name          = (String) tableModel.getValueAt(modelRow, 0);
                        _imeiData     = (String) tableModel.getValueAt(modelRow, 1);
                        history       = (String) tableModel.getValueAt(modelRow, 2);
                        _createdAt    = (String) tableModel.getValueAt(modelRow, 3);
                        _updatedAt    = (String) tableModel.getValueAt(modelRow, 4);
                        _lastSeenCell = (String) tableModel.getValueAt(modelRow, 5);
                        _status       = (String) tableModel.getValueAt(modelRow, 6);
                        jButton2.setVisible(true);
                        jButton4.setVisible(true);
                    } else {
                        jButton2.setVisible(false);
                        jButton4.setVisible(false);
                    }
                }
            }
        });

        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(jButton2);
        buttonPanel.add(jButton4);

        jPanel2.setLayout(new BorderLayout());
        jPanel2.add(jScrollPane1, BorderLayout.CENTER);
        jPanel2.add(buttonPanel, BorderLayout.SOUTH);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem importItem = new JMenuItem("Import CSV…");
        JMenuItem exportItem = new JMenuItem("Export CSV…");
        JMenuItem exitItem   = new JMenuItem("Exit");
        importItem.addActionListener(e -> importCsv());
        exportItem.addActionListener(e -> exportCsv());
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(860, 480));

        jLabel1.setText("IMEI / Name:");

        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        statusFilterCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyFilters();
            }
        });

        jButton3.setText("Add New Phone");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setText("SDR Scan");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusFilterCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(statusStatsLabel)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(statusFilterCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton5)
                    .addComponent(statusStatsLabel)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
        configureTable();
    }// </editor-fold>//GEN-END:initComponents

    private void postInit() {
        buildTablePopupMenu();
        wireTablePopupMenu();
        wireKeyboardShortcuts();
    }

    private void configureTable() {
        int[] widths = {120, 155, 180, 110, 110, 130, 70};
        for (int i = 0; i < widths.length; i++) {
            jTable1.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        StatusRowRenderer renderer = new StatusRowRenderer();
        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    // -------------------------------------------------------------------------
    // Right-click context menu
    // -------------------------------------------------------------------------

    private void buildTablePopupMenu() {
        tablePopupMenu = new javax.swing.JPopupMenu();

        JMenuItem miActive = new JMenuItem("Mark as ACTIVE");
        JMenuItem miStolen = new JMenuItem("Mark as STOLEN");
        JMenuItem miFound  = new JMenuItem("Mark as FOUND");
        miActive.addActionListener(e -> quickSetStatus(_imeiData, "ACTIVE"));
        miStolen.addActionListener(e -> quickSetStatus(_imeiData, "STOLEN"));
        miFound .addActionListener(e -> quickSetStatus(_imeiData, "FOUND"));

        JMenuItem miCopy = new JMenuItem("Copy IMEI to Clipboard");
        miCopy.addActionListener(e -> {
            if (_imeiData != null && !_imeiData.isEmpty()) {
                java.awt.Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(new StringSelection(_imeiData), null);
            }
        });

        JMenuItem miEdit   = new JMenuItem("Edit");
        JMenuItem miDelete = new JMenuItem("Delete");
        miEdit  .addActionListener(e -> jButton2ActionPerformed(null));
        miDelete.addActionListener(e -> jButton4ActionPerformed(null));

        tablePopupMenu.add(miActive);
        tablePopupMenu.add(miStolen);
        tablePopupMenu.add(miFound);
        tablePopupMenu.addSeparator();
        tablePopupMenu.add(miCopy);
        tablePopupMenu.addSeparator();
        tablePopupMenu.add(miEdit);
        tablePopupMenu.add(miDelete);
    }

    private void wireTablePopupMenu() {
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            private void maybeShowPopup(java.awt.event.MouseEvent e) {
                if (!e.isPopupTrigger()) return;
                int viewRow = jTable1.rowAtPoint(e.getPoint());
                if (viewRow < 0) return;
                jTable1.setRowSelectionInterval(viewRow, viewRow);
                tablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
            @Override public void mousePressed (java.awt.event.MouseEvent e) { maybeShowPopup(e); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { maybeShowPopup(e); }
        });
    }

    // -------------------------------------------------------------------------
    // Keyboard shortcuts (InputMap/ActionMap)
    // -------------------------------------------------------------------------

    private void wireKeyboardShortcuts() {
        // Delete key: confirm + delete selected row
        jTable1.getInputMap(javax.swing.JComponent.WHEN_FOCUSED)
               .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteRow");
        jTable1.getActionMap().put("deleteRow", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                if (jTable1.getSelectedRow() >= 0) jButton4ActionPerformed(null);
            }
        });

        // Enter key: open EditData for selected row
        jTable1.getInputMap(javax.swing.JComponent.WHEN_FOCUSED)
               .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "editRow");
        jTable1.getActionMap().put("editRow", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                if (jTable1.getSelectedRow() >= 0) jButton2ActionPerformed(null);
            }
        });

        // F5: re-query DB and reapply filters (window-wide scope)
        jTable1.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
               .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refreshDb");
        jTable1.getActionMap().put("refreshDb", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                try { queryDatabase(); } catch (Exception ex) {
                    System.err.println("F5 refresh failed: " + ex.getMessage());
                }
                applyFilters();
            }
        });

        // Escape in search field: clear text and reapply filters
        jTextField1.getInputMap(javax.swing.JComponent.WHEN_FOCUSED)
                   .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearSearch");
        jTextField1.getActionMap().put("clearSearch", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                jTextField1.setText("");
                applyFilters();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Quick status update (no EditData dialog)
    // -------------------------------------------------------------------------

    public void quickSetStatus(String imei, String status) {
        if (imei == null || imei.isEmpty()) return;
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE phones SET status=?, updated_at=NOW() WHERE imei=?")) {
            ps.setString(1, status);
            ps.setString(2, imei);
            ps.executeUpdate();
            queryDatabase();
            applyFilters();
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("quickSetStatus failed: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to update status: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -------------------------------------------------------------------------
    // Statistics label
    // -------------------------------------------------------------------------

    private void updateStats() {
        if (_phonesArr == null || _phonesArr.isEmpty()) {
            statusStatsLabel.setText("Total: 0  |  Active: 0  |  Stolen: 0  |  Found: 0");
            return;
        }
        int total = _phonesArr.size(), active = 0, stolen = 0, found = 0;
        for (Phones p : _phonesArr) {
            String s = p.getStatus();
            if      ("ACTIVE".equals(s)) active++;
            else if ("STOLEN".equals(s)) stolen++;
            else if ("FOUND" .equals(s)) found++;
        }
        statusStatsLabel.setText(
                "Total: " + total
                + "  |  Active: " + active
                + "  |  Stolen: " + stolen
                + "  |  Found: "  + found);
    }

    // -------------------------------------------------------------------------
    // Event handlers
    // -------------------------------------------------------------------------

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        applyFilters();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        applyFilters();
    }//GEN-LAST:event_jButton1ActionPerformed

    public void applyFilters() {
        String query        = jTextField1.getText().trim().toLowerCase();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        tableModel.setRowCount(0);
        jButton2.setVisible(false);
        jButton4.setVisible(false);
        if (_phonesArr == null) { updateStats(); return; }
        for (Phones p : _phonesArr) {
            boolean textMatch = query.isEmpty()
                    || p.getImei().toLowerCase().contains(query)
                    || p.getName().toLowerCase().contains(query);
            boolean statusMatch = "All".equals(statusFilter)
                    || statusFilter.equals(p.getStatus());
            if (textMatch && statusMatch) {
                tableModel.addRow(new Object[]{
                    p.getName(), p.getImei(), p.getHistory(),
                    safe(p.getCreatedAt()), safe(p.getUpdatedAt()),
                    safe(p.getLastSeenCell()), safe(p.getStatus())
                });
            }
        }
        updateStats();
    }

    public void refreshTable() {
        applyFilters();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        EditData editData = new EditData(this, true, _imeiData);
        editData.setNameField(name);
        editData.setImeiField(_imeiData);
        editData.setHistoryField(history);
        editData.setLastSeenCell(safe(_lastSeenCell));
        editData.setCreatedAt(safe(_createdAt));
        editData.setUpdatedAt(safe(_updatedAt));
        editData.setStatus(safe(_status));
        editData.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        EditData editData = new EditData(this, false, null);
        editData.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        SdrScanFrame sdr = new SdrScanFrame(this);
        sdr.setVisible(true);
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        if (_imeiData == null || _imeiData.isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete record for IMEI: " + _imeiData + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            deleteFromDB(_imeiData);
        }
    }

    private void importCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        try {
            CsvUtils.ImportResult result = CsvUtils.importPhones(file, this);
            JOptionPane.showMessageDialog(this, result.toString(),
                    "Import Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Import failed: " + e.getMessage(),
                    "Import Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportCsv() {
        List<Phones> toExport = buildFilteredPhonesList();
        if (toExport.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No records visible. Adjust filters or run a search first.",
                    "Export", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        fc.setSelectedFile(new File("phones_export.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getPath() + ".csv");
        }
        try {
            CsvUtils.exportPhones(toExport, file);
            JOptionPane.showMessageDialog(this,
                    "Exported " + toExport.size() + " record(s) to " + file.getName(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Phones> buildFilteredPhonesList() {
        List<Phones> result = new ArrayList<>();
        if (_phonesArr == null) return result;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String imei = (String) tableModel.getValueAt(i, 1);
            for (Phones p : _phonesArr) {
                if (imei.equals(p.getImei())) { result.add(p); break; }
            }
        }
        return result;
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                pPrompt = new PasswordPrompt();
                pPrompt.setVisible(true);
            }
        });
    }

    private Connection openConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        password = pPrompt.retrievePass();
        return DriverManager.getConnection(
                DB_PROPS.getProperty("db.url"),
                DB_PROPS.getProperty("db.user"),
                String.valueOf(password));
    }

    public void addToDB(String owner, String imei, String history, String status) {
        if (imeiExistsInMemory(imei)) {
            JOptionPane.showMessageDialog(this,
                    "IMEI " + imei + " already exists in the database.",
                    "Duplicate IMEI", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO phones (owner, imei, history, status) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, owner);
            ps.setString(2, imei);
            ps.setString(3, history);
            ps.setString(4, status);
            ps.executeUpdate();
            queryDatabase();
            applyFilters();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("addToDB failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to save: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateDB(String owner, String imei, String history,
            String originalImei, String lastSeenCell, String status) {
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE phones SET owner=?, imei=?, history=?, last_seen_cell=?, "
                     + "status=?, updated_at=NOW() WHERE imei=?")) {
            ps.setString(1, owner);
            ps.setString(2, imei);
            ps.setString(3, history);
            ps.setString(4, lastSeenCell.isEmpty() ? null : lastSeenCell);
            ps.setString(5, status);
            ps.setString(6, originalImei);
            ps.executeUpdate();
            queryDatabase();
            applyFilters();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("updateDB failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to update: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteFromDB(String imei) {
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM phones WHERE imei=?")) {
            ps.setString(1, imei);
            ps.executeUpdate();
            queryDatabase();
            applyFilters();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("deleteFromDB failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to delete: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void batchAddToDB(List<String[]> rows) {
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO phones (owner, imei, history, status) VALUES (?, ?, ?, ?)")) {
            for (String[] row : rows) {
                ps.setString(1, row[0]);
                ps.setString(2, row[1]);
                ps.setString(3, row[2]);
                ps.setString(4, row.length > 3 ? row[3] : "ACTIVE");
                ps.addBatch();
            }
            ps.executeBatch();
            queryDatabase();
            applyFilters();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("batchAddToDB failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to import: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean imeiExistsInMemory(String imei) {
        if (_phonesArr == null) return false;
        for (Phones p : _phonesArr) {
            if (imei.equals(p.getImei())) return true;
        }
        return false;
    }

    public List<Phones> findByCell(String cellKey) {
        List<Phones> matches = new ArrayList<>();
        if (_phonesArr == null || cellKey == null || cellKey.isEmpty()) return matches;
        for (Phones p : _phonesArr) {
            if (cellKey.equals(p.getLastSeenCell())) matches.add(p);
        }
        return matches;
    }

    private static Connection openConnectionStatic() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        password = pPrompt.retrievePass();
        return DriverManager.getConnection(
                DB_PROPS.getProperty("db.url"),
                DB_PROPS.getProperty("db.user"),
                String.valueOf(password));
    }

    public static boolean queryDatabase() throws SQLException, ClassNotFoundException {
        _phonesArr = new ArrayList<>();
        try (Connection conn = openConnectionStatic()) {
            SchemaManager.migrate(conn);
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM phones");
                 ResultSet srs = ps.executeQuery()) {
                while (srs.next()) {
                    Phones phone = new Phones();
                    phone.setImei(srs.getString("imei"));
                    phone.setName(srs.getString("owner"));
                    phone.setHistory(srs.getString("history"));
                    phone.setCreatedAt(getColumnSafe(srs, "created_at"));
                    phone.setUpdatedAt(getColumnSafe(srs, "updated_at"));
                    phone.setLastSeenCell(getColumnSafe(srs, "last_seen_cell"));
                    phone.setStatus(getColumnSafe(srs, "status"));
                    _phonesArr.add(phone);
                }
            }
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("queryDatabase failed: " + e.getMessage());
            return false;
        }
    }

    private static String getColumnSafe(ResultSet rs, String column) {
        try {
            String val = rs.getString(column);
            return val == null ? "" : val;
        } catch (SQLException e) {
            return "";
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    // -------------------------------------------------------------------------
    // Color-coded row renderer (STOLEN=red tint, FOUND=green tint)
    // -------------------------------------------------------------------------

    private static class StatusRowRenderer extends DefaultTableCellRenderer {
        private static final Color COLOR_STOLEN = new Color(255, 180, 180);
        private static final Color COLOR_FOUND  = new Color(180, 255, 180);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            if (!isSelected) {
                int modelRow = table.convertRowIndexToModel(row);
                String status = (String) table.getModel().getValueAt(modelRow, 6);
                if ("STOLEN".equals(status)) {
                    setBackground(COLOR_STOLEN);
                } else if ("FOUND".equals(status)) {
                    setBackground(COLOR_FOUND);
                } else {
                    setBackground(table.getBackground());
                }
            } else {
                setBackground(table.getSelectionBackground());
            }
            return this;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel statusStatsLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu tablePopupMenu;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.table.DefaultTableModel tableModel;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JComboBox<String> statusFilterCombo;
    // End of variables declaration//GEN-END:variables
}
