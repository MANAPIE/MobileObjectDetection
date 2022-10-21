package me.manapie.objectdetection;

public class ObjectItem {
    String objectClass;
    String objectPosition;

    public ObjectItem(String objectClass, String objectPosition) {
        this.objectClass = objectClass;
        this.objectPosition= objectPosition;
    }

    public String getObjectPosition() {
        return objectPosition;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public void setName(String value) {
        this.objectClass = value;
    }
}
