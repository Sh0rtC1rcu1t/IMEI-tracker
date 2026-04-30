package imei.track;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends javax.swing.JFrame {

    private String _imeiData;
    private String _createdAt;
    private String _updatedAt;
    private String _lastSeenCell;
    private static char[] password;
    private String name;
    private String history;
    private static ArrayList<Phones> _phonesArr;
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
        jPanel2 = new javax.swing.JPanel();

        // table setup — 6 columns
        tableModel = new javax.swing.table.DefaultTableModel(
                new Object[]{"Owner", "IMEI", "Repair History", "Created", "Updated", "Last Cell"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        jTable1 = new javax.swing.JTable(tableModel);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.setFillsViewportHeight(true);
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
                        name          = (String) tableModel.getValueAt(row, 0);
                        _imeiData     = (String) tableModel.getValueAt(row, 1);
                        history       = (String) tableModel.getValueAt(row, 2);
                        _createdAt    = (String) tableModel.getValueAt(row, 3);
                        _updatedAt    = (String) tableModel.getValueAt(row, 4);
                        _lastSeenCell = (String) tableModel.getValueAt(row, 5);
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
        setResizable(false);

        jLabel1.setText("IMEI:");

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
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton5)))
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
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        jButton1ActionPerformed(evt);
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String query = jTextField1.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        jButton2.setVisible(false);
        jButton4.setVisible(false);
        if (query.isEmpty()) return;
        for (Phones p : _phonesArr) {
            if (p.getImei().toLowerCase().contains(query)
                    || p.getName().toLowerCase().contains(query)) {
                tableModel.addRow(new Object[]{
                    p.getName(), p.getImei(), p.getHistory(),
                    safe(p.getCreatedAt()), safe(p.getUpdatedAt()), safe(p.getLastSeenCell())
                });
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        EditData editData = new EditData(this, true, _imeiData);
        editData.setNameField(name);
        editData.setImeiField(_imeiData);
        editData.setHistoryField(history);
        editData.setLastSeenCell(safe(_lastSeenCell));
        editData.setCreatedAt(safe(_createdAt));
        editData.setUpdatedAt(safe(_updatedAt));
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
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete record for IMEI: " + _imeiData + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            deleteFromDB(_imeiData);
            tableModel.setRowCount(0);
            jButton2.setVisible(false);
            jButton4.setVisible(false);
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
        if (_phonesArr == null || _phonesArr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No records loaded. Run a search first.",
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
            CsvUtils.exportPhones(_phonesArr, file);
            JOptionPane.showMessageDialog(this,
                    "Exported " + _phonesArr.size() + " record(s) to " + file.getName(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
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

    public void addToDB(String owner, String imei, String history) {
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO phones (owner, imei, history) VALUES (?, ?, ?)")) {
            ps.setString(1, owner);
            ps.setString(2, imei);
            ps.setString(3, history);
            ps.executeUpdate();
            queryDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("addToDB failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to save: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateDB(String owner, String imei, String history,
            String originalImei, String lastSeenCell) {
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE phones SET owner=?, imei=?, history=?, last_seen_cell=?, "
                     + "updated_at=NOW() WHERE imei=?")) {
            ps.setString(1, owner);
            ps.setString(2, imei);
            ps.setString(3, history);
            ps.setString(4, lastSeenCell.isEmpty() ? null : lastSeenCell);
            ps.setString(5, originalImei);
            ps.executeUpdate();
            queryDatabase();
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
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("deleteFromDB failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to delete: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void batchAddToDB(List<String[]> rows) {
        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO phones (owner, imei, history) VALUES (?, ?, ?)")) {
            for (String[] row : rows) {
                ps.setString(1, row[0]);
                ps.setString(2, row[1]);
                ps.setString(3, row[2]);
                ps.addBatch();
            }
            ps.executeBatch();
            queryDatabase();
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.table.DefaultTableModel tableModel;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
