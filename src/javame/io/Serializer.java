/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javame.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Tug
 */
public class Serializer {
    
    public static byte[] serializeClass(Object input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        serializeClass(input, output);
        return buffer.toByteArray();
    }

    public static void serializeClass(Object input, DataOutput output) {
        try {
            output.writeUTF(input.getClass().getName());
            if(input instanceof Serializable) {
                ((Serializable)input).serialize(output);
            } else if(input instanceof String) {
                output.writeUTF((String) input);
            } else if(input instanceof Integer) {
                output.writeInt(((Integer) input).intValue());
            } else if(input instanceof Double) {
                output.writeDouble(((Double) input).doubleValue());
            } else if(input instanceof Float) {
                output.writeFloat(((Float) input).floatValue());
            } else if(input instanceof Long) {
                output.writeLong(((Long) input).longValue());
            } else if(input instanceof Character) {
                output.writeChar(((Character) input).charValue());
            } else if(input instanceof Byte) {
                output.writeByte(((Byte) input).byteValue());
            } else if(input instanceof Short) {
                output.writeShort(((Short) input).shortValue());
            } else if(input instanceof Boolean) {
                output.writeBoolean(((Boolean) input).booleanValue());
            } else if(input instanceof StringBuffer) {
                output.writeUTF(input.toString());
            }
        } catch (IOException ex) {
            // do nothing
        }
    }

    public static Object deserializeClass(byte[] data) {
        DataInput input = new DataInputStream(new ByteArrayInputStream(data));
        return deserializeClass(input);
    }

    public static Object deserializeClass(DataInput input) {
        Object deserializedObject;
        Object result = null;
        try {
            String classType = input.readUTF();
            deserializedObject = Class.forName(classType).newInstance();
            if (deserializedObject instanceof Serializable) {
                Serializable resultTemp = (Serializable) deserializedObject;
                resultTemp.deserialize(input);
                result = resultTemp;
            } else if(deserializedObject instanceof String) {
                result = input.readUTF();
            } else if(deserializedObject instanceof Integer) {
                result = new Integer(input.readInt());
            } else if(deserializedObject instanceof Double) {
                result = new Double(input.readDouble());
            } else if(deserializedObject instanceof Float) {
                result = new Float(input.readFloat());
            } else if(deserializedObject instanceof Long) {
                result = new Long(input.readLong());
            } else if(deserializedObject instanceof Character) {
                result = new Character(input.readChar());
            } else if(deserializedObject instanceof Byte) {
                result = new Byte(input.readByte());
            } else if(deserializedObject instanceof Short) {
                result = new Short(input.readShort());
            } else if(deserializedObject instanceof Boolean) {
                result = new Boolean(input.readBoolean());
            } else if(deserializedObject instanceof StringBuffer) {
                result = new StringBuffer(input.readUTF());
            }
        } catch (IOException ex) {
            result = null;
        } catch (ClassNotFoundException ex) {
            result = null;
        } catch (InstantiationException ex) {
            result = null;
        } catch (IllegalAccessException ex) {
            result = null;
        }
        return result;
    }

}
