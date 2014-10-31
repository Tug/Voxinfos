/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io;

import javame.util.Observable;

/**
 *
 * @author Tug
 */
public abstract class Receiver extends Observable implements Runnable {

    abstract public void end();
}
