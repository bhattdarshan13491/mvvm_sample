package com.oncobuddy.app.models.pojo.records_list;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "person")
public class Person {
 
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "personId")
    private int id;
 
    @ColumnInfo(name = "personName")
    private String name;

    private int number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}