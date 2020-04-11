package org.telegram.bot.beldtp.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GenerateFileNameUtil {

    @Value("${beldtp.filename.alphabet}")
    private String alphabet;

    @Value("${beldtp.filename.length}")
    private static int length = 100;

    public String generate(String originalFileName) {

        if (length > 255 || length > alphabet.length()) {
            length = alphabet.length();
        }

        String contentType = getFromFileNameContentType(originalFileName);
        return generateName() + "." + contentType;
    }

    public String getFromFileNameContentType(String originalFileName) {
        String[] s = originalFileName.split("\\.");
        return s[1];
    }

    public String generateName() {
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
