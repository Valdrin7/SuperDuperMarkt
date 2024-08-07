package de.brockhaus.Products;

public class Wine extends Product {

    // Alle zehn Tage gewinnt der Wein +1 an Qualität
    private static final int increaseQualityFrequency = 10;
    private static final int INCREASE = 1;
    public static final int MAX_QUALITY = 50;
    public static final int MIN_QUALITY = 0;


    public Wine(String name, int quality, double basePrice, String category) {
        if (quality < 0) {
            throw new IllegalArgumentException("Wein braucht eine nicht negative Qualität");
        }
        super(name, Math.min(quality, 50), null, basePrice, category);
    }

    /**
     * Stellt sicher, dass Wein eine nicht negative Qualität aufweist und max. 50 Qualität erreicht
     */
    public boolean updateQualityAndPrice(int day) {
        boolean remove = false;
        if (day > 1) {
            if (wineHasValidQuality() && (day % increaseQualityFrequency == 0)) {
                qualityUpdate(-INCREASE, MIN_QUALITY);
            } else if (getQuality() < MIN_QUALITY) {
                remove = true;
            }
        }
        return remove;
    }

    private boolean wineHasValidQuality() {
        return getQuality() >= MIN_QUALITY && getQuality() < MAX_QUALITY;
    }
}
