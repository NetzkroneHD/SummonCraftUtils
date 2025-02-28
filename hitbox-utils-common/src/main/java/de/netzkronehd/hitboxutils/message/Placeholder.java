package de.netzkronehd.hitboxutils.message;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Placeholder {

    public static Placeholder placeholder(String text) {
        return new Placeholder(text);
    }

    public static Placeholder placeholder(String text, String placeholder, String replaceWith) {
        return new Placeholder(text).replace(placeholder, replaceWith);
    }

    @Getter
    private final String originalText;
    private final Map<String, String> toReplace;

    public Placeholder(String originalText) {
        this.originalText = (originalText == null) ? "" : originalText;
        this.toReplace = new HashMap<>();
    }

    public Placeholder replace(String placeholder, String replaceWith) {
        if(this.originalText.equalsIgnoreCase("")) return this;
        toReplace.put("%"+placeholder.toUpperCase()+"%", replaceWith);
        return this;
    }

    public String build() {
        if(this.originalText.equalsIgnoreCase("")) return "";
        StringBuilder replacedText = new StringBuilder(originalText);
        for (Map.Entry<String, String> entry : toReplace.entrySet()) {
            final String placeholder = Pattern.quote(entry.getKey());
            replacedText = new StringBuilder(replacedText.toString().replaceAll(placeholder, entry.getValue()));
        }
        return replacedText.toString();
    }

}
