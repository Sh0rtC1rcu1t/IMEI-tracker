package imei.track;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JOptionPane;

public class EditData extends javax.swing.JFrame {

    private final MainFrame mainFrame;
    private final boolean isEditMode;
    private final String originalImei;

    private String name;
    private String imei;
    private String history;

    public EditData(MainFrame mainFrame, boolean isEditMode, String originalImei) {
        this.mainFrame   = mainFrame;
        this.isEditMode  = isEditMode;
        this.originalImei = originalImei;
        initComponents();
        setTitle(isEditMode ? "Edit Phone Record" : "Add New Phone");
        // Timestamp rows only meaningful in edit mode
        jLabelCreatedTitle.setVisible(isEditMode);
        jLabelCreated.setVisible(isEditMode);
        jLabelUpdatedTitle.setVisible(isEditMode);
        jLabelUpdated.setVisible(isEditMode);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1      = new javax.swing.JLabel();
        jTextField1  = new javax.swing.JTextField();
        jLabel2      = new javax.swing.JLabel();
        jTextField2  = new javax.swing.JTextField();
        jLabelTac    = new javax.swing.JLabel();
        jLabel4      = new javax.swing.JLabel();
        jTextField3  = new javax.swing.JTextField();
        jLabel3      = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1   = new javax.swing.JTextArea();
        jLabelCreatedTitle = new javax.swing.JLabel();
        jLabelCreated      = new javax.swing.JLabel();
        jLabelUpdatedTitle = new javax.swing.JLabel();
        jLabelUpdated      = new javax.swing.JLabel();
        jButton1     = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Row 0: Owner
        g.gridx = 0; g.gridy = 0; g.weightx = 0; g.gridwidth = 1;
        jLabel1.setText("Owner's Name:");
        getContentPane().add(jLabel1, g);
        g.gridx = 1; g.weightx = 1;
        getContentPane().add(jTextField1, g);

        // Row 1: IMEI
        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        jLabel2.setText("IMEI (15 digits):");
        getContentPane().add(jLabel2, g);
        g.gridx = 1; g.weightx = 1;
        getContentPane().add(jTextField2, g);

        // Row 2: TAC info (spans 2 cols)
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; g.weightx = 1;
        jLabelTac.setText(" ");
        jLabelTac.setFont(jLabelTac.getFont().deriveFont(Font.ITALIC));
        getContentPane().add(jLabelTac, g);
        g.gridwidth = 1;

        // Row 3: Last Seen Cell
        g.gridx = 0; g.gridy = 3; g.weightx = 0;
        jLabel4.setText("Last Seen Cell:");
        getContentPane().add(jLabel4, g);
        g.gridx = 1; g.weightx = 1;
        jTextField3.setToolTipText(
                "Format from SDR scan: MCC:MNC:LAC:CID (e.g. 310:410:12345:6789)");
        getContentPane().add(jTextField3, g);

        // Row 4: Repair History label
        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        jLabel3.setText("Repair History:");
        getContentPane().add(jLabel3, g);
        g.gridwidth = 1;

        // Row 5: History text area
        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        g.weighty = 1; g.fill = GridBagConstraints.BOTH;
        jTextArea1.setColumns(28);
        jTextArea1.setRows(7);
        jTextArea1.setLineWrap(true);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTextArea1);
        getContentPane().add(jScrollPane1, g);
        g.weighty = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.gridwidth = 1;

        // Row 6: Created (edit mode only)
        g.gridx = 0; g.gridy = 6; g.weightx = 0;
        jLabelCreatedTitle.setText("Created:");
        getContentPane().add(jLabelCreatedTitle, g);
        g.gridx = 1; g.weightx = 1;
        jLabelCreated.setText("-");
        jLabelCreated.setForeground(Color.DARK_GRAY);
        getContentPane().add(jLabelCreated, g);

        // Row 7: Updated (edit mode only)
        g.gridx = 0; g.gridy = 7; g.weightx = 0;
        jLabelUpdatedTitle.setText("Updated:");
        getContentPane().add(jLabelUpdatedTitle, g);
        g.gridx = 1; g.weightx = 1;
        jLabelUpdated.setText("-");
        jLabelUpdated.setForeground(Color.DARK_GRAY);
        getContentPane().add(jLabelUpdated, g);

        // Row 8: Commit button (centred)
        g.gridx = 0; g.gridy = 8; g.gridwidth = 2; g.weightx = 0;
        g.fill   = GridBagConstraints.NONE;
        g.anchor = GridBagConstraints.CENTER;
        jButton1.setText("Commit Changes");
        getContentPane().add(jButton1, g);

        // --- Listeners ---

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jTextField2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateTacLabel(jTextField2.getText().trim());
            }
        });

        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        jTextField2.requestFocusInWindow();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        updateTacLabel(jTextField2.getText().trim());
        jTextField3.requestFocusInWindow();
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        name    = jTextField1.getText().trim();
        imei    = jTextField2.getText().trim();
        history = jTextArea1.getText().trim();
        String lastSeenCell = jTextField3.getText().trim();

        if (imei.isEmpty()) {
            JOptionPane.showMessageDialog(this, "IMEI cannot be empty.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            jTextField2.requestFocusInWindow();
            return;
        }
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Owner name cannot be empty.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            jTextField1.requestFocusInWindow();
            return;
        }
        if (!ImeiUtils.isValidImei(imei)) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "IMEI \"" + imei + "\" does not pass the Luhn check.\n"
                    + "This may indicate a typo. Save anyway?",
                    "IMEI Validation Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                jTextField2.requestFocusInWindow();
                return;
            }
        }

        if (isEditMode) {
            mainFrame.updateDB(name, imei, history, originalImei, lastSeenCell);
        } else {
            mainFrame.addToDB(name, imei, history);
        }
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void updateTacLabel(String rawImei) {
        if (rawImei.isEmpty()) {
            jLabelTac.setText(" ");
            jLabelTac.setForeground(Color.DARK_GRAY);
            return;
        }
        boolean valid = ImeiUtils.isValidImei(rawImei);
        String tac    = ImeiUtils.tacDescription(rawImei);
        if (valid) {
            jLabelTac.setText("✓ Valid IMEI  •  " + tac);
            jLabelTac.setForeground(new Color(0, 128, 0));
        } else {
            jLabelTac.setText("✗ Invalid IMEI  •  " + tac);
            jLabelTac.setForeground(Color.RED);
        }
    }

    // -------------------------------------------------------------------------
    // Public setters/getters (called from MainFrame)
    // -------------------------------------------------------------------------

    public void setNameField(String s)     { jTextField1.setText(s); }
    public void setImeiField(String s)     { jTextField2.setText(s); updateTacLabel(s); }
    public void setHistoryField(String s)  { jTextArea1.setText(s); }
    public void setLastSeenCell(String s)  { jTextField3.setText(s); }
    public void setCreatedAt(String s)     { jLabelCreated.setText(s); }
    public void setUpdatedAt(String s)     { jLabelUpdated.setText(s); }

    public String getNameField()     { return jTextField1.getText().trim(); }
    public String getImeiField()     { return jTextField2.getText().trim(); }
    public String getHistoryField()  { return jTextArea1.getText().trim(); }
    public String getLastSeenCell()  { return jTextField3.getText().trim(); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelCreated;
    private javax.swing.JLabel jLabelCreatedTitle;
    private javax.swing.JLabel jLabelTac;
    private javax.swing.JLabel jLabelUpdated;
    private javax.swing.JLabel jLabelUpdatedTitle;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
