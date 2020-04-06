package org.telegram.bot.beldtp.util;


public class GenerateFileNameUtil {

    private static String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static int length = 100;

    public static String generate(String originalFileName) {

        if (length > 255 || length > alphabet.length()) {
            length = alphabet.length();
        }

        String contentType = getFromFileNameContentType(originalFileName);
        return generateName() + "." + contentType;
    }

    public static String getFromFileNameContentType(String originalFileName) {
        String[] s = originalFileName.split("\\.");
        return s[1];
    }

    public static String generateName() {
        StringBuilder name = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * alphabet.length());
            name.append(
                    alphabet.charAt(index)
            );
        }
        return name.toString();
    }
}
