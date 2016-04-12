package com.kosalgeek.android.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * (The MIT License)
 * Copyright (c) 2015 KosalGeek. (kosalgeek at gmail dot com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the 'Software'), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Modified by EmmanuelMess
 **/
public class JsonConverter<T> {

    /**
     * add this line below into dependecies
     * compile 'com.google.code.gson:gson:2.2.4'
     *
     *
     * Usage:
     * List<User> userList = new JsonConverter().toList(output, User.class);
     *
     * Model Declaration:
     * In User.java, use @SerializedName() if a field name differs from a JSON attribute.
     * Make all attributes public. No need to create setters/getters.
     * Bellow is a sample code:
     *
     * import com.google.gson.annotations.SerializedName; // include gson-2.2.4.jar in libs folder
     * public class User {
     *    @SerializedName("user_id")
     *    public int user_id;
     *    @SerializedName("username")
     *    public String username;
     *    @SerializedName("password")
     *    public String password;
     * }
     * End of Usage
     **/
    public ArrayList<T> toArrayList(String jsonString, Class<T> clazz){
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("dd/MM/yy HH:mm:ss");
        Gson gson = builder.create();
        Type type = new ListParameterizedType(clazz);

	    return gson.fromJson(jsonString, type);
    }

    public List<T> toList(String jsonString, Class<T> clazz) {

	    return toArrayList(jsonString, clazz);
    }

    private static class ListParameterizedType implements ParameterizedType {

        private Type type;

        private ListParameterizedType(Type type) {
            this.type = type;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] {type};
        }

        @Override
        public Type getRawType() {
            return ArrayList.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

    }
}
