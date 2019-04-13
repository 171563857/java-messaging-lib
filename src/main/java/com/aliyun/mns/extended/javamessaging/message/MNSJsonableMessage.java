//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MNSJsonableMessage implements Serializable {
    private static final long serialVersionUID = 5098986439571320233L;
    private List<MNSJsonableProperty> properties;
    private String messageBody;

    public MNSJsonableMessage() {
        this.properties = new ArrayList();
    }

    public MNSJsonableMessage(List<MNSJsonableProperty> properties, String messageBody) {
        this.properties = properties;
        this.messageBody = messageBody;
    }

    public void addProperty(String propertyName, String propertyType, String propertyValue) {
        MNSJsonableProperty property = new MNSJsonableProperty(propertyName, propertyType, propertyValue);
        this.properties.add(property);
    }

    public List<MNSJsonableProperty> getProperties() {
        return this.properties;
    }

    public void setProperties(List<MNSJsonableProperty> properties) {
        this.properties = properties;
    }

    public String getMessageBody() {
        return this.messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
