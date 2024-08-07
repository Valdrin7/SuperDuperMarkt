package de.brockhaus;

import de.brockhaus.Products.Wine;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class WineTest {

    @Test
    public void testWineExpiryDateIsNeglected() {
        LocalDate expireDate = LocalDate.now().plusDays(100);
        Wine wine = (Wine) SuperDuperMarkt.getInstance().determineProductType("Merlot", 25, expireDate, 0.99, "Wine");
        assertNull(wine.getExpire());
    }

    @Test
    public void testWineQualityStaysOn50EvenIfHigher() {
        Wine wine = new Wine("Merlot", 54, 1.99, "Wine");
        assertEquals(50, wine.getQuality());
    }

    @Test
    public void testWineQualityIsNegativeNoObjectCreatedThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Wine("Merlot", -1, 1.99, "Wine"));
        assertEquals("Wein braucht eine nicht negative Qualität", exception.getMessage());
    }

    @Test
    public void testWineGainsQualityEvery10Days() {
        int startQuality = 25;
        Wine wine = new Wine("Merlot", startQuality, 1.99, "Wine");
        int iterations = 10;
        for (int i = 1; i <= iterations; i++) {
            wine.updateQualityAndPrice(10 * i);
            assertEquals(startQuality + i, wine.getQuality());
        }
    }

    @Test
    public void testWineGainsQualityUntil50ThenStays50() {
        int quality = 25;
        Wine wine = new Wine("Merlot", quality, 1.99, "Wine");
        while (quality <= Wine.MAX_QUALITY) {
            wine.updateQualityAndPrice(10);
            assertEquals(Math.min(++quality, Wine.MAX_QUALITY), wine.getQuality());
        }
    }

    @Test
    public void testWineHasNoPriceIncrease() {
        Wine wine = new Wine("Merlot", Wine.MAX_QUALITY, 1.99, "Wine");
        wine.updateQualityAndPrice(1);
        wine.updateQualityAndPrice(2);
        wine.updateQualityAndPrice(3);
        wine.updateQualityAndPrice(4);
        wine.updateQualityAndPrice(5);
        wine.updateQualityAndPrice(6);
        wine.updateQualityAndPrice(7);
        wine.updateQualityAndPrice(8);
        wine.updateQualityAndPrice(9);
        wine.updateQualityAndPrice(10);

        assertEquals(wine.getSellingPrice(), wine.getBasePrice(), 0.0001);
    }

}
