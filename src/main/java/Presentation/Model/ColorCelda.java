/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Presentation.Model;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author james
 */
public class ColorCelda extends DefaultTableCellRenderer {

    /*private int columnIndex; // Índice de la columna que se va a personalizar
    private int tolerance; // Tolerancia para la comparación
    private int reference;
    private List<Integer> rowsToHighlight;

    public ColorCelda(int columnIndex, int tolerance, List<Integer> rowsToHighlight, int reference) {
        this.columnIndex = columnIndex;
        this.tolerance = tolerance;
        this.rowsToHighlight = rowsToHighlight;
        this.reference = reference;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public int getTolerance() {
        return tolerance;
    }

    public void setTolerance(int tolerance) {
        this.tolerance = tolerance;
    }

    public int getReference() {
        return reference;
    }

    public void setReference(int reference) {
        this.reference = reference;
    }

    public List<Integer> getRowsToHighlight() {
        return rowsToHighlight;
    }

    public void setRowsToHighlight(List<Integer> rowsToHighlight) {
        this.rowsToHighlight = rowsToHighlight;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        if (col == columnIndex && rowsToHighlight.contains(row)) {
            int cellValue = Integer.parseInt(value.toString());
            int validationValue = reference + tolerance;
            int validationFew = reference - tolerance;

            if (cellValue > validationValue || cellValue < validationFew) {
                component.setBackground(Color.RED);
                component.setForeground(Color.WHITE);
            }
        } else {
            component.setBackground(Color.WHITE);
            component.setForeground(Color.BLACK);
        }

        return component;
    }*/
     
    
    
     private int columnIndex; // Índice de la columna que se va a personalizar
    private int tolerance; // Tolerancia para la comparación
    private int reference;
    private int rows;

    public ColorCelda(int columnIndex, int rows, int tolerance, int reference) {
        this.columnIndex = columnIndex;
        this.tolerance = tolerance;
        this.reference = reference;
        this.rows = rows;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        if (col == columnIndex && row == rows) {
            int cellValue = Integer.parseInt(value.toString());
            int validationValue = reference + tolerance;
            int validationFew = reference - tolerance;

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
