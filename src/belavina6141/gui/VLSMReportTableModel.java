package belavina6141.gui;

import belavina6141.Calculator.VLSMReport;
import belavina6141.Calculator.VLSMReportSubnet;

import javax.swing.table.AbstractTableModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olga Belavina on 2016-04-08.
 */
public class VLSMReportTableModel extends AbstractTableModel {

    private List<VLSMReportSubnet> vlsmReport;

    private String[] columnNames = {"Subnet",
            "Network Address",
            "Hosts",
            "Mask",
            "Range",
            "Broadcast"
    };

    public VLSMReportTableModel() { vlsmReport = new ArrayList<>(); }

    public void setData(List<VLSMReportSubnet> vlsmReport){
        this.vlsmReport = vlsmReport;
    }


    public String getColumnName(int col) {
        return columnNames[col];
    }

    public void saveCSVFile(File file){

        /* Check if the user didn't enter extension */
        if(Utils.getFileExtension(file.getName()) == null ) {
            file = new File(file + ".csv");
        }

        BufferedWriter bw = null;

        try {
            if (!file.exists()) {
                file.createNewFile();
            }


            bw = new BufferedWriter(new FileWriter(file));

            // Print headers :
            int colCount = getColumnCount() - 1;
            for(int i = 0; i < colCount; i++){
                bw.write(getColumnName(i) + ",");
            }
            bw.write(getColumnName(colCount) + "\n");

            // Print the table
            for (int i = 0; i < getRowCount(); i++) {
                // bw.write();
                for (int j = 0; j < colCount; j++) {
                    bw.write(String.valueOf(getValueAt(i,j) + ","));
                }

                bw.write(String.valueOf(getValueAt(i,colCount)) + "\n");
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if(bw != null ) {
                try {
                    bw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }

    }

    @Override
    public int getRowCount() {
        return vlsmReport.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        VLSMReportSubnet report = vlsmReport.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return report.getSubnetName();
            case 1:
                return report.getNetAddress();
            case 2:
                return report.getHosts();
            case 3:
                return report.getSubnetMask();
            case 4:
                return report.getRange();
            case 5:
                return report.getBroadcastAddress();
        }

        /* Should never be reached */
        return null;
    }
}
