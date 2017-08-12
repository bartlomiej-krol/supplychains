/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supplychains.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Branimir
 */
public class Customer {
    
    public double demand;
    private List<TransportConnection> connections = new ArrayList<>();
    public boolean isFake = false;
    public boolean isPrivileged = false;
    public double sellingPrice;

    public Customer(double demand, double sellingPrice) {
        this.demand = demand;
        this.sellingPrice = sellingPrice;
    }
    
    public void addConnection(TransportConnection c) {
        if(!connections.contains(c))
            connections.add(c);
    }
    
    
}
