/*
SPDX-License-Identifier: Apache-2.0
*/
package basic.papernet.ledgerapi;

import com.google.gson.Gson;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * State class. States have a class, unique key, and a lifecycle current state
 * the current state is determined by the specific subclass
 */
public class State {

    protected String key;

    /**
     * @param {String|Object} class An identifiable class of the instance
     * @param {keyParts[]}    elements to pull together to make a key for the objects
     */
    public State() {

    }

    String getKey() {
        return this.key;
    }

    public String[] getSplitKey() {
        return State.splitKey(this.key);
    }

    /**
     * Convert object to buffer containing JSON data serialization Typically used
     * before putState()ledger API
     *
     * @param {Object} JSON object to serialize
     * @return {buffer} buffer with the data to store
     */
    public static byte[] serialize(Object object) {
        return new Gson().toJson(object).getBytes(UTF_8);
    }

    /**
     * Join the keyParts to make a unififed string
     *
     * @param keyParts
     */
    public static String makeKey(String[] keyParts) {
        return String.join(":", keyParts);
    }

    public static String[] splitKey(String key) {
        System.out.println("splitting key " + key + "   " + java.util.Arrays.asList(key.split(":")));
        return key.split(":");
    }

}
