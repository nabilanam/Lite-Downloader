package com.nabilanam.litedownloader.model;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PathConverter implements JsonDeserializer<Path>, JsonSerializer<Path> {

	@Override
	public Path deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		return Paths.get(arg0.getAsString());
	}

	@Override
	public JsonElement serialize(Path arg0, Type arg1, JsonSerializationContext arg2) {
		return new JsonPrimitive(arg0.toString());
	}
}
