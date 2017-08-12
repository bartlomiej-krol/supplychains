package supplychains.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import supplychains.core.exceptions.SupplyChainsException;
import supplychains.core.helpers.SupplyChainsUtils;

/**
 *
 * @author Branimir
 */
public class SupplyChains {

    private SupplyChainsLogger logger = null;
    private ArrayList<Supplier> suppliers = null;
    private ArrayList<Customer> customers = null;
    private TransportConnection[][] connections = null;
    private double summaryCosts = 0.0;
    private int suppliersAmount;
    private int customersAmount;

    private double[] vectorA;
    private double[] vectorB;
    private double[][] matrixD;
    private double demand, supply;
    private boolean brokerProblem = false;

    private void init() throws SupplyChainsException {
        if (suppliers == null || suppliers.isEmpty()) {
            throw new SupplyChainsException("Suppliers not found");
        }
        if (customers == null || customers.isEmpty()) {
            throw new SupplyChainsException("Customers not found");
        }
        if (connections == null) {
            throw new SupplyChainsException("Connections not found");
        }
        if (connections.length != suppliers.size()) {
            throw new SupplyChainsException("Connections costs matrix do not match to amount of suppliers.");
        }
        if (connections[0].length != customers.size()) {
            throw new SupplyChainsException("Connections costs matrix do not match to amount of customers.");
        }

        demand = 0.0;
        supply = 0.0;
        for (Supplier s : suppliers) {
            supply += s.supply;
        }
        for (Customer c : customers) {
            demand += c.demand;
        }
        
        logInfo("Demand: " + demand);
        logInfo("Supply: " + supply);
        
        logInfo("supp: " + suppliers.size());
        logInfo("cust: " + customers.size());
        
        if(brokerProblem) {
                Customer cust =new Customer(supply, 0);
                cust.isFake = true;
                customers.add(cust);
                Supplier supp = new Supplier(demand, 0);
                supp.isFake = true;
                suppliers.add(supp);
        }
        else {
            if (demand < supply) {
                Customer cust =new Customer(supply - demand, 0);
                cust.isFake = true;
                customers.add(cust);
            } else if (supply < demand) {
                Supplier supp = new Supplier(demand - supply, 0);
                supp.isFake = true;
                suppliers.add(supp);
            }
        }
        customersAmount = customers.size();
        suppliersAmount = suppliers.size();
        if (brokerProblem || demand != supply) {
            TransportConnection[][] tmp = new TransportConnection[suppliersAmount][customersAmount];
            for (int i = 0; i < suppliersAmount; i++) {
                for (int j = 0; j < customersAmount; j++) {
                    if (i < connections.length && j < connections[0].length) {
                        tmp[i][j] = connections[i][j];
                        if(tmp[i][j].supplier.isPrivileged || tmp[i][j].customer.isPrivileged)
                            tmp[i][j].priority = TransportConnection.PRIORITY_PRIVILAGED;
                    } else {
                        tmp[i][j] = new TransportConnection(suppliers.get(i), customers.get(j), 0, i, j);
                        tmp[i][j].priority = TransportConnection.PRIORITY_FAKE;
                    }
                    if(tmp[i][j].supplier.isFake && tmp[i][j].customer.isPrivileged
                            || tmp[i][j].customer.isFake && tmp[i][j].supplier.isPrivileged) {
                        tmp[i][j].blocked = true;
                    }
                }
            }
            connections = tmp;
        }
        matrixD = new double[suppliersAmount][customersAmount];
        vectorA = new double[suppliersAmount];
        vectorB = new double[customersAmount];
        logInfo("supp: " + suppliers.size());
        logInfo("cust: " + customers.size());
    }

    public void solve() throws SupplyChainsException {
        init();
        logInfo(SupplyChainsUtils.drawCostsMatrix(connections));
        logInfo();

        SupplyChainsUtils.resetAmounts(connections);
        naiveSolution();

        logInfo();
        int iter = 0;
        Point minP = null;
        //double a0 = -connectionCosts[minP.x][minP.y];

        while (true) {
            iter++;
            logInfo("\nIteracja: " + iter);
            logInfo(SupplyChainsUtils.drawAmountMatrix(connections));
            summaryCosts = SupplyChainsUtils.countTotalCosts(connections);
            logInfo("K" + iter + ": " + summaryCosts);
            recalculateABVectors(0.0);
            recalculateMatrixD();
            minP = SupplyChainsUtils.getMaximumFromMatrix(matrixD);
            // logInfo(vectorA);
            // logInfo(vectorB);
            logInfo(SupplyChainsUtils.drawMatrix(matrixD));
            List<TransportConnection> relocatePath = findBestRelocationPath(connections[minP.x][minP.y]);
            double pathProfit = 0.0;
            if(relocatePath != null)
                pathProfit = countTotalProfitForRelocationPath(relocatePath);
            
            logInfo(String.valueOf(pathProfit));
            if (pathProfit > 0.0) {
                try {
                    relocateSuppliesByRelocationPath(relocatePath);
                } catch (Exception ex) {
                    Logger.getLogger(SupplyChains.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
            } else {
                break;
            }
            summaryCosts = SupplyChainsUtils.countTotalCosts(connections);
        }
    }

    public void solve2() throws SupplyChainsException {
        logInfo(SupplyChainsUtils.drawCostsMatrix(connections));
        logInfo();

        suppliersAmount = suppliers.size();
        customersAmount = customers.size();
        vectorA = new double[suppliersAmount];
        vectorB = new double[customersAmount];
        matrixD = new double[suppliersAmount][customersAmount];
        
        SupplyChainsUtils.resetAmounts(connections);
        northWestSolution();
        logInfo();
        int iter = 0;
        Point minP = null;
        //double a0 = -connectionCosts[minP.x][minP.y];

        while (true) {
            iter++;
            logInfo("\nIteracja: " + iter);
            logInfo(SupplyChainsUtils.drawAmountMatrix(connections));
            summaryCosts = SupplyChainsUtils.countTotalCosts(connections);
            logInfo("K" + iter + ": " + summaryCosts);
            recalculateABVectors(0.0);
            recalculateMatrixD();
            minP = SupplyChainsUtils.getMaximumFromMatrix(matrixD);
            // logInfo(vectorA);
            // logInfo(vectorB);
            logInfo(SupplyChainsUtils.drawMatrix(matrixD));
            List<TransportConnection> relocatePath = findBestRelocationPath(connections[minP.x][minP.y]);
            double pathProfit = 0.0;
            if(relocatePath != null)
                pathProfit = countTotalProfitForRelocationPath(relocatePath);
            
            logInfo(String.valueOf(pathProfit));
            if (pathProfit > 0.0) {
                try {
                    relocateSuppliesByRelocationPath(relocatePath);
                } catch (Exception ex) {
                    Logger.getLogger(SupplyChains.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
            } else {
                break;
            }
            summaryCosts = SupplyChainsUtils.countTotalCosts(connections);
        }
    }
    
    private void naiveSolution() {
        LinkedList<TransportConnection> orderedConList = SupplyChainsUtils.getDescendingOrderByProfit(connections);

        Iterator<TransportConnection> iter = orderedConList.iterator();
        while (iter.hasNext()) {
            TransportConnection con = iter.next();
            double amount = Math.min(
                    (con.supplier.supply - SupplyChainsUtils.getAmountOfRow(connections, con.x)),
                    (con.customer.demand - SupplyChainsUtils.getAmountOfColumn(connections, con.y))
            );
            con.amount = amount;
            //logInfo(String.format("(%d, %d) - %d |%f", con.x, con.y, con.priority, con.unitProfit()));
        }
    }

    private void recalculateMatrixD() {
        for (int i = 0; i < suppliersAmount; i++) {
            for (int j = 0; j < customersAmount; j++) {
                if (!connections[i][j].blocked && connections[i][j].amount == 0) {
                    matrixD[i][j] = connections[i][j].unitProfit() - vectorA[i] - vectorB[j];
                } else {
                    matrixD[i][j] = 0;
                }
            }
        }
    }

    private void recalculateABVectors(double a0) {
        Boolean[] ba = new Boolean[vectorA.length];
        Boolean[] bb = new Boolean[vectorB.length];
        Arrays.fill(ba, 0, ba.length, false);
        Arrays.fill(bb, 0, bb.length, false);
        vectorA[0] = a0;
        ba[0] = true;
        int k = 1, k_prev;
        while (true) {
            k_prev = k;
            for (int i = 0; i < vectorA.length; i++) {
                if (!ba[i]) {
                    for (int j = 0; j < vectorB.length; j++) {
                        if (bb[j] && connections[i][j].amount > 0) {
                            vectorA[i] = connections[i][j].unitProfit() - vectorB[j];
                            ba[i] = true;
                            k++;
                        }
                    }
                }
                if (ba[i]) {
                    for (int j = 0; j < vectorB.length; j++) {
                        if (!bb[j] && connections[i][j].amount > 0) {
                            vectorB[j] = connections[i][j].unitProfit() - vectorA[i];
                            bb[j] = true;
                            k++;
                        }
                    }
                }
            }
            if (k_prev == k) {
                for (int i = 0; i < vectorA.length; i++) {
                    if (!ba[i]) {
                        vectorA[i] = a0;
                        ba[i] = true;
                        k++;
                        break;
                    }
                }
            }
            if (k_prev == k) {
                break;
            }
        }
    }

    /**
     * GETTERS AND SETTERS
     */
    public ArrayList<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(ArrayList<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
    }

    public TransportConnection[][] getConnectionCosts() {
        return connections;
    }

    public void setConnections(TransportConnection[][] connections) {
        this.connections = connections;
    }

    public double getSummaryCosts() {
        return summaryCosts;
    }

    public void setBrokerProblem() {
        brokerProblem = true;
    }

    private List<TransportConnection> findBestRelocationPath(TransportConnection start) {
        LinkedList<TransportConnection> list = new LinkedList<>();
        return findBestLoopedRelocationPath(start, list, true, true);
    }

    private LinkedList<TransportConnection> findBestLoopedRelocationPath(TransportConnection currentConn, LinkedList<TransportConnection> listMain, boolean verticalDirection, boolean nextNeedSupply) {
        listMain.add(currentConn);
        List<TransportConnection> toCheck = null;
        if (verticalDirection) {
            toCheck = SupplyChainsUtils.getColumnAsList(currentConn.x, connections);
        } else {
            toCheck = SupplyChainsUtils.getRowAsList(currentConn.y, connections);
        }
        if (currentConn != listMain.get(0) && toCheck.contains(listMain.get(0)) && !nextNeedSupply) {
            return listMain;
        }

        LinkedList<TransportConnection> bestList = null, tmpList = null;
        for (TransportConnection c : toCheck) {
            if (c != currentConn && !listMain.contains(c) && !c.blocked) {
                if (!nextNeedSupply || c.amount > 0) {
                    tmpList = findBestLoopedRelocationPath(c, (LinkedList<TransportConnection>) listMain.clone(),
                            !verticalDirection, !nextNeedSupply);
                    if (tmpList != null && countUnitProfitForRelocationPath(tmpList) > 0 && (bestList == null || comparePaths(tmpList, bestList))) {//countTotalProfitForRelocationPath(tmpList) > countTotalProfitForRelocationPath(bestList))) {
                        bestList = tmpList;
                    }
                }
            }
        }
        return bestList;
    }

    private double countTotalProfitForRelocationPath(List<TransportConnection> tmpList) {
        double amountToMove = countAmountToMoveForRelocationPath(tmpList);
        double unitProfit = countUnitProfitForRelocationPath(tmpList);
        return unitProfit * amountToMove;
    }

    private double countAmountToMoveForRelocationPath(List<TransportConnection> tmpList) {
        double amountToMove = tmpList.get(1).amount;
        Iterator iter = tmpList.iterator();
        for (int i = 1; iter.hasNext(); i++) {
            TransportConnection c = (TransportConnection) iter.next();
            if (i % 2 == 0) {
                amountToMove = Math.min(amountToMove, c.amount);
            }
        }
        return amountToMove;
    }

    private double countUnitProfitForRelocationPath(List<TransportConnection> tmpList) {
        double unitProfit = 0;
        Iterator iter = tmpList.iterator();
        for (int i = 1; iter.hasNext(); i++) {
            TransportConnection c = (TransportConnection) iter.next();
            if (i % 2 == 1) {
                unitProfit += matrixD[c.x][c.y];
            }
        }
        return unitProfit;
    }

    private void relocateSuppliesByRelocationPath(List<TransportConnection> relocatePath) {
        double amountToMove = countAmountToMoveForRelocationPath(relocatePath);
        Iterator iter = relocatePath.iterator();
        for (int i = 0; iter.hasNext(); i++) {
            TransportConnection c = (TransportConnection) iter.next();
            c.amount += Math.pow(-1, i) * amountToMove;
        }
    }

    private boolean comparePaths(LinkedList<TransportConnection> list1, LinkedList<TransportConnection> list2) {
        int n1 =0, n2 =0;
        for(TransportConnection tc : list1) 
            if(tc.amount > 0) n1++;
        for(TransportConnection tc : list2) 
            if(tc.amount > 0) n2++;
        
        double xs1 = 1.0*n1/list1.size();
        double xs2 = 1.0*n2/list2.size();
        if(xs1 > xs2)
            return true;
        else if(xs1 == xs2) { 
            if(countTotalProfitForRelocationPath(list1) > countTotalProfitForRelocationPath(list2))
                return true;
        }
        return false;
    }

    public void setLogger(SupplyChainsLogger logger) {
        this.logger = logger;
    }
    
    private void logInfo() {
        logInfo("");
    }
    
    private void logInfo(String s) {
        if(logger == null) return;
        logger.info(s);
    }
    
    private void logError(String s, Exception e) {
        if(logger == null) return;
        logger.error(s, e);
    }

    private void northWestSolution() {
        for (int i = 0; i < suppliersAmount; i++) {
            for (int j = 0; j < customersAmount; j++) {
                if(!connections[i][j].blocked) {
                    connections[i][j].amount = Math.max(0, 
                            Math.min(connections[i][j].supplier.supply - SupplyChainsUtils.getAmountOfRow(connections, i),
                                    connections[i][j].customer.demand - SupplyChainsUtils.getAmountOfColumn(connections, j)));
                }
            }
        }
    }
}
