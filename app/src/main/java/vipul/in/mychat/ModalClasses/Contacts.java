package vipul.in.mychat.ModalClasses;

/**
 * Created by vipul on 9/1/18.
 */

public class Contacts {

    String phoneNum;
    String name;
    String device_token;
    String isOnline;

    public Contacts() {

        super();

    }

    public Contacts(String phoneNum, String name, String device_token, String isOnline) {
        this.phoneNum = phoneNum;
        this.name = name;
        this.device_token = device_token;
        this.isOnline = isOnline;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String isOnline() {
        return isOnline;
    }

    public void setOnline(String online) {
        isOnline = online;
    }
}
