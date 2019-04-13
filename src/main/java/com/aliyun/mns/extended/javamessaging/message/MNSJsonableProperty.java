//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aliyun.mns.extended.javamessaging.message;

public class MNSJsonableProperty {
    private String propertyName;
    private String propertyType;
    private String propertyValue;

    public MNSJsonableProperty() {
    }

    public MNSJsonableProperty(String propertyName, String propertyType, String propertyValue) {
        this.setPropertyName(propertyName);
        this.setPropertyType(propertyType);
        this.setPropertyValue(propertyValue);
    }

    public String getPropertyValue() {
        return this.propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyType() {
        return this.propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}
