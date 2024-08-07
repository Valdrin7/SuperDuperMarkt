package de.brockhaus.Products;

import de.brockhaus.SuperDuperMarkt;

import java.time.LocalDate;

public class Cheese extends Product {

    public static final int MIN_QUALITY = 30;
    public static final int MIN_RANGE_EXPIRY_DAYS = 50;
    public static final int MAX_RANGE_EXPIRY_DAYS = 100;
    private static final int DECREASE = 1;

    public Cheese(String name, int quality, LocalDate expire, double basePrice, String category) {
        boolean expiryTooSoon = expire.isBefore(SuperDuperMarkt.today.plusDays(MIN_RANGE_EXPIRY_DAYS));
        boolean expiryTooLate = expire.isAfter(SuperDuperMarkt.today.plusDays(MAX_RANGE_EXPIRY_DAYS));
        boolean hasEnoughQuality = quality >= MIN_QUALITY;

        if (expiryTooSoon || expiryTooLate) {
            throw new IllegalArgumentException("Das Ablaufdatum muss zwischen 50 und 100 Tagen in der Zukunft liegen.");
        }
        if (!hasEnoughQuality) {
            throw new IllegalArgumentException("Die Qualität von Käse muss mindestens 30 betragen.");
        }
        super(name, quality, expire, basePrice, category);
    }

    /**
     * Käse verliert täglich einen Qualitätspunkt. Wenn er unter 30 Qualitätspunkte gefallen ist, soll er aussortiert werden
     */
    public boolean updateQualityAndPrice(int day) {
        return predictQualityAndPrice(day, MIN_QUALITY, DECREASE);
    }
}

