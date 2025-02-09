package com.gaiabit.gaiabit.Model;

public class Animal {

    String ShelterName,ShelterAddress,Image,userUID;

    public Animal() {
    }

    public Animal(String shelterName, String shelterAddress, String image, String userUID) {
        ShelterName = shelterName;
        ShelterAddress = shelterAddress;
        Image = image;
        this.userUID = userUID;
    }

    public String getShelterName() {
        return ShelterName;
    }

    public void setShelterName(String shelterName) {
        ShelterName = shelterName;
    }

    public String getShelterAddress() {
        return ShelterAddress;
    }

    public void setShelterAddress(String shelterAddress) {
        ShelterAddress = shelterAddress;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}

