package de.brockhaus;

import de.brockhaus.Products.*;
import org.junit.Test;


import java.time.LocalDate;

import static org.junit.Assert.*;


public class VegetableTest {

    @Test
    public void testVegetableNotExpiredBeforeOneWeek() {
        LocalDate expireDate = LocalDate.now().plusDays(7);
        Vegetable product = new Vegetable("Carrot", 25, expireDate, 0.85, "vegetable");
        product.updateQualityAndPrice(1);
        assertFalse(product.updateQualityAndPrice(2));
    }

    @Test
    public void testVegetableExpiredAfterOneWeek() {
        LocalDate expireDate = LocalDate.now().plusDays(7);
        Vegetable product = new Vegetable("Carrot", 25, expireDate, 0.85, "vegetable");
        assertTrue(product.updateQualityAndPrice(8));
    }

    @Test
    public void testVegetableExpiredStaysExpired() {
        LocalDate expireDate = LocalDate.now().minusDays(10);
        Vegetable product = new Vegetable("Carrot", 25, expireDate, 0.85, "vegetable");
        product.updateQualityAndPrice(1);
        assertTrue(product.updateQualityAndPrice(2));
    }

    @Test
    public void testVegetableQualityHighEnough() {
        LocalDate expireDate = LocalDate.now().plusDays(5);
        Vegetable product = new Vegetable("Carrot", 28, expireDate, 0.85, "vegetable");
        assertFalse(product.updateQualityAndPrice(1));
    }

    @Test
    public void testVegetableMinQualityReached() {
        LocalDate expireDate = LocalDate.now().plusDays(5);
        Vegetable product = new Vegetable("Carrot", 28, expireDate, 0.85, "vegetable");
        product.updateQualityAndPrice(1);
        product.updateQualityAndPrice(2);
        assertTrue(product.updateQualityAndPrice(3));
    }

    @Test
    public void testWineDecreasedPriceByQualityPerDay() {
        LocalDate expireDate = LocalDate.now().plusDays(6);
        Vegetable vegetable = new Vegetable("Vegetable", 45, expireDate, 1.99, "Vegetable");

        vegetable.updateQualityAndPrice(1); // Preisupdate für selling_price
        vegetable.updateQualityAndPrice(2); // -0,50 €
        vegetable.updateQualityAndPrice(3); // -0,50 €
        vegetable.updateQualityAndPrice(4); // -0,50 €
        vegetable.updateQualityAndPrice(5); // -0,50 €

        //ermittelter Preis für das Produkt am 5. Tag (4,49 € nach Vorgabe)
        assertEquals(4.49, vegetable.getSellingPrice(), 0.0001);
    }
}
