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
public interface SupplyChainsLogger {
    public void info(String s);
    public void error(String s, Exception e);
}
