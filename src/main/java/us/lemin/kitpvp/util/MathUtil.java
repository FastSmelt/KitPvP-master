package us.lemin.kitpvp.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {
    public static boolean isWithin(double i, double j, double range) {
        return Math.abs(i - j) <= range;
    }
}
