package de.netzkronehd.hitboxutils.utils;

import com.google.gson.Gson;
import de.netzkronehd.hitboxutils.message.Messages;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class Utils {

    public static final Gson GSON = new Gson();
    public static final Gson GSON_PRETTY = new Gson().newBuilder().setPrettyPrinting().create();
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

    private static final char[] characters = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();

    public static String getRandomString(int length) {
        final StringBuilder sb = new StringBuilder();
        final Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters[random.nextInt(characters.length)]);
        }
        return sb.toString();
    }

    public static boolean isOver(Long end) {
        if (end == null) return true;
        return ((System.currentTimeMillis() < end ? 1 : 0) | (end == -1L ? 1 : 0)) == 0;
    }

    public static float getMoveSpeed(double speed, boolean isFlying) {
        return getRealMoveSpeed(getMoveSpeed((float) speed), isFlying);
    }

    private static float getMoveSpeed(float userSpeed) {
        if (userSpeed > 10.0F) {
            userSpeed = 10.0F;
        } else if (userSpeed < 1.0E-4F) {
            userSpeed = 1.0E-4F;
        }
        return userSpeed;
    }

    private static float getRealMoveSpeed(float userSpeed, boolean isFly) {
        float defaultSpeed = isFly ? 0.1F : 0.2F;
        float maxSpeed = 1.0F;
        if (userSpeed < 1.0F) {
            return defaultSpeed * userSpeed;
        }
        float ratio = (userSpeed - 1.0F) / 9.0F * (maxSpeed - defaultSpeed);
        return ratio + defaultSpeed;
    }

    public static BufferedImage loadImage(String url) throws IOException {
        final URLConnection connection = new URL(url).openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        return ImageIO.read(connection.getInputStream());
    }

    public static String formatIpAddress(String ip) {
        String formattedIp = ip;
        formattedIp = formattedIp.replace("/", "");
        if (formattedIp.contains(":")) {
            formattedIp = formattedIp.split(":")[0];
        }
        return formattedIp;
    }

    public static String[] readData(byte[] data) {
        final List<String> read = new ArrayList<>();
        final DataInputStream di = new DataInputStream(new ByteArrayInputStream(data));
        for (int i = 0; i < 255; i++) {
            try {
                final String dr = di.readUTF();
                read.add(dr);
            } catch (IOException e) {
                if (!read.isEmpty()) {
                    return read.toArray(new String[0]);
                }
                return new String[]{new String(data, StandardCharsets.UTF_8)};
            }
        }
        return read.toArray(new String[0]);
    }

    public static String getArgsAsText(String[] args, int from) {
        final StringBuilder sb = new StringBuilder();
        for (int i = from; i < args.length; i++) {
            if ((i + 1) == args.length) {
                sb.append(args[i]);
            } else sb.append(args[i]).append(" ");
        }
        return sb.toString();
    }

    public static String getRemainingTime(long end) {
        if (end == -1L) return "§c§lPERMANENT";

        final long c = System.currentTimeMillis();
        long diff = end - c;

        long sekunden = 0, minuten = 0;

        while (diff > 1000) {
            diff -= 1000;
            sekunden++;
        }
        while (sekunden >= 60) {
            sekunden -= 60;
            minuten++;
        }
        final StringBuilder sb = new StringBuilder();

        if (minuten > 0) sb.append((minuten == 1 ? Messages.ONE_MINUTE : minuten + " " + Messages.MINUTES + " "));
        sb.append((sekunden == 1 ? Messages.ONE_SECOND : sekunden + " " + Messages.SECONDS));
        return sb.toString();
    }

    public static String getRemainingTimeInMinutes(int seconds) {
        if (seconds <= 0) return seconds + " " + Messages.SECONDS;

        int calcSeconds = seconds;
        int calcMinutes = 0;

        while (calcSeconds >= 60) {
            calcSeconds -= 60;
            calcMinutes++;
        }

        final StringBuilder sb = new StringBuilder();
        if (calcMinutes > 0) sb.append((calcMinutes == 1 ? Messages.ONE_MINUTE : calcMinutes + " " + Messages.MINUTES));
        if (calcSeconds == 0 && calcMinutes > 0) return sb.toString();
        if (!sb.isEmpty()) sb.append(" ");

        sb.append((calcSeconds == 1 ? Messages.ONE_SECOND : calcSeconds + " " + Messages.SECONDS));
        return sb.toString();
    }

    public static String getRemainingTimeInHours(long millis, String hours, String minutes, String seconds) {

        int sekunden = 0;
        int minuten = 0;
        int stunden = 0;

        while (millis > 1000) {
            millis -= 1000;
            sekunden++;
        }
        while (sekunden > 60) {
            sekunden -= 60;
            minuten++;
        }

        while (minuten > 60) {
            minuten -= 60;
            stunden++;
        }


        final StringBuilder sb = new StringBuilder();
        if (stunden > 0) sb.append(stunden).append(hours);
        if (!sb.isEmpty()) sb.append(" ");
        if (minuten > 0) sb.append(minuten).append(minutes);
        if (!sb.isEmpty()) sb.append(" ");
        sb.append(sekunden).append(seconds);
        return sb.toString();
    }

    public static String getRemainingTimeInHours(long millis) {

        int sekunden = 0;
        int minuten = 0;
        int stunden = 0;

        while (millis > 1000) {
            millis -= 1000;
            sekunden++;
        }
        while (sekunden > 60) {
            sekunden -= 60;
            minuten++;
        }

        while (minuten > 60) {
            minuten -= 60;
            stunden++;
        }


        final StringBuilder sb = new StringBuilder();
        if (stunden > 0) sb.append((stunden == 1 ? Messages.ONE_HOUR : stunden + " " + Messages.HOURS));
        if (!sb.isEmpty()) sb.append(" ");
        if (minuten > 0) sb.append((minuten == 1 ? Messages.ONE_MINUTE : minuten + " " + Messages.MINUTES));
        if (!sb.isEmpty()) sb.append(" ");
        sb.append((sekunden == 1 ? Messages.ONE_SECOND : sekunden + " " + Messages.SECONDS));
        return sb.toString();
    }

    public static boolean hasText(String prefix) {
        if (prefix == null) return false;
        return !prefix.trim().isEmpty();
    }

    // Methode zur Interpolation zwischen zwei Farben
    private static String interpolateColors(String color1, String color2, double ratio) {
        final int r1 = Integer.parseInt(color1.substring(1, 3), 16);
        final int g1 = Integer.parseInt(color1.substring(3, 5), 16);
        final int b1 = Integer.parseInt(color1.substring(5, 7), 16);

        final int r2 = Integer.parseInt(color2.substring(1, 3), 16);
        final int g2 = Integer.parseInt(color2.substring(3, 5), 16);
        final int b2 = Integer.parseInt(color2.substring(5, 7), 16);

        final int r = (int) (r1 + (r2 - r1) * ratio);
        final int g = (int) (g1 + (g2 - g1) * ratio);
        final int b = (int) (b1 + (b2 - b1) * ratio);

        return String.format("#%02X%02X%02X", r, g, b);
    }

    // Methode zum Formatieren des Texts mit den angegebenen Farben und einem gleitenden Übergang
    public static String formatTextWithGradientColors(String text, char colorCode, String... colors) {

        if (colors.length < 2) return text; // Nicht genügend Farben für einen Übergang

        final StringBuilder formattedText = new StringBuilder();
        final double colorStep = (double) (colors.length - 1) / (text.length() - 1);

        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (c == ' ') {
                formattedText.append(c);
            } else {
                final int colorIndex1 = (int) (i * colorStep);
                final int colorIndex2 = Math.min(colorIndex1 + 1, colors.length - 1);
                final double ratio = (i * colorStep) - colorIndex1;
                final String interpolatedColor = interpolateColors(colors[colorIndex1], colors[colorIndex2], ratio);
                formattedText.append(colorCode).append(interpolatedColor).append(c);
            }
        }
        return formattedText.toString();
    }

    public static List<Color> getGradientColors(int length, String... colors) {
        if (colors.length < 2) return Collections.emptyList();

        final List<Color> gradientColors = new ArrayList<>();
        final double colorStep = (double) (colors.length - 1) / (length - 1);
        for (int i = 0; i < length; i++) {
            final int colorIndex1 = (int) (i * colorStep);
            final int colorIndex2 = Math.min(colorIndex1 + 1, colors.length - 1);
            final double ratio = (i * colorStep) - colorIndex1;
            gradientColors.add(Color.decode(interpolateColors(colors[colorIndex1], colors[colorIndex2], ratio)));
        }
        return gradientColors;
    }

    public static <T> T[] addToArray(T[] array, T toAdd) {
        final List<T> list = new ArrayList<>(Arrays.asList(array));
        list.add(toAdd);
        return list.toArray(array);
    }

    public static List<String> getMessageLines(String msg, int maxWordsPerLine) {
        final List<String> lines = new ArrayList<>();
        final String[] words = msg.split(" ");
        final StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.isEmpty()) {
                currentLine.append(word);
            } else if (currentLine.toString().split(" ").length < maxWordsPerLine) {
                currentLine.append(" ").append(word);
            } else {
                lines.add(currentLine.toString());
                currentLine.setLength(0);
                currentLine.append(word);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    public static void writeDataInFile(File file, String data, boolean append) throws IOException {
        final FileWriter fw = new FileWriter(file, append);
        final BufferedWriter bw = new BufferedWriter(fw);
        bw.write(data);
        bw.newLine();
        bw.close();
    }

    public static String toBase64(String message) {
        return Base64.getEncoder().encodeToString(message.getBytes());
    }

    public static String fromBase64(String base) {
        return new String(Base64.getDecoder().decode(base.getBytes()));
    }
}
