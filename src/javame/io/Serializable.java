/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javame.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Tug
 */
public interface Serializable {
    void serialize(DataOutput output) throws IOException;
    void deserialize(DataInput input) throws IOException;
}
