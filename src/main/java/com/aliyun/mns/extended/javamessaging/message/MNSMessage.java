//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging.message;

import com.aliyun.mns.extended.javamessaging.acknowledge.Acknowledger;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotWriteableException;

public abstract class MNSMessage implements Message {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final String BYTE_MESSAGE_TYPE = "byte";
    public static final String OBJECT_MESSAGE_TYPE = "object";
    public static final String TEXT_MESSAGE_TYPE = "text";
    public static final String JMS_MNS_MESSAGE_TYPE = "JMS_MNSMessageType";
    private final Map<String, MNSMessage.JMSMessagePropertyValue> properties;
    private boolean writePermissionsForProperties;
    private boolean writePermissionsForBody;
    private String jmsMessageID;
    private Acknowledger acknowledger;
    private String queueURL;
    private String receiptHandle;

    MNSMessage() {
        this.properties = new HashMap();
        this.writePermissionsForBody = true;
        this.writePermissionsForProperties = true;
    }

    MNSMessage(Acknowledger acknowledger, Map<String, MNSMessage.JMSMessagePropertyValue> properties, String queueURL, String receiptHandle) {
        this.acknowledger = acknowledger;
        this.properties = properties;
        this.queueURL = queueURL;
        this.receiptHandle = receiptHandle;
    }

    public String getQueueURL() {
        return this.queueURL;
    }

    public String getReceiptHandle() {
        return this.receiptHandle;
    }

    public String getJMSMessageID() throws JMSException {
        return this.jmsMessageID;
    }

    public void setJMSMessageID(String id) throws JMSException {
        this.jmsMessageID = id;
    }

    public long getJMSTimestamp() throws JMSException {
        return 0L;
    }

    public void setJMSTimestamp(long timestamp) throws JMSException {
    }

    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        return null;
    }

    public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException {
    }

    public void setJMSCorrelationID(String correlationID) throws JMSException {
    }

    public String getJMSCorrelationID() throws JMSException {
        return null;
    }

    public Destination getJMSReplyTo() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void setJMSReplyTo(Destination replyTo) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public Destination getJMSDestination() throws JMSException {
        return null;
    }

    public void setJMSDestination(Destination destination) throws JMSException {
    }

    public int getJMSDeliveryMode() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public boolean getJMSRedelivered() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void setJMSRedelivered(boolean redelivered) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public String getJMSType() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void setJMSType(String type) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public long getJMSExpiration() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void setJMSExpiration(long expiration) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public int getJMSPriority() throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void setJMSPriority(int priority) throws JMSException {
        throw new JMSException("Unsupported Method");
    }

    public void clearProperties() throws JMSException {
        this.properties.clear();
        this.writePermissionsForProperties = true;
    }

    public boolean propertyExists(String name) throws JMSException {
        return this.properties.containsKey(name);
    }

    <T> T getPrimitiveProperty(String property, Class<T> type) throws JMSException {
        if (property == null) {
            throw new NullPointerException("Property name is null");
        } else {
            Object value = this.getObjectProperty(property);
            if (value == null) {
                return this.handleNullPropertyValue(property, type);
            } else {
                T convertedValue = MNSMessage.TypeConversionSupport.convert(value, type);
                if (convertedValue == null) {
                    throw new MessageFormatException("Property " + property + " was " + value.getClass().getName() + " and cannot be read as " + type.getName());
                } else {
                    return convertedValue;
                }
            }
        }
    }

    private <T> T handleNullPropertyValue(String name, Class<T> classType) {
        if (classType == String.class) {
            return null;
        } else if (classType == Boolean.class) {
            return (T) Boolean.FALSE;
        } else if (classType != Double.class && classType != Float.class) {
            throw new NumberFormatException("Value of property with name " + name + " is null.");
        } else {
            throw new NullPointerException("Value of property with name " + name + " is null.");
        }
    }

    public boolean getBooleanProperty(String name) throws JMSException {
        return (Boolean)this.getPrimitiveProperty(name, Boolean.class);
    }

    public byte getByteProperty(String name) throws JMSException {
        return (Byte)this.getPrimitiveProperty(name, Byte.class);
    }

    public short getShortProperty(String name) throws JMSException {
        return (Short)this.getPrimitiveProperty(name, Short.class);
    }

    public int getIntProperty(String name) throws JMSException {
        return (Integer)this.getPrimitiveProperty(name, Integer.class);
    }

    public long getLongProperty(String name) throws JMSException {
        return (Long)this.getPrimitiveProperty(name, Long.class);
    }

    public float getFloatProperty(String name) throws JMSException {
        return (Float)this.getPrimitiveProperty(name, Float.class);
    }

    public double getDoubleProperty(String name) throws JMSException {
        return (Double)this.getPrimitiveProperty(name, Double.class);
    }

    public String getStringProperty(String name) throws JMSException {
        return (String)this.getPrimitiveProperty(name, String.class);
    }

    public Object getObjectProperty(String name) throws JMSException {
        MNSMessage.JMSMessagePropertyValue propertyValue = this.getJMSMessagePropertyValue(name);
        return propertyValue != null ? propertyValue.getValue() : null;
    }

    public Enumeration<String> getPropertyNames() throws JMSException {
        return new MNSMessage.PropertyEnum(this.properties.keySet().iterator());
    }

    public MNSMessage.JMSMessagePropertyValue getJMSMessagePropertyValue(String name) {
        return (MNSMessage.JMSMessagePropertyValue)this.properties.get(name);
    }

    public void setBooleanProperty(String name, boolean value) throws JMSException {
        this.setObjectProperty(name, value);
    }

    public void setByteProperty(String name, byte value) throws JMSException {
        this.setObjectProperty(name, value);
    }

    public void setShortProperty(String name, short value) throws JMSException {
        this.setObjectProperty(name, value);
    }

    public void setIntProperty(String name, int value) throws JMSException {
        this.setObjectProperty(name, value);
    }

    public void setLongProperty(String name, long value) throws JMSException {
        this.setObjectProperty(name, value);
    }

    public void setFloatProperty(String name, float value) throws JMSException {
        this.setObjectProperty(name, value);
    }

    public void setDoubleProperty(String name, double value) throws JMSException {
        this.setObjectProperty(name, value);
    }

    public void setStringProperty(String name, String value) throws JMSException {
        this.setObjectProperty(name, value);
    }

    public void setObjectProperty(String name, Object value) throws JMSException {
        if (name != null && !name.isEmpty()) {
            if (value != null && !"".equals(value)) {
                if (!this.isValidPropertyValueType(value)) {
                    throw new MessageFormatException("Value of property with name " + name + " has incorrect type " + value.getClass().getName() + ".");
                } else {
                    this.checkPropertyWritePermissions();
                    this.properties.put(name, new MNSMessage.JMSMessagePropertyValue(value));
                }
            } else {
                throw new IllegalArgumentException("Property value can not be null or empty.");
            }
        } else {
            throw new IllegalArgumentException("Property name can not be null or empty.");
        }
    }

    public void acknowledge() throws JMSException {
        this.acknowledger.acknowledge(this);
    }

    public abstract void internalClearBody() throws JMSException;

    public void clearBody() throws JMSException {
        this.internalClearBody();
        this.writePermissionsForBody = true;
    }

    private boolean isValidPropertyValueType(Object value) {
        return value instanceof Boolean || value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double || value instanceof String;
    }

    protected void checkPropertyWritePermissions() throws JMSException {
        if (!this.writePermissionsForProperties) {
            throw new MessageNotWriteableException("Message properties are not writable");
        }
    }

    protected void checkBodyWritePermissions() throws JMSException {
        if (!this.writePermissionsForBody) {
            throw new MessageNotWriteableException("Message body is not writable");
        }
    }

    private static class PropertyEnum implements Enumeration<String> {
        private final Iterator<String> propertyItr;

        public PropertyEnum(Iterator<String> propertyItr) {
            this.propertyItr = propertyItr;
        }

        public boolean hasMoreElements() {
            return this.propertyItr.hasNext();
        }

        public String nextElement() {
            return (String)this.propertyItr.next();
        }
    }

    public static class TypeConversionSupport {
        private static final Map<MNSMessage.TypeConversionSupport.ConversionKey, MNSMessage.TypeConversionSupport.Converter> CONVERSION_MAP = new HashMap();

        public TypeConversionSupport() {
        }

        public static <T> T convert(Object value, Class<T> clazz) {
            assert value != null && clazz != null;

            if (value.getClass() == clazz) {
                return (T) value;
            } else {
                MNSMessage.TypeConversionSupport.Converter c = (MNSMessage.TypeConversionSupport.Converter)CONVERSION_MAP.get(new MNSMessage.TypeConversionSupport.ConversionKey(value.getClass(), clazz));
                return c == null ? null : (T) c.convert(value);
            }
        }

        static {
            MNSMessage.TypeConversionSupport.Converter toStringConverter = new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    return value.toString();
                }
            };
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Boolean.class, String.class), toStringConverter);
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Byte.class, String.class), toStringConverter);
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Short.class, String.class), toStringConverter);
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Integer.class, String.class), toStringConverter);
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Long.class, String.class), toStringConverter);
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Float.class, String.class), toStringConverter);
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Double.class, String.class), toStringConverter);
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(String.class, Boolean.class), new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    String stringValue = (String)value;
                    return !Boolean.valueOf(stringValue) && !"1".equals((String)value) ? Boolean.FALSE : Boolean.TRUE;
                }
            });
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(String.class, Byte.class), new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    return Byte.valueOf((String)value);
                }
            });
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(String.class, Short.class), new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    return Short.valueOf((String)value);
                }
            });
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(String.class, Integer.class), new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    return Integer.valueOf((String)value);
                }
            });
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(String.class, Long.class), new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    return Long.valueOf((String)value);
                }
            });
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(String.class, Float.class), new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    return Float.valueOf((String)value);
                }
            });
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(String.class, Double.class), new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    return Double.valueOf((String)value);
                }
            });
            MNSMessage.TypeConversionSupport.Converter longConverter = new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    return ((Number)value).longValue();
                }
            };
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Byte.class, Long.class), longConverter);
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Short.class, Long.class), longConverter);
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Integer.class, Long.class), longConverter);
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Date.class, Long.class), new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    return ((Date)value).getTime();
                }
            });
            MNSMessage.TypeConversionSupport.Converter intConverter = new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    return ((Number)value).intValue();
                }
            };
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Byte.class, Integer.class), intConverter);
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Short.class, Integer.class), intConverter);
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Byte.class, Short.class), new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    return ((Number)value).shortValue();
                }
            });
            CONVERSION_MAP.put(new MNSMessage.TypeConversionSupport.ConversionKey(Float.class, Double.class), new MNSMessage.TypeConversionSupport.Converter() {
                public Object convert(Object value) {
                    return ((Number)value).doubleValue();
                }
            });
        }

        interface Converter {
            Object convert(Object var1);
        }

        static class ConversionKey {
            final Class<?> from;
            final Class<?> to;

            public ConversionKey(Class<?> from, Class<?> to) {
                this.from = from;
                this.to = to;
            }

            public int hashCode() {
                int result = 31 * 1 + (this.from == null ? 0 : this.from.hashCode());
                result = 31 * result + (this.to == null ? 0 : this.to.hashCode());
                return result;
            }

            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                } else if (obj == null) {
                    return false;
                } else if (this.getClass() != obj.getClass()) {
                    return false;
                } else {
                    MNSMessage.TypeConversionSupport.ConversionKey other = (MNSMessage.TypeConversionSupport.ConversionKey)obj;
                    if (this.from == null) {
                        if (other.from != null) {
                            return false;
                        }
                    } else if (!this.from.equals(other.from)) {
                        return false;
                    }

                    if (this.to == null) {
                        if (other.to != null) {
                            return false;
                        }
                    } else if (!this.to.equals(other.to)) {
                        return false;
                    }

                    return true;
                }
            }
        }
    }

    public static class JMSMessagePropertyValue {
        private final Object value;
        private final String type;
        private final String stringMessageAttributeValue;

        public JMSMessagePropertyValue(String stringValue, String type) throws JMSException {
            this.type = type;
            this.value = getObjectValue(stringValue, type);
            this.stringMessageAttributeValue = stringValue;
        }

        public JMSMessagePropertyValue(Object value) throws JMSException {
            this.type = getType(value);
            this.value = value;
            if ("Number.Boolean".equals(this.type)) {
                if ((Boolean)value) {
                    this.stringMessageAttributeValue = "1";
                } else {
                    this.stringMessageAttributeValue = "0";
                }
            } else {
                this.stringMessageAttributeValue = value.toString();
            }

        }

        public JMSMessagePropertyValue(Object value, String type) throws JMSException {
            this.value = value;
            this.type = type;
            if ("Number.Boolean".equals(type)) {
                if ((Boolean)value) {
                    this.stringMessageAttributeValue = "1";
                } else {
                    this.stringMessageAttributeValue = "0";
                }
            } else {
                this.stringMessageAttributeValue = value.toString();
            }

        }

        private static String getType(Object value) throws JMSException {
            if (value instanceof String) {
                return "String";
            } else if (value instanceof Integer) {
                return "Number.int";
            } else if (value instanceof Long) {
                return "Number.long";
            } else if (value instanceof Boolean) {
                return "Number.Boolean";
            } else if (value instanceof Byte) {
                return "Number.byte";
            } else if (value instanceof Double) {
                return "Number.double";
            } else if (value instanceof Float) {
                return "Number.float";
            } else if (value instanceof Short) {
                return "Number.short";
            } else {
                throw new JMSException("Not a supported JMS property type");
            }
        }

        private static Object getObjectValue(String value, String type) throws JMSException {
            if ("String".equals(type)) {
                return value;
            } else if ("Number.int".equals(type)) {
                return Integer.valueOf(value);
            } else if ("Number.long".equals(type)) {
                return Long.valueOf(value);
            } else if ("Number.Boolean".equals(type)) {
                return "1".equals(value) ? Boolean.TRUE : Boolean.FALSE;
            } else if ("Number.byte".equals(type)) {
                return Byte.valueOf(value);
            } else if ("Number.double".equals(type)) {
                return Double.valueOf(value);
            } else if ("Number.float".equals(type)) {
                return Float.valueOf(value);
            } else if ("Number.short".equals(type)) {
                return Short.valueOf(value);
            } else {
                throw new JMSException(type + " is not a supported JMS property type");
            }
        }

        public String getType() {
            return this.type;
        }

        public Object getValue() {
            return this.value;
        }

        public String getStringMessageAttributeValue() {
            return this.stringMessageAttributeValue;
        }
    }
}
