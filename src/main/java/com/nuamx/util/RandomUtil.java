package com.nuamx.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

public class RandomUtil {

    private static final int DEFAULT_RANDOM_LEN = 1000;

    public static String randomGenerator(int randomStrLen) {
        int startRange = 48;
        int endRange = 122;
        int strLen = randomStrLen != -1 ? randomStrLen : DEFAULT_RANDOM_LEN;
        Random randomObj = new Random();
        return randomObj.ints(startRange, endRange + 1)
                .filter(codepoint -> (codepoint <= 57 || codepoint >= 65) && (codepoint <= 90 || codepoint >= 97))
                .limit(strLen)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static BigDecimal randomBigDecimal(int numericPrecision, int numericScale) {
        long rangeNumeric = (long) Math.pow(10, (numericPrecision - numericScale));
        long rangeDecimal = (long) Math.pow(10, numericScale);
        BigDecimal numeric = new BigDecimal(BigInteger.valueOf(new Random().nextLong(rangeNumeric)));
        BigDecimal decimal = new BigDecimal(0);
        if (numericScale != 0) {
            decimal = new BigDecimal(BigInteger.valueOf(new Random().nextLong(rangeDecimal)), numericScale);
        }
        return numeric.add(decimal);
    }

    public static void main(String[] args) {
        System.out.println(RandomUtil.randomBigDecimal(18, 4));
        System.out.println(RandomUtil.randomBigDecimal(18, 4));
        System.out.println(RandomUtil.randomBigDecimal(18, 4));
    }

}
