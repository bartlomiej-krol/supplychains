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
public class Supplier {
    
    public double supply;
    private List<TransportConnection> connections = new ArrayList<>();
    public boolean isFake = false;
    public boolean isPrivileged = false;
    public double purchasePrice;

    public Supplier(double supply, double purchasePrice) {
        this.supply = supply;
        this.purchasePrice = purchasePrice;
    }
    
    public void addConnection(TransportConnection c) {
        if(!connections.contains(c))
            connections.add(c);
    }
    
    
}
