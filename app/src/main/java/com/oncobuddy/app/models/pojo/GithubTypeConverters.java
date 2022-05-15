package com.oncobuddy.app.models.pojo;

import androidx.room.TypeConverter;

import com.oncobuddy.app.models.pojo.appointments.ParticipantDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class GithubTypeConverters {
    
    static Gson gson = new Gson();
    
    @TypeConverter
    public static List<ParticipantDetails> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<ParticipantDetails>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<ParticipantDetails> someObjects) {
        return gson.toJson(someObjects);
    }
}