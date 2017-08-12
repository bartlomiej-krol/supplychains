/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supplychains.core.helpers;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import supplychains.core.TransportConnection;
/**
 *
 * @author Branimir
 */
public class SupplyChainsUtils {
    
    public static void resetAmounts(TransportConnection[][] matrix) {
        resetAmounts(matrix, 0.0);
    }
    public static void resetAmounts(TransportConnection[][] matrix, double val) {
        int N = matrix.length;
        int M = matrix[0].length;
        for(int i = 0; i < N; i++)
            for(int j = 0; j < M; j++)
                matrix[i][j].amount = val;
    }

    public static LinkedList<TransportConnection> getDescendingOrderByProfit(TransportConnection[][] matrix) {
        LinkedList<TransportConnection> list = new LinkedList<>();        
        int N = matrix.length;
        int M = matrix[0].length;
        
        TransportConnection c = null;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                if(matrix[i][j].blocked) continue;
                int k = 0;
                while(k < list.size()) {
                    c = list.get(k);
                    if(matrix[i][j].priority < c.priority) k++;
                    else if(matrix[i][j].priority == c.priority && c.unitProfit() >= matrix[i][j].unitProfit()) k++;
                    else break;
                }
                list.add(k, matrix[i][j]);
            }
        }
        
        return list;
    }
    
    public static double getAmountOfRow(TransportConnection[][] matrix, int row) {
        int N = matrix[row].length;
        double sum = 0;
        for(int i = 0; i < N; i++) {
            sum += matrix[row][i].amount;
        }
        return sum;
    }
    
    public static double getAmountOfColumn(TransportConnection[][] matrix, int col) {
        int N = matrix.length;
        double sum = 0;
        for(int i = 0; i < N; i++) {
            sum += matrix[i][col].amount;
        }
        return sum;
    }
    
    public static String drawAmountMatrix(TransportConnection[][] matrix) {
        String s = "";
        int N = matrix.length;
        int M = matrix[0].length;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                if(j != 0) s += " ";
                s += matrix[i][j].amount;
            }
            s += "\n";
        }
        return s;
    }
    
    public static String drawCostsMatrix(TransportConnection[][] matrix) {
        String s = "";
        int N = matrix.length;
        int M = matrix[0].length;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                if(j != 0) s += " ";
                s += matrix[i][j].transportCost;
            }
            s += "\n";
        }
        return s;
    }
    
    public static String drawUnitProfitsMatrix(TransportConnection[][] matrix) {
        String s = "";
        int N = matrix.length;
        int M = matrix[0].length;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                if(j != 0) s += " ";
                s += matrix[i][j].unitProfit();
            }
            s += "\n";
        }
        return s;
    }
    
    public static String drawMatrix(double[][] matrix) {
        String s = "";
        int N = matrix.length;
        int M = matrix[0].length;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                if(j != 0) s += " ";
                s += matrix[i][j];
            }
            s += "\n";
        }
        return s;
    }

    public static double countTotalProfit(TransportConnection[][] matrix) {
        int N = matrix.length;
        int M = matrix[0].length;
        double sum = 0;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                sum += matrix[i][j].totalProfit();
            }
        }
        return sum;
    }

    public static double countTotalCosts(TransportConnection[][] matrix) {
        int N = matrix.length;
        int M = matrix[0].length;
        double sum = 0;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                sum += matrix[i][j].transportCost * matrix[i][j].amount;
            }
        }
        return sum;
    }

    public static Point getMaximumFromMatrix(double[][] matrix) {
        int N = matrix.length;
        int M = matrix[0].length;
        Point p = new Point(0,0);
        for(int i = 0; i < N; i++)
            for(int j = 0; j < M; j++)
                if(matrix[i][j] > matrix[p.x][p.y])
                    p = new Point(i, j);
        return p;            
    }

    public static double getMinimumFromVector(double[] vector) {
        double min = vector[0];
        for(double d : vector)
            if(d < min)
                min = d;
        return min;
    }

    public static List<TransportConnection> getColumnAsList(int n, TransportConnection[][] connections) {
        List<TransportConnection> list = new LinkedList<>();
        int N = connections[n].length;
        for(int i = 0; i < N; i++)
            list.add(connections[n][i]);
        return list;
    }

    public static List<TransportConnection> getRowAsList(int n, TransportConnection[][] connections) {
        List<TransportConnection> list = new LinkedList<>();
        int N = connections.length;
        for(int i = 0; i < N; i++)
            list.add(connections[i][n]);
        return list;
    }
}
