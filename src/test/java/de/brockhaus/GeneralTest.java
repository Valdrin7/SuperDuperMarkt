package de.brockhaus;

import de.brockhaus.Products.General;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Random;

import static org.junit.Assert.*;

public class GeneralTest {

    @Test
    public void testGeneralProductCreationAndGetCategory() {
        General product = new General("Test Product", 10, LocalDate.of(2024, 10, 12), 2.99, "General");
        assertEquals("General", product.getCategory());
    }

    @Test
    public void testGeneralSellingPriceIsNeverChanged() {
        General general = new General("General", 41, LocalDate.of(2025, 10, 12), 1.99, "");

        Random r = new Random();
        int i = 1;
        int iterations = r.nextInt(1, 100);
        while (i < iterations) {
            general.updateQualityAndPrice(i++); // kein Preisupdate
        }

        assertEquals(6.09, general.getSellingPrice(), 0.0001);
    }

    @Test
    public void testGeneralNotExpiredBeforeExpiryDate() {
        LocalDate expireDate = LocalDate.now().plusDays(95);
        General generalProduct = new General("Sample Product", 80, expireDate, 0.85, "general");
        int i = 0;
        while (i < 96) {
            generalProduct.updateQualityAndPrice(i++);
        }
        assertFalse(generalProduct.updateQualityAndPrice(i));
    }

    @Test
    public void testGeneralExpiredAfterExpiryDate() {
        LocalDate expireDate = LocalDate.now().plusDays(70);
        General generalProduct = new General("Sample Product", 80, expireDate, 0.85, "general");
        int i = 0;
        while (i < 71) {
            generalProduct.updateQualityAndPrice(i++);
        }
        assertTrue(generalProduct.updateQualityAndPrice(i + 1));
    }


}
