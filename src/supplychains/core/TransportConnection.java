/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supplychains.core;

/**
 *
 * @author Branimir
 */
public class TransportConnection {

    public final static int PRIORITY_PRIVILAGED = 10;
    public final static int PRIORITY_1 = 1;
    public final static int PRIORITY_2 = 2;
    public final static int PRIORITY_3 = 3;
    public final static int PRIORITY_FAKE = 0;
    public Supplier supplier;
    public Customer customer;
    public double transportCost;
    public boolean blocked = false;
    public double amount = 0.0;
    public int priority = TransportConnection.PRIORITY_1;
    public int x;
    public int y;
    
    public TransportConnection(Supplier supplier, Customer customer, double cost, int x, int y) {
        this.customer = customer;
        this.supplier = supplier;
        this.transportCost = cost;
        this.x = x;
        this.y = y;
    }
    
    public double totalProfit() {
        return amount*unitProfit();
    }
    
    public double unitProfit() {
        if(supplier.isFake || customer.isFake) return 0.0;
        return customer.sellingPrice - supplier.purchasePrice - transportCost;
    }
}
