package de.brockhaus;

import de.brockhaus.Products.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class SuperDuperMarkt {

    private static SuperDuperMarkt superDuperMarkt;

    public static final int FILIAL_NUMMER = 1000;
    public static final String ORT = "Lünen";
    public static final String STEUERID = "DE12312313";

    private static final String RESET = "\033[0m";
    private static final String RED = "\033[31m";
    private static final String GREEN = "\033[32m";
    private static final String BLUE = "\033[34m";

    public static final LocalDate today = LocalDate.now();


    private SuperDuperMarkt() {
    }

    /**
     * @return Instanz von SuperDuperMarkt
     */
    public static SuperDuperMarkt getInstance() {
        if (superDuperMarkt == null) {
            superDuperMarkt = new SuperDuperMarkt();
        }
        return superDuperMarkt;
    }

    public void start() {
        List<Product> productList;
        productList = getData("sql", ""); // Einlesen der Produkte aus der Datenbank

        LocalDate latestDate = getLatestExpiryDate(productList);

        showInventory(productList);
        iterateDays(latestDate, productList); // Durchlaufen der Tage bis Haltbarkeit der Produkte erreicht sind
    }

    /**
     * Methode zum Einlesen einer Datei
     *
     * @param type sql, csv
     * @param path nur gebraucht bei csv --> das wäre der path: String path = "src/main/java/productList.csv";
     * @return returned eine Liste aller Produkte aus der Quelle
     */
    private List<Product> getData(String type, String path) {

        List<Product> productList = new ArrayList<>();
        String line = "";

        switch (type) {
            case "csv":
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(path));

                    while (!((line = reader.readLine()).isEmpty())) {
                        String[] data = line.split(";");
                        String name = data[0];
                        int quality = Integer.parseInt(data[1]);
                        LocalDate expiryDate = (data[2].equals("null") || data[2].isEmpty()) ? LocalDate.MAX : LocalDate.parse(data[2]);
                        double basePrice = Double.parseDouble(data[3]);
                        String category = (data.length < 5 || data[4] == null || data[4].isEmpty()) ? "General" : data[4];

                        addToProductList(productList, determineProductType(name, quality, expiryDate, basePrice, category));
                    }
                    System.out.println();
                    reader.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                break;

            case "sql":
                String url = "jdbc:sqlite:products.db";

                // SQL statements for creating table, trigger and select all
                String createTableSQL = "CREATE TABLE IF NOT EXISTS products ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT NOT NULL,"
                        + "quality INTEGER,"
                        + "expireDate TEXT,"
                        + "base_price REAL,"
                        + "category TEXT,"
                        + "updated_at TIMESTAMP DEFAULT (datetime('now', 'localtime')),"
                        + "created_at TIMESTAMP DEFAULT (datetime('now', 'localtime'))"
                        + ")";
                String createTrigger = "CREATE TRIGGER IF NOT EXISTS update_product AFTER UPDATE ON products " +
                        "FOR EACH ROW BEGIN UPDATE products SET updated_at = (datetime('now', 'localtime')) WHERE id=old.id; END";
                String selectAllSQL = "SELECT id, name, quality, expireDate, base_price, category, updated_at, created_at FROM products";

                try (Connection conn = DriverManager.getConnection(url);
                     Statement stmt = conn.createStatement()) {

                    stmt.execute(createTableSQL);
                    stmt.execute(createTrigger);

                    try (PreparedStatement pstmt = conn.prepareStatement(selectAllSQL);
                         ResultSet rs = pstmt.executeQuery()) {

                        while (rs.next()) {
                            int id = rs.getInt("id");
                            String name = rs.getString("name");
                            int quality = rs.getInt("quality");
                            String parseDate;
                            LocalDate expiryDate;
                            try {
                                parseDate = rs.getString("expireDate");
                                expiryDate = LocalDate.parse(parseDate);

                            } catch (DateTimeParseException | NullPointerException e) {
                                expiryDate = null;
                            }
                            double basePrice = rs.getDouble("base_price"); // 0 wenn null
                            String category = rs.getString("category");
                            String updatedAt = rs.getString("updated_at");
                            String createdAt = rs.getString("created_at");

                            if (category == null) {
                                category = "General";
                            }

                            addToProductList(productList, determineProductType(name, quality, expiryDate, basePrice, category));
                        }
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                break;
            default:
                System.out.println("Der Einlesetyp wird ignoriert. Der Typ muss aktuell csv oder sql sein");
                break;
        }
        return productList;
    }

    /**
     * Füge ein Produkt der Produktliste hinzu, sofern es nicht ungültig ist
     * @param productList
     * @param product
     */
    private void addToProductList(List<Product> productList, Product product) {
        if (product != null) {
            productList.add(product);
        }
    }

     /**
     * @param productList
     */
    private void showInventory(List<Product> productList) {
        //Formatierung und Ausgabe der Produkte + Info
        System.out.println("-------------------------------------------------------");
        System.out.printf("%s%-16s %-12s %-17s %-10s%s%n", BLUE, "Name", "Quality", "Expires", "Preis €", RESET);
        System.out.println("-------------------------------------------------------");
        for (Product product : productList) {
            if (product.getCategory() == null || product.getCategory().isEmpty()) {
                product.setCategory("General");
            }
            product.showProduct();
            System.out.println();
        }

        System.out.println();
        System.out.println();
    }

    /**
     * Ausgabe der Liste (täglich) entsprechend mit allen zugehörigen Informationen
     * @param latestDate, das am weitesten in der Zukunft liegende Ablaufdatum
     * @param products
     */
    private void iterateDays(LocalDate latestDate, List<Product> products) {
        int day = 1;

        LocalDate futureDay = today;

        while (futureDay.isBefore(latestDate.plusDays(2))) {

            System.out.printf("-----------------------------------| Tag %3d |-----------------------------------", day);
            System.out.println();
            System.out.printf("%s%-16s %-12s %-17s %-13s %-10s %-9s %s%n", BLUE, "Name", "Quality", "Expires", "Price €", "Keep", "Date", RESET);
            System.out.println("---------------------------------------------------------------------------------");

            for (Product product : products) {
                boolean remove = product.updateQualityAndPrice(day);
                product.showProduct();

                if (!remove) {
                    System.out.printf("%s %9.7s %s", GREEN, "⬤", RESET);
                } else {
                    System.out.printf("%s %9.7s %s", RED, "⬤", RESET);
                }
                System.out.printf("%15.10s%n", futureDay);
            }
            day++;
            futureDay = futureDay.plusDays(1);
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Ermitteln von Haltbarkeitsdatum (aktueller Produkte), das am weitesten in der Zukunft liegt!
     * Dadurch setzen wir eine Abbruchbedingung für die Iteration später!
     * @param productList
     * @return LocalDate
     */
    private LocalDate getLatestExpiryDate(List<Product> productList) {
        LocalDate latestDate = LocalDate.MIN;
        LocalDate expiryDate;
        for (Product product : productList) {
            expiryDate = product.getExpire();
            if (!(product.getExpire() == null) && expiryDate.isAfter(latestDate)) {
                latestDate = expiryDate;
            }
        }
        return latestDate;
    }

    /**
     * Ermitteln der Kategorie
     *
     * @param name
     * @param quality
     * @param expire
     * @param basePrice
     * @param category
     * @return neues Produkt, zur entsprechenden Kategorie hinzufügen
     */
    public Product determineProductType(String name, int quality, LocalDate expire, double basePrice, String category) {
        try {
            Product product;
            if (category.equalsIgnoreCase("General")) {
                product = new General(name, quality, expire, basePrice, category);
            } else if (category.equalsIgnoreCase("Cheese")) {
                product = new Cheese(name, quality, expire, basePrice, category);
            } else if (category.equalsIgnoreCase("Wine")) {
                product = new Wine(name, quality, basePrice, category);
            } else if (category.equalsIgnoreCase("Vegetable")) {
                product = new Vegetable(name, quality, expire, basePrice, category);
            } else {
                // Standardfall oder Fehlerbehandlung für unbekannte Kategorie
                product = new General(name, quality, expire, basePrice, category);
            }
            return product;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gibt das fortlaufende Datum in der Iteration aus, um die Haltbarkeit zu prüfen
     * @param daysToAdd Anzahl der zu addierenden Tage auf das Startdatum
     * @return LocalDate für den Haltbarkeitsvergleich
     */
    public static LocalDate getFutureDay (int daysToAdd) {
        return today.plusDays(daysToAdd - 1);
    }
}

