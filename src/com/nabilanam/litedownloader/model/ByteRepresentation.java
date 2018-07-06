package com.nabilanam.litedownloader.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author nabil
 */
public class ByteRepresentation
{
    private static final long KB = 1024;
    private static final long MB = 1024 * 1024;
    private static final long GB = 1024 * 1024 * 1024;

    public static String Represent(long length)
    {
        String result;
        if (length >= GB)
        {
            double val = (float) length / (1024 * 1024 * 1024);
            result = round(val) + " GB";
        }
        else if (length >= MB)
        {
            double val = (float) length / (1024 * 1024);
            result = round(val) + " MB";
        }
        else if (length >= KB)
        {
            double val = (double) length / (1024);
            result = round(val) + " KB";
        }
        else if (length == -1)
        {
            return "undefined";
        }
        else
        {
            result = length + " B";
        }
        return result;
    }

    public static double round(double value)
    {
        return new BigDecimal(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
