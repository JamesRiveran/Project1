/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author james
 */
public class ColorCelda extends DefaultTableCellRenderer {
    private int columnIndex; // Índice de la columna que se va a personalizar
    private double tolerance; // Tolerancia para la comparación
     private double reference;

    public ColorCelda(int columnIndex, double tolerance, double reference) {
        this.columnIndex = columnIndex;
        this.tolerance = tolerance;
        this.reference = reference;
    }

    


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        if (col == columnIndex && value != null) {
            double cellValue = Double.parseDouble(value.toString());
            double validationValue = reference + tolerance;

            if (cellValue > validationValue) {
                component.setBackground(Color.RED);
                component.setForeground(Color.WHITE);
            } 
        } else {
            component.setBackground(Color.WHITE);
            component.setForeground(Color.BLACK);
        }

        return component;
    }
}
