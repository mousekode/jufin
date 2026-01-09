/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package oop_class.jufin;

import java.awt.Color;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.DriverManager;

// Java Jasper
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Akbar
 */
public class jufin_jurnal extends javax.swing.JFrame {
    // Agar bisa dipakai lagi
    private int JOURNAL_ID;
    private DefaultTableModel dbModel;
    private kategori kategori = new kategori();
    private boolean isUpdateMode = false;
    private Object toUpdate = -1;

    /**
     * Mengambil informasi dari database berdasarkan argumen yang di passing
     */
    public jufin_jurnal(int journal_id) {
        initComponents();
        
        // Inisialisasi global
        JOURNAL_ID = journal_id;
        
        // Menambahkan JComboBox ke jurnalTable
        JComboBox<String> comboKategori = new JComboBox<>(kategori.list);
        
        // Define dbModel
        dbModel = new DefaultTableModel();
        
        // Define jurnalTable as dbModel
        jurnalTable.setModel(dbModel);
        dbModel.addColumn("Tanggal");
        dbModel.addColumn("Kategori");
        dbModel.addColumn("Ref");
        dbModel.addColumn("Nilai");
        dbModel.addColumn("Deskripsi");
        
        TableColumn kolomKategori = jurnalTable.getColumnModel().getColumn(1);
        kolomKategori.setCellEditor(new DefaultCellEditor(comboKategori));
        
        // Mengganti title, subtitle dan deskripsi
        try {
            Connection DB = DBConnect.getConnect();
            String QUERY = "SELECT journal_name, created_in, journal_desc FROM journals WHERE journal_id = ?";
            PreparedStatement STATE = DB.prepareStatement(QUERY);
            STATE.setInt(1, journal_id);
            
            ResultSet RS = STATE.executeQuery();
            
            // Bagian perlu optimisasi
            while (RS.next()) {
                jurnalTitle.setText(RS.getString("journal_name"));
                jurnalMonth.setText(month.getNameFromValue(RS.getInt("created_in")));
                jurnalDesc.setText(RS.getString("journal_desc"));
            }
            
            STATE.close();
            RS.close();
        } catch (SQLException err) {
            System.err.print("Err di constructor");
        } 
        finally {
            loadData();
        }
    }
    
    // Fungsi ini memuat data dari database (db_jufin) ke tabel (var jurnalTable)
    public void loadData() {
        // Clear shi
        clearShi();
        
        // Menghapus data
        dbModel.getDataVector().removeAllElements();
        
        // Info bahwa data telah kosong
        dbModel.fireTableDataChanged();
        
        // Memastikan nilai yang diambil dari db_jufin berdasarkan JOURNAL_ID yang tepat
        try {
            Connection DB = DBConnect.getConnect();
            String QUERY = "SELECT * FROM transactions WHERE journal_id = ? ORDER BY transactions.date ASC";
            PreparedStatement STATE = DB.prepareStatement(QUERY);
            STATE.setInt(1, JOURNAL_ID);
            ResultSet RS = STATE.executeQuery();
            while(RS.next()){
                // lakukan penelusuran baris
                Object[] obj = new Object[5];
                obj[0] = RS.getInt("date");
                obj[1] = RS.getString("category");
                obj[2] = RS.getInt("transaction_id");
                obj[3] = RS.getDouble("amount");
                obj[4] = RS.getString("description");
                dbModel.addRow(obj);
            }

            RS.close();
            STATE.close();
            
        } catch(SQLException err) {
            System.out.println("Terjadi Error di loadData()");
            System.out.println("Error code: " + err.getErrorCode());
            System.out.println("SQL state: " + err.getSQLState());
            System.out.println("Message: " + err.getMessage());
            err.printStackTrace(); // optional, shows full stack trace
        }
    }
    
    public void insertData(Object category, Object date, Object amount, Object desc) {
        try {
            System.out.println("Update mode is false");
            Connection DB = DBConnect.getConnect();
            String QUERY = "INSERT INTO transactions(journal_id, category, date, amount, description) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparePush = DB.prepareStatement(QUERY);
            preparePush.setInt(1, JOURNAL_ID);
            preparePush.setObject(2, category);
            preparePush.setObject(3, date);
            preparePush.setObject(4, amount);
            preparePush.setObject(5, desc);
            preparePush.executeUpdate();
            preparePush.close();
            
        } catch (SQLException err) {
            System.err.println("Error: mencoba menambahkan data");
            System.out.println("Error code: " + err.getErrorCode());
            System.out.println("SQL state: " + err.getSQLState());
            System.out.println("Message: " + err.getMessage());
            err.printStackTrace(); // optional, shows full stack trace
        } finally {
            loadData();
        }
    }
    
    public void updateData(Object transaction_id, Object category, Object date, Object amount, Object desc) {
        try {
            Connection DB = DBConnect.getConnect();
            String QUERY = "UPDATE transactions SET journal_id = ?, category = ?, date = ?, amount = ?, description = ? WHERE transaction_id = ?";
            PreparedStatement preparePush = DB.prepareStatement(QUERY);

            preparePush.setObject(6, transaction_id);
            preparePush.setObject(1, JOURNAL_ID);
            preparePush.setObject(2, category);
            preparePush.setObject(3, date);
            preparePush.setObject(4, amount);
            preparePush.setObject(5, desc);
            preparePush.executeUpdate();
            preparePush.close();
            
        } catch (SQLException err) {
            System.err.println("Error: mencoba mengupdate data");
            System.out.println("Error code: " + err.getErrorCode());
            System.out.println("SQL state: " + err.getSQLState());
            System.out.println("Message: " + err.getMessage());
            err.printStackTrace(); // optional, shows full stack trace
        } finally {
            loadData();
        }
    }
    
    public void deleteData(Object transaction_id) {
        try {
            Connection DB = DBConnect.getConnect();
            String QUERY = "DELETE FROM transactions WHERE transaction_id = ?";
            PreparedStatement PSTMT = DB.prepareStatement(QUERY);
            
            PSTMT.setObject(1, transaction_id);
            PSTMT.executeUpdate();
            PSTMT.close();
        } catch (SQLException err) {
            System.err.println("Error: mencoba menghapus data");
        } finally {
            loadData();
        }
    }
    
    // Membersihkan query dan seleksi
    public void clearShi() {
        jurnalTable.clearSelection();
        btnAdd.setText("Tambah");
        jurnalTable.clearSelection();
        spinDate.setValue(1);
        labelUpd.setText("");
        comboKategori.setSelectedIndex(0);
        fieldNilai.setText("");
        fieldDesc.setText("");
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the jForm Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jurnalTable = new javax.swing.JTable();
        jurnalTitle = new javax.swing.JLabel();
        jurnalMonth = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        jurnalDesc = new javax.swing.JLabel();
        fieldNilai = new javax.swing.JTextField();
        comboKategori = new javax.swing.JComboBox<>();
        spinDate = new javax.swing.JSpinner();
        fieldDesc = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        labelUpd = new javax.swing.JLabel();
        btnReset = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jurnalTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null}
            },
            new String [] {
                "Tanggal", "Kategori", "Ref", "Nilai", "Deskripsi"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jurnalTable.setToolTipText("");
        jurnalTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jurnalTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jurnalTable);

        jurnalTitle.setFont(new java.awt.Font("Cascadia Mono", 1, 28)); // NOI18N
        jurnalTitle.setText("Title");

        jurnalMonth.setFont(new java.awt.Font("Cascadia Mono", 1, 18)); // NOI18N
        jurnalMonth.setText("Subtitle");

        btnAdd.setText("Tambah");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnDelete.setText("Hapus");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnPrint.setText("Cetak");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        jurnalDesc.setFont(new java.awt.Font("Cascadia Mono", 0, 16)); // NOI18N
        jurnalDesc.setText("Nothing");
        jurnalDesc.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jurnalDesc.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        comboKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Gaji", "Grosir", "Lain", "Tagihan" }));
        comboKategori.setToolTipText("");

        spinDate.setModel(new javax.swing.SpinnerNumberModel(1, 1, 30, 1));

        jLabel1.setFont(new java.awt.Font("Cascadia Mono", 0, 12)); // NOI18N
        jLabel1.setText("Tanggal");

        jLabel2.setFont(new java.awt.Font("Cascadia Mono", 0, 12)); // NOI18N
        jLabel2.setText("Kategori");

        jLabel3.setFont(new java.awt.Font("Cascadia Mono", 0, 12)); // NOI18N
        jLabel3.setText("Nilai");

        jLabel4.setFont(new java.awt.Font("Cascadia Mono", 0, 12)); // NOI18N
        jLabel4.setText("Deskripsi");

        labelUpd.setFont(new java.awt.Font("Cascadia Mono", 0, 12)); // NOI18N

        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jurnalDesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jurnalTitle)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(spinDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fieldNilai, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(fieldDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnAdd)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnReset)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnDelete)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnPrint))
                                    .addComponent(labelUpd))))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jurnalMonth)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jurnalTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jurnalMonth)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jurnalDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(labelUpd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnDelete)
                    .addComponent(btnPrint)
                    .addComponent(fieldNilai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset))
                .addGap(38, 38, 38)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
    // Force Jasper to use the JVM's default font mapping
    System.setProperty("net.sf.jasperreports.extension.registry.factory.fonts", "net.sf.jasperreports.engine.fonts.SimpleFontExtensionsRegistryFactory");
    try {
        Connection DB = DBConnect.getConnect();
        // 1. Path to your compiled report file (.jasper)
        String reportPath = "C:\\Users\\Akbar\\OneDrive\\Documents\\NetBeansProjects\\jufin\\src\\main\\java\\oop_class\\jufin\\jasper\\JufinReport.jasper";

        // 2. The ID of the journal you want to print
        int journalID = JOURNAL_ID; // This usually comes from your JTable or TextField

        // 3. Put the ID into the Parameter Map
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("SELECTED_JOURNAL_ID", journalID);

        // 4. Fill the report with data
        JasperPrint jp = JasperFillManager.fillReport(reportPath, parameters, DB);

        // 5. Show the report in a popup window
        JasperViewer.viewReport(jp, false); 

    } catch (JRException e) {
        e.printStackTrace();
}
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        try {
            // Mengecek jika fieldNilai kosong, jika kosong throw error lalu return
            if (fieldNilai.getText() == null || fieldNilai.getText().trim().isEmpty()) {
                throw new Exception("Error: nilai tidak boleh kosong");
            }
            
            Object[] tbObj = {spinDate.getValue(), comboKategori.getSelectedItem(), "#", 0, fieldDesc.getText()};
            
            if (comboKategori.getSelectedIndex() == 0) {
                tbObj[3] = (String) fieldNilai.getText();
            } else tbObj[3] = "-" + (String) fieldNilai.getText();
            
            int selectedRow = jurnalTable.getSelectedRow();
            if (selectedRow == -1) {
                insertData(tbObj[1], tbObj[0], tbObj[3], tbObj[4]);
            } else updateData(jurnalTable.getValueAt(selectedRow, jurnalTable.getColumnModel().getColumnIndex("Ref")), tbObj[1], tbObj[0], tbObj[3], tbObj[4]);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return;
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void jurnalTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jurnalTableMouseClicked
        int selectedRow = jurnalTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Mengisi field yang tertera
        spinDate.setValue(jurnalTable.getValueAt(selectedRow, jurnalTable.getColumnModel().getColumnIndex("Tanggal")));
        comboKategori.setSelectedItem(jurnalTable.getValueAt(selectedRow, jurnalTable.getColumnModel().getColumnIndex("Kategori")));
        if (comboKategori.getSelectedItem() != kategori.list[0]) {
            fieldNilai.setText(String.valueOf(jurnalTable.getValueAt(selectedRow, jurnalTable.getColumnModel().getColumnIndex("Nilai"))).replace("-", ""));
        } else fieldNilai.setText(String.valueOf(jurnalTable.getValueAt(selectedRow, jurnalTable.getColumnModel().getColumnIndex("Nilai"))));
        fieldDesc.setText(String.valueOf(jurnalTable.getValueAt(selectedRow, jurnalTable.getColumnModel().getColumnIndex("Deskripsi"))));
        
        // Mengubah status tombol
        btnAdd.setText("Update");
        
        // Memberi petunjuk ID yang dipilih
        labelUpd.setText("Selected ref: " + String.valueOf(jurnalTable.getValueAt(selectedRow, jurnalTable.getColumnModel().getColumnIndex("Ref"))));
    }//GEN-LAST:event_jurnalTableMouseClicked

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        clearShi();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int selectedRow = jurnalTable.getSelectedRow();
        if (selectedRow == -1) {
            System.err.println("Error: tidak ada yang dipilih");
            return;
        }
        
        deleteData(jurnalTable.getValueAt(selectedRow, jurnalTable.getColumnModel().getColumnIndex("Ref")));
    }//GEN-LAST:event_btnDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnReset;
    private javax.swing.JComboBox<String> comboKategori;
    private javax.swing.JTextField fieldDesc;
    private javax.swing.JTextField fieldNilai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jurnalDesc;
    private javax.swing.JLabel jurnalMonth;
    private javax.swing.JTable jurnalTable;
    private javax.swing.JLabel jurnalTitle;
    private javax.swing.JLabel labelUpd;
    private javax.swing.JSpinner spinDate;
    // End of variables declaration//GEN-END:variables
}
