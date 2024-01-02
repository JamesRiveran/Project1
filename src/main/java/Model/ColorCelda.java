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
    private int rows;

    public ColorCelda(int columnIndex, int rows, double tolerance, double reference) {
        this.columnIndex = columnIndex;
        this.tolerance = tolerance;
        this.reference = reference;
        this.rows = rows;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        if (col == columnIndex && row == rows) {
            double cellValue = Double.parseDouble(value.toString());
            double validationValue = reference + tolerance;
            double validationFew = reference - tolerance;

            if (cellValue > validationValue || cellValue < validationFew) {
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
