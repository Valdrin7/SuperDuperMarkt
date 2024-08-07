package de.brockhaus.Products;

import de.brockhaus.SuperDuperMarkt;

import java.time.LocalDate;


public abstract class Product {
    private String name;
    private int quality;
    private final LocalDate expire;
    private double sellingPrice;
    private final double basePrice;
    private String category;

//    static final LocalDate today = LocalDate.now();

    /**
     * Konstruktor
     */
    public Product(String name, int quality, LocalDate expire, double basePrice, String category) {
        this.name = name;
        this.quality = quality;
        this.expire = expire;
        this.basePrice = this.sellingPrice = basePrice;
        this.category = category;
    }

    public Product(String[] array) {
        this(array[0], Integer.parseInt(array[1]), (array[2].equals("null") || array[2].isEmpty()) ? LocalDate.MAX : LocalDate.parse(array[2]), Double.parseDouble(array[3]), (array.length > 4) ? array[4] : "");
    }

    /**
     * Diese Methode wird überschrieben für jeden Produkttypen
     *
     * @param day    aktueller Tag der aktuellen Iteration
     * @return boolean, der bestimmt, ob die aktuelle Schleife unterbrochen wird
     */
    public abstract boolean updateQualityAndPrice(int day);


    /**
     * Getter
     */

    public String getName() {
        return name;
    }

    public int getQuality() {
        return quality;
    }

    public LocalDate getExpire() {
        return expire;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public String getCategory() {
        return category;
    }

    public double getBasePrice() {
        return basePrice;
    }


    /**
     * Setter
     */

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSellingPrice(double price) {
        this.sellingPrice = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    /**
     * Public Methoden mit Logik
     */

    public void showProduct() {
        System.out.printf("%-16.15s", getName());
        System.out.printf("%8.3s", getQuality());
        if (getExpire() == null) {
            System.out.printf("%15.10s", "---");
        } else {
            System.out.printf("%15.10s", getExpire());
        }
        System.out.printf("%15.2f", getSellingPrice());
    }

    public double calculatePrice(double price, int quality) {
        price = price + 0.10 * quality;
        return price;
    }

    /**
     * Der Preis ist heiß! Aber nur wenn > 0, weil gratis bleibt gratis
     */
    public void sellingPriceUpdate() {
        if (getBasePrice() > 0) {
            setSellingPrice(calculatePrice(getBasePrice(), getQuality()));
        }
    }

    /**
     * @param decrease Qualitätsverlust
     * @param min_quality Mindestqualität (variiert je Produktkategorie)
     */
    public void qualityUpdate(int decrease, int min_quality) {
        if (getCategory().equalsIgnoreCase("Vegetable")) {
            if (getQuality() - decrease < min_quality) {
                setQuality(min_quality - 1);
                return;
            }
        }
        setQuality(getQuality() - decrease);
    }

    /**
     * @param day Tag der Iteration
     * @param minQuality Mindestqualität (variiert je Produktkategorie)
     * @param decrease Qualitätsverlust
     * @return remove flag
     */
    public boolean predictQualityAndPrice(int day, int minQuality, int decrease) {
        boolean remove = false;
        if (day > 1) {
            if (getQuality() < minQuality || checkIsExpired(SuperDuperMarkt.getFutureDay(day))) {
                remove = true;
            } else {
                qualityUpdate(decrease, minQuality);
                sellingPriceUpdate();
                if (getQuality() < minQuality) {
                    remove = true;
                }
            }
        } else if (day == 1) {
            if (getQuality() < minQuality) {
                remove = true;
            }
            sellingPriceUpdate();
        }
        return remove;
    }

    /**
     * Ist das Produkt abgelaufen
     * @param day zu überprüfender Tag
     * @return gibt Wert zurück, ob das Produkt abgelaufen ist oder nicht
     */
    public boolean checkIsExpired(LocalDate day) {
        return day.isAfter(getExpire());
    }
}

