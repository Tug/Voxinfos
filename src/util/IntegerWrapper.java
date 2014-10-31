/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javame.io.Serializable;

/**
 *
 * @author Tug
 */
public class IntegerWrapper implements Serializable {

    private int value;

    public IntegerWrapper(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void serialize(DataOutput output) throws IOException {
        output.writeInt(value);
    }

    public void deserialize(DataInput input) throws IOException {
        value = input.readInt();
    }
    
}
