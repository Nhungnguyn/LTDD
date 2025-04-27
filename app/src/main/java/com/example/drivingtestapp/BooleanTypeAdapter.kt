package com.example.drivingtestapp

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class BooleanTypeAdapter : TypeAdapter<Boolean>() {
    override fun write(out: JsonWriter, value: Boolean?) {
        out.value(value ?: false)
    }

    override fun read(reader: JsonReader): Boolean {
        return when (reader.peek()) {
            com.google.gson.stream.JsonToken.BOOLEAN -> reader.nextBoolean()
            com.google.gson.stream.JsonToken.STRING -> reader.nextString().toBooleanStrictOrNull() ?: false
            com.google.gson.stream.JsonToken.NUMBER -> reader.nextInt() != 0
            else -> {
                reader.skipValue()
                false
            }
        }
    }
}