//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging.message;

import com.aliyun.mns.extended.javamessaging.acknowledge.Acknowledger;
import com.aliyun.mns.extended.javamessaging.message.MNSMessage.JMSMessagePropertyValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MNSBytesMessage extends MNSMessage implements BytesMessage {
    private static final Log LOG = LogFactory.getLog(MNSBytesMessage.class);
    private byte[] bytes;
    private ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    public MNSBytesMessage() {
        this.dataOut = new DataOutputStream(this.bytesOut);
    }

    public MNSBytesMessage(String messageBody, Map<String, JMSMessagePropertyValue> properties, Acknowledger acknowledger, String queueURL, String receiptHandle) throws JMSException {
        super(acknowledger, properties, queueURL, receiptHandle);
        this.dataOut = new DataOutputStream(this.bytesOut);

        try {
            this.dataOut.write(Base64.decodeBase64(messageBody));
            this.reset();
        } catch (IOException var7) {
            LOG.error("IOException: Message cannot be written", var7);
            throw convertExceptionToJMSException(var7);
        } catch (Exception var8) {
            LOG.error("Unexpected exception: ", var8);
            throw convertExceptionToJMSException(var8);
        }
    }

    protected static JMSException convertExceptionToJMSException(Exception e) {
        JMSException ex = new JMSException(e.getMessage());
        ex.initCause(e);
        return ex;
    }

    void checkCanRead() throws JMSException {
        if (this.bytes == null) {
            throw new MessageNotReadableException("Message is not readable");
        }
    }

    void checkCanWrite() throws JMSException {
        if (this.dataOut == null) {
            throw new MessageNotWriteableException("Message is not writeable");
        }
    }

    public void internalClearBody() throws JMSException {
        this.bytes = null;
        this.dataIn = null;
        this.bytesOut = new ByteArrayOutputStream();
        this.dataOut = new DataOutputStream(this.bytesOut);
    }

    public long getBodyLength() throws JMSException {
        this.checkCanRead();
        return (long)this.bytes.length;
    }

    public boolean readBoolean() throws JMSException {
        this.checkCanRead();

        try {
            return this.dataIn.readBoolean();
        } catch (EOFException var2) {
            throw new MessageEOFException(var2.getMessage());
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public byte readByte() throws JMSException {
        this.checkCanRead();

        try {
            return this.dataIn.readByte();
        } catch (EOFException var2) {
            throw new MessageEOFException(var2.getMessage());
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public int readUnsignedByte() throws JMSException {
        this.checkCanRead();

        try {
            return this.dataIn.readUnsignedByte();
        } catch (EOFException var2) {
            throw new MessageEOFException(var2.getMessage());
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public short readShort() throws JMSException {
        this.checkCanRead();

        try {
            return this.dataIn.readShort();
        } catch (EOFException var2) {
            throw new MessageEOFException(var2.getMessage());
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public int readUnsignedShort() throws JMSException {
        this.checkCanRead();

        try {
            return this.dataIn.readUnsignedShort();
        } catch (EOFException var2) {
            throw new MessageEOFException(var2.getMessage());
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public char readChar() throws JMSException {
        this.checkCanRead();

        try {
            return this.dataIn.readChar();
        } catch (EOFException var2) {
            throw new MessageEOFException(var2.getMessage());
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public int readInt() throws JMSException {
        this.checkCanRead();

        try {
            return this.dataIn.readInt();
        } catch (EOFException var2) {
            throw new MessageEOFException(var2.getMessage());
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public long readLong() throws JMSException {
        this.checkCanRead();

        try {
            return this.dataIn.readLong();
        } catch (EOFException var2) {
            throw new MessageEOFException(var2.getMessage());
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public float readFloat() throws JMSException {
        this.checkCanRead();

        try {
            return this.dataIn.readFloat();
        } catch (EOFException var2) {
            throw new MessageEOFException(var2.getMessage());
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public double readDouble() throws JMSException {
        this.checkCanRead();

        try {
            return this.dataIn.readDouble();
        } catch (EOFException var2) {
            throw new MessageEOFException(var2.getMessage());
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public String readUTF() throws JMSException {
        this.checkCanRead();

        try {
            return this.dataIn.readUTF();
        } catch (EOFException var2) {
            throw new MessageEOFException(var2.getMessage());
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public int readBytes(byte[] value) throws JMSException {
        return this.readBytes(value, value.length);
    }

    public int readBytes(byte[] value, int length) throws JMSException {
        if (length < 0) {
            throw new IndexOutOfBoundsException("Length bytes to read can't be smaller than 0 but was " + length);
        } else {
            this.checkCanRead();

            try {
                int n;
                int count;
                for(n = 0; n < length; n += count) {
                    count = this.dataIn.read(value, n, length - n);
                    if (count < 0) {
                        break;
                    }
                }

                if (n == 0 && length > 0) {
                    n = -1;
                }

                return n;
            } catch (IOException var5) {
                throw convertExceptionToJMSException(var5);
            }
        }
    }

    public void writeBoolean(boolean value) throws JMSException {
        this.checkCanWrite();

        try {
            this.dataOut.writeBoolean(value);
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public void writeByte(byte value) throws JMSException {
        this.checkCanWrite();

        try {
            this.dataOut.writeByte(value);
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public void writeShort(short value) throws JMSException {
        this.checkCanWrite();

        try {
            this.dataOut.writeShort(value);
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public void writeChar(char value) throws JMSException {
        this.checkCanWrite();

        try {
            this.dataOut.writeChar(value);
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public void writeInt(int value) throws JMSException {
        this.checkCanWrite();

        try {
            this.dataOut.writeInt(value);
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public void writeLong(long value) throws JMSException {
        this.checkCanWrite();

        try {
            this.dataOut.writeLong(value);
        } catch (IOException var4) {
            throw convertExceptionToJMSException(var4);
        }
    }

    public void writeFloat(float value) throws JMSException {
        this.checkCanWrite();

        try {
            this.dataOut.writeFloat(value);
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public void writeDouble(double value) throws JMSException {
        this.checkCanWrite();

        try {
            this.dataOut.writeDouble(value);
        } catch (IOException var4) {
            throw convertExceptionToJMSException(var4);
        }
    }

    public void writeUTF(String value) throws JMSException {
        this.checkCanWrite();

        try {
            this.dataOut.writeUTF(value);
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public void writeBytes(byte[] value) throws JMSException {
        this.checkCanWrite();

        try {
            this.dataOut.write(value);
        } catch (IOException var3) {
            throw convertExceptionToJMSException(var3);
        }
    }

    public void writeBytes(byte[] value, int offset, int length) throws JMSException {
        this.checkCanWrite();

        try {
            this.dataOut.write(value, offset, length);
        } catch (IOException var5) {
            throw convertExceptionToJMSException(var5);
        }
    }

    public void writeObject(Object value) throws JMSException {
        if (value == null) {
            throw new NullPointerException("Cannot write null value of object");
        } else {
            if (value instanceof Boolean) {
                this.writeBoolean((Boolean)value);
            } else if (value instanceof Character) {
                this.writeChar((Character)value);
            } else if (value instanceof Byte) {
                this.writeByte((Byte)value);
            } else if (value instanceof Short) {
                this.writeShort((Short)value);
            } else if (value instanceof Integer) {
                this.writeInt((Integer)value);
            } else if (value instanceof Long) {
                this.writeLong((Long)value);
            } else if (value instanceof Float) {
                this.writeFloat((Float)value);
            } else if (value instanceof Double) {
                this.writeDouble((Double)value);
            } else if (value instanceof String) {
                this.writeUTF(value.toString());
            } else {
                if (!(value instanceof byte[])) {
                    throw new MessageFormatException("Cannot write non-primitive type: " + value.getClass());
                }

                this.writeBytes((byte[])((byte[])value));
            }

        }
    }

    public void reset() throws JMSException {
        if (this.dataOut != null) {
            this.bytes = this.bytesOut.toByteArray();
            this.dataOut = null;
            this.bytesOut = null;
        }

        this.dataIn = new DataInputStream(new ByteArrayInputStream(this.bytes));
    }

    public byte[] getBodyAsBytes() throws JMSException {
        return this.bytes != null ? Arrays.copyOf(this.bytes, this.bytes.length) : this.bytesOut.toByteArray();
    }
}
