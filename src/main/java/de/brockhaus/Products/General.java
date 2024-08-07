package de.brockhaus.Products;

import de.brockhaus.SuperDuperMarkt;

import java.time.LocalDate;

public class General extends Product {

    public General(String name, int quality, LocalDate expire, double basePrice, String category) {
        super(name, quality, expire, basePrice, category);
    }

    /**
     * @param day aktueller Tag der aktuellen Iteration
     * @return boolean, der bestimmt, ob die aktuelle Schleife unterbrochen wird
     */
    public boolean updateQualityAndPrice(int day) {
        boolean remove = getExpire() != null && checkIsExpired(SuperDuperMarkt.getFutureDay(day));
        sellingPriceUpdate();
        return remove;
    }

}
