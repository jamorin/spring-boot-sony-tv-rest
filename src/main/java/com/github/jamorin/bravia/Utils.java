package com.github.jamorin.bravia;

public class Utils {

    public static String slug(String command) {
        return command.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}
