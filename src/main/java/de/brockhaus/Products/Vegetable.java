package de.brockhaus.Products;

import java.time.LocalDate;

public class Vegetable extends Product {

    private static final int MIN_QUALITY = 20;
    private static final int MAX_EXPIRY_DAYS = 7;
    private static final int DECREASE = 5;

    public Vegetable(String name, int quality, LocalDate expire, double basePrice, String category) {
        super(name, quality, expire, basePrice, category);
    }

    /**
     * Gemüse verliert täglich fünf Qualitätspunkte. Wenn es unter 20 Qualitätspunkte gefallen ist,
     * soll es aussortiert werden. Das Ablaufdatum liegt maximal eine Woche in der Zukunft.
     */
    public boolean updateQualityAndPrice(int day) {
        if (day > MAX_EXPIRY_DAYS) {
            return true;
        }
        return predictQualityAndPrice(day, MIN_QUALITY, DECREASE);
    }
}
