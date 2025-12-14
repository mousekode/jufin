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

/**
 *
 * @author Akbar
 */
public class jufin_jurnal extends javax.swing.JFrame {
    // Agar bisa dipakai lagi
    private int JOURNAL_ID;
    private DefaultTableModel dbModel;
    private kategori kategori = new kategori();

    /**
     * Mengambil informasi dari database berdasarkan argumen yang di passing
     */
    public jufin_jurnal(int journal_id) {
        initComponents();
        
        // Inisialisasi JOURNAL_ID
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
        dbModel.addColumn("Debit");
        dbModel.addColumn("Kredit");
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
                Object[] obj = new Object[6];
                obj[0] = RS.getInt("date");
                obj[1] = RS.getString("category");
                obj[2] = RS.getInt("transaction_id");
                if (RS.getDouble("amount") >= 0) {
                obj[3] = RS.getDouble("amount");
                } else obj[4] = RS.getDouble("amount") * (-1);
                obj[5] = RS.getString("description");
                dbModel.addRow(obj);
            }

            RS.close();
            STATE.close();
            
        } catch(SQLException e) {
                System.out.println("Terjadi Error di loadData()");
        }
    }
    
    public void insertData(String category, int date, double amount, String desc) {
        try {
            Connection DB = DBConnect.getConnect();
            String QUERY = "INSERT INTO transactions(journal_id, category, date, amount, description) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparePush = DB.prepareStatement(QUERY);
            preparePush.setInt(1, JOURNAL_ID);
            preparePush.setString(2, category);
            preparePush.setInt(3, date);
            preparePush.setDouble(4, amount);
            preparePush.setString(5, desc);
            
            preparePush.executeUpdate();
            preparePush.close();
        } catch (SQLException err) {
            System.err.println("Error: mencoba menambahkan data");
        }
    }
    
    public void updateData(int transaction_id, String category, int date, double amount, String desc) {
        try {
            Connection DB = DBConnect.getConnect();
            String QUERY = "UPDATE transactions SET journal_id = ?, category = ?, date = ?, amount = ?, description = ? WHERE transaction_id = ?";
            PreparedStatement preparePush = DB.prepareStatement(QUERY);
            
            preparePush.setInt(1, JOURNAL_ID);
            preparePush.setString(2, category);
            preparePush.setInt(3, date);
            preparePush.setDouble(4, amount);
            preparePush.setString(5, desc);
            
            preparePush.executeUpdate();
            preparePush.close();
        } catch (SQLException err) {
            System.err.println("Error: mencoba update data");
        }
    }
    
    public void deleteData(int transaction_id) {
        try {
            Connection DB = DBConnect.getConnect();
            String QUERY = "DELETE FROM transaction WHERE transaction_id = ?";
            PreparedStatement PSTMT = DB.prepareStatement(QUERY);
            
            PSTMT.setInt(1, transaction_id);
            PSTMT.executeUpdate();
            PSTMT.close();
        } catch (SQLException err) {
            System.err.println("Error: mencoba menghapus data");
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
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
        btnSave = new javax.swing.JButton();
        debugPrompt = new javax.swing.JTextField();
        jurnalDesc = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jurnalTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null}
            },
            new String [] {
                "Tanggal", "Kategori", "Ref", "Nilai", "Deskripsi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
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

        btnSave.setText("Simpan");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        debugPrompt.setEditable(false);
        debugPrompt.setBackground(new java.awt.Color(204, 204, 255));
        debugPrompt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        debugPrompt.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jurnalDesc.setFont(new java.awt.Font("Cascadia Mono", 0, 16)); // NOI18N
        jurnalDesc.setText("Nothing");
        jurnalDesc.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jurnalDesc.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jurnalDesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 748, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(debugPrompt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPrint))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jurnalTitle)
                            .addComponent(jurnalMonth))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jurnalTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jurnalMonth)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jurnalDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnDelete)
                    .addComponent(btnPrint)
                    .addComponent(btnSave)
                    .addComponent(debugPrompt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        dbModel.addRow(new Object[]{null, null, null, null, null, null});
    }//GEN-LAST:event_btnAddActionPerformed

    // Fungsi tombol hapus: menghapus baris dari jurnalTable
    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int selectedRow = jurnalTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        dbModel.removeRow(selectedRow);
    }//GEN-LAST:event_btnDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JTextField debugPrompt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jurnalDesc;
    private javax.swing.JLabel jurnalMonth;
    private javax.swing.JTable jurnalTable;
    private javax.swing.JLabel jurnalTitle;
    // End of variables declaration//GEN-END:variables
}
