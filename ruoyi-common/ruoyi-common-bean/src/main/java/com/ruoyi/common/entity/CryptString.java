package com.ruoyi.common.entity;

import java.io.Serializable;
import java.util.Arrays;

public final class CryptString implements Serializable {

    /** The value is used for character storage. */
    private final char value[];

    /** Cache the hash code for the string */
    private int hash; // Default to 0

    public CryptString() {
        this.value = "".toCharArray();
    }
    public CryptString(String original) {
        this.value = original.toCharArray();
        this.hash = original.hashCode();
    }

    /**
     * Allocates a new {@code String} so that it represents the sequence of
     * characters currently contained in the character array argument. The
     * contents of the character array are copied; subsequent modification of
     * the character array does not affect the newly created string.
     *
     * @param  value
     *         The initial value of the string
     */
    public CryptString(char value[]) {
        this.value = Arrays.copyOf(value, value.length);
    }

    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof String) {
            String anotherString = (String)anObject;
            int n = value.length;
            if (n == anotherString.toCharArray().length) {
                char v1[] = value;
                char v2[] = anotherString.toCharArray();
                int i = 0;
                while (n-- != 0) {
                    if (v1[i] != v2[i])
                        return false;
                    i++;
                }
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int h = hash;
        if (h == 0 && value.length > 0) {
            char val[] = value;

            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
            hash = h;
        }
        return h;
    }

    public String toString() {
        return new String(value);
    }
}
