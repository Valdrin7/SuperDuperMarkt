package de.brockhaus;

import de.brockhaus.Products.Cheese;
import org.junit.Test;

import java.util.Random;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class CheeseTest {

    @Test
    public void testCheeseProductExpiryDateOutOfRangeBetween50And100DaysInTheFuture() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Cheese("Cheddar", 38, LocalDate.now().plusDays(Cheese.MAX_RANGE_EXPIRY_DAYS + 1), 1.99, "cheese"));
        assertEquals("Das Ablaufdatum muss zwischen 50 und 100 Tagen in der Zukunft liegen.", exception.getMessage());

        IllegalArgumentException second_exception = assertThrows(IllegalArgumentException.class, () -> new Cheese("Cheddar", 40, LocalDate.now().plusDays(Cheese.MIN_RANGE_EXPIRY_DAYS - 1), 1.99, "cheese"));
        assertEquals("Das Ablaufdatum muss zwischen 50 und 100 Tagen in der Zukunft liegen.", second_exception.getMessage());
    }

    @Test
    public void testCheeseProductExpiryDateInRangeBetween50And100DaysInTheFuture() {
        Random r = new Random();

        Cheese cheese = new Cheese("Cheddar", 38, LocalDate.now().plusDays(r.nextInt(Cheese.MIN_RANGE_EXPIRY_DAYS, Cheese.MAX_RANGE_EXPIRY_DAYS + 1)), 1.99, "cheese");
        assertTrue(cheese.getExpire().isAfter(LocalDate.now().plusDays(Cheese.MIN_RANGE_EXPIRY_DAYS - 1))
                && cheese.getExpire().isBefore(LocalDate.now().plusDays(Cheese.MAX_RANGE_EXPIRY_DAYS + 1)));

        cheese = new Cheese("Cheddar", 38, LocalDate.now().plusDays(Cheese.MIN_RANGE_EXPIRY_DAYS), 1.99, "cheese");
        assertTrue(cheese.getExpire().isAfter(LocalDate.now().plusDays(Cheese.MIN_RANGE_EXPIRY_DAYS - 1))
                && cheese.getExpire().isBefore(LocalDate.now().plusDays(Cheese.MAX_RANGE_EXPIRY_DAYS + 1)));

        cheese = new Cheese("Cheddar", 38, LocalDate.now().plusDays(Cheese.MAX_RANGE_EXPIRY_DAYS), 1.99, "cheese");
        assertTrue(cheese.getExpire().isAfter(LocalDate.now().plusDays(Cheese.MIN_RANGE_EXPIRY_DAYS - 1))
                && cheese.getExpire().isBefore(LocalDate.now().plusDays(Cheese.MAX_RANGE_EXPIRY_DAYS + 1)));

    }

    @Test
    public void testCheeseIsNotExpiredAndWillExpire() {
        LocalDate expireDate = LocalDate.now().plusDays(Cheese.MIN_RANGE_EXPIRY_DAYS); // 50
        Cheese cheese = new Cheese("Käse", 81, expireDate, 1.80, "Cheese");
        int i = 1;
        while (i < 51) {
            cheese.updateQualityAndPrice(i);
            i++;
        }
        // Test nach 50 vollendeten Tagen
        assertTrue(cheese.checkIsExpired(SuperDuperMarkt.getFutureDay(i + 1)));
    }

    @Test
    public void testCheeseMinQualityReachedInTime() {
        LocalDate expireDate = LocalDate.now().plusDays(69);
        Cheese cheese = new Cheese("Kas", 32, expireDate, 1.87, "cheese");

        cheese.updateQualityAndPrice(1);
        cheese.updateQualityAndPrice(2);
        cheese.updateQualityAndPrice(3);

        assertTrue(cheese.getQuality() >= Cheese.MIN_QUALITY);

        cheese.updateQualityAndPrice(4);

        assertFalse(cheese.getQuality() >= Cheese.MIN_QUALITY);
    }

    @Test
    public void testCheeseHasInvalidQuality28() {
        assertThrows(IllegalArgumentException.class, () -> new Cheese("Käse", 28, LocalDate.of(2024, 10, 12), 1.99, "cheese"));
    }

    @Test
    public void testCheeseDecreasedPriceByQualityPerDay() {
        Cheese cheese = new Cheese("Käse", 41, LocalDate.of(2024, 10, 12), 1.99, "cheese");

        cheese.updateQualityAndPrice(1); // kein Preisupdate
        cheese.updateQualityAndPrice(2); // -0,10 €
        cheese.updateQualityAndPrice(3); // -0,10 €
        cheese.updateQualityAndPrice(4); // -0,10 €
        cheese.updateQualityAndPrice(5); // -0,10 €

        assertEquals(5.69, cheese.getSellingPrice(), 0.0001);
    }

}
