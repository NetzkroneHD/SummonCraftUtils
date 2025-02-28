package de.netzkronehd.hitboxutils.utils;

public class LevenshteinDistance {

    private static int unlimitedCompare(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }

        int n = left.length();
        int m = right.length();

        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }

        if (n > m) {
            final CharSequence tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.length();
        }

        final int[] p = new int[n + 1];

        int i;
        int j;
        int upperLeft;
        int upper;

        char rightJ;
        int cost;

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            upperLeft = p[0];
            rightJ = right.charAt(j - 1);
            p[0] = j;

            for (i = 1; i <= n; i++) {
                upper = p[i];
                cost = left.charAt(i - 1) == rightJ ? 0 : 1;
                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
                upperLeft = upper;
            }
        }

        return p[n];
    }

    public static Integer compare(final CharSequence left, final CharSequence right) {
        return unlimitedCompare(left, right);
    }

    public static double getSimilarity(final String msg1, final String msg2) {
        if (msg1 == null) return 0;
        if (msg2 == null) return 0;
        if (msg1.equalsIgnoreCase(msg2)) return 1.0;

        final double maxLength = Integer.max(msg1.length(), msg2.length());

        if (maxLength > 0) {
            return (maxLength - LevenshteinDistance.compare(msg1.toLowerCase(), msg2.toLowerCase())) / maxLength;
        }
        return 1.0;
    }

}
