package com.yichiuan.homecamera.data.remote.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Token {
    public abstract String token();

    public static TypeAdapter<Token> typeAdapter(Gson gson) {
        return new AutoValue_Token.GsonTypeAdapter(gson);
    }
}