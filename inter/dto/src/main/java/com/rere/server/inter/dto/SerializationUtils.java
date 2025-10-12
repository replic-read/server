package com.rere.server.inter.dto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

/**
 * Utility class for serializing/deserializing objects from/to base 64.
 */
public final class SerializationUtils {

    private SerializationUtils() {
    }

    /**
     * Serializes an object to base 64.
     *
     * @param serializable The object to serialie.
     * @return The base 64 string.
     */
    public static String toBase64(Serializable serializable) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(serializable);
            oos.close();
        } catch (IOException e) {
            return null;
        }

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * Deserializes an object from a base 64 string.
     *
     * @param serialized The serialized base 64 string.
     * @param <T>        The type to deserialize.
     * @return The deserialized object, or null if the object could not be deserialized.
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromBase64(String serialized) {
        byte[] data = Base64.getDecoder().decode(serialized);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

}
