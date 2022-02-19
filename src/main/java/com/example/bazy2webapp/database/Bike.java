package com.example.bazy2webapp.database;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class Bike {
    ObjectId _id;
    String brand;
    String model;
    int wheel_size;
    boolean taken;
    String img_url;
    String type;

    public Bike() {
    }

    public Bike(ObjectId _id, String brand, String model, int wheel_size, boolean taken, String img_url, String type) {
        this._id = _id;
        this.brand = brand;
        this.model = model;
        this.wheel_size = wheel_size;
        this.taken = taken;
        this.img_url = img_url;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Bike{" +
                "_id=" + _id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", wheel_size=" + wheel_size +
                ", taken=" + taken +
                ", img_url='" + img_url + '\'' +
                '}';
    }
}
