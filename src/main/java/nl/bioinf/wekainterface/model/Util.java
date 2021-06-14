package nl.bioinf.wekainterface.model;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

/**
 * @author Jelle 387615
 * Small utility class for processing numbers and dates.
 */
public class Util {
    /**
     * Given a value round the value to 'numDecimals' decimals
     * @param value double
     * @param numDecimals int number of decimals the value should be rounded to
     * @return double, rounded value
     */
    public static double roundTo(double value, int numDecimals){
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols();
        dfSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#." + "#".repeat(Math.max(0, numDecimals)), dfSymbols);
        String decimals = Double.toString(value).split("\\.")[1];

        if(numDecimals < decimals.length()){

            int lastDigit = Character.getNumericValue(decimals.toCharArray()[numDecimals]);
            if (lastDigit >= 5){
                df.setRoundingMode(RoundingMode.UP);
            }else {
                df.setRoundingMode(RoundingMode.DOWN);
            }
            return Double.parseDouble(df.format(value));
        }
        // If the value is already rounded to the right amount of decimals return the given value
        return value;
    }

    /**
     * given a value, determine the amount of decimals
     * @param d double
     * @return int, the amount of decimals
     */
    public static int numDecimals(double d){
        String text = Double.toString(Math.abs(d));
        int integerPlaces = text.indexOf('.');
        return text.length() - integerPlaces - 1;
    }

    /**
     * Given a double parses it as a date in the given dateformat
     * @param date double value representing the milliseconds from the starting count date
     * @param dateformat String representing the format the date should be parsed in
     * @return String date
     */
    public static String parseDate(double date, String dateformat){
        long lDate = (long) date;
        return new SimpleDateFormat(dateformat).format(lDate);
    }
}
