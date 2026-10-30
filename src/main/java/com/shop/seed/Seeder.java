package com.shop.seed;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Seeder {

    private record Category(String slug, String name) {}
    private record Product(String slug, String name, int price, String categorySlug,
                           boolean hasImage, boolean available, String description) {}
    private record Promo(String productSlug, String title, String text) {}

    private static final List<Category> CATEGORIES =
            List.of(
                    new Category("videokarty", "Видеокарты"),
                    new Category("processory", "Процессоры"),
                    new Category("materinskie-platy", "Материнские платы"));

    private static final List<Product> PRODUCTS =
            List.of(
                    new Product(
                            "nvidia-geforce-rtx-4070",
                            "NVIDIA GeForce RTX 4070",
                            62990,
                            "videokarty",
                            true,
                            true,
                            "Видеокарта 12 ГБ GDDR6X, 5888 CUDA-ядер. Тянет 1440p с трассировкой лучей."),
                    new Product(
                            "nvidia-geforce-rtx-4060-ti",
                            "NVIDIA GeForce RTX 4060 Ti",
                            41990,
                            "videokarty",
                            true,
                            true,
                            "Видеокарта 8 ГБ GDDR6, 4352 CUDA-ядра. Разумный минимум для 1440p."),
                    new Product(
                            "nvidia-geforce-rtx-4090",
                            "NVIDIA GeForce RTX 4090",
                            189990,
                            "videokarty",
                            true,
                            false,
                            "Видеокарта 24 ГБ GDDR6X, 16384 CUDA-ядра. Флагман поколения."),
                    new Product(
                            "amd-radeon-rx-7800-xt",
                            "AMD Radeon RX 7800 XT",
                            54990,
                            "videokarty",
                            true,
                            true,
                            "Видеокарта 16 ГБ GDDR6, 3840 потоковых процессоров."),
                    new Product(
                            "intel-arc-a770",
                            "Intel Arc A770",
                            27990,
                            "videokarty",
                            false,
                            true,
                            "Видеокарта 16 ГБ GDDR6, 512 исполнительных блоков."),
                    new Product(
                            "amd-ryzen-5-7600x",
                            "AMD Ryzen 5 7600X",
                            18990,
                            "processory",
                            true,
                            true,
                            "Процессор 6 ядер, 12 потоков, до 5.3 ГГц. Сокет AM5."),
                    new Product(
                            "amd-ryzen-7-7800x3d",
                            "AMD Ryzen 7 7800X3D",
                            34990,
                            "processory",
                            true,
                            true,
                            "Процессор 8 ядер, 16 потоков, 96 МБ кеша 3D V-Cache. Сокет AM5."),
                    new Product(
                            "amd-ryzen-9-7950x",
                            "AMD Ryzen 9 7950X",
                            52990,
                            "processory",
                            true,
                            true,
                            "Процессор 16 ядер, 32 потока, до 5.7 ГГц. Сокет AM5."),
                    new Product(
                            "intel-core-i7-14700k",
                            "Intel Core i7-14700K",
                            39990,
                            "processory",
                            true,
                            true,
                            "Процессор 20 ядер, 28 потоков, до 5.6 ГГц. Сокет LGA1700."),
                    new Product(
                            "intel-core-i5-14400f",
                            "Intel Core i5-14400F",
                            15990,
                            "processory",
                            false,
                            true,
                            "Процессор 10 ядер, 16 потоков, до 4.7 ГГц. Без встроенной графики."),
                    new Product(
                            "asus-prime-b650m-a",
                            "ASUS PRIME B650M-A",
                            9990,
                            "materinskie-platy",
                            true,
                            true,
                            "Материнская плата microATX, сокет AM5, четыре слота DDR5."),
                    new Product(
                            "gigabyte-x670-aorus-elite",
                            "Gigabyte X670 AORUS Elite",
                            24990,
                            "materinskie-platy",
                            true,
                            true,
                            "Материнская плата ATX, сокет AM5, четыре слота M.2."),
                    new Product(
                            "msi-mag-b760-tomahawk",
                            "MSI MAG B760 Tomahawk",
                            16990,
                            "materinskie-platy",
                            true,
                            true,
                            "Материнская плата ATX, сокет LGA1700, поддержка DDR5."),
                    new Product(
                            "asrock-b550m-pro4",
                            "ASRock B550M Pro4",
                            21990,
                            "materinskie-platy",
                            false,
                            false,
                            "Материнская плата microATX, сокет AM4, четыре слота DDR4."));

    private static final List<Promo> PROMOS =
            List.of(
                    new Promo(
                            "nvidia-geforce-rtx-4070",
                            "Собери 1440p без компромиссов",
                            "Трассировка лучей и DLSS 3 на разумной мощности."),
                    new Promo(
                            "amd-ryzen-7-7800x3d",
                            "Игровой процессор года",
                            "96 МБ кеша 3D V-Cache там, где важен каждый кадр."),
                    new Promo(
                            "asus-prime-b650m-a",
                            "Платформа AM5 без переплаты",
                            "DDR5 и PCIe 5.0 в компактном корпусе."));

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public Seeder(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    public int seed() {
        return transactionTemplate.execute(status -> {
            Map<String, Integer> categoryIds = seedCategories();
            seedProducts(categoryIds);
            seedPromos();
            return PRODUCTS.size();
        });
    }

    private Map<String, Integer> seedCategories() {
        var sql = """
            insert into categories (slug, name) values (?, ?)
            on conflict (slug) do update set name = excluded.name
            returning id
            """;
        var ids = new HashMap<String, Integer>();
        for (var category : CATEGORIES) {
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class,
                    category.slug(), category.name());
            ids.put(category.slug(), id);
        }
        return ids;
    }

    private void seedProducts(Map<String, Integer> categoryIds) {
        var sql = """
            insert into products
                (slug, name, description, price_amount, category_id, image_url, available)
            values (?, ?, ?, ?, ?, ?, ?)
            on conflict (slug) do update set
                name = excluded.name,
                description = excluded.description,
                price_amount = excluded.price_amount,
                category_id = excluded.category_id,
                image_url = excluded.image_url,
                available = excluded.available
            """;
        for (var product : PRODUCTS) {
            var imageUrl = product.hasImage() ? "/images/" + product.slug() + ".svg" : null;
            jdbcTemplate.update(sql,
                    product.slug(), product.name(), product.description(),
                    product.price(), categoryIds.get(product.categorySlug()),
                    imageUrl, product.available());
        }
    }

    private void seedPromos() {
        var sql = """
            insert into promos (title, text, product_id)
            select ?, ?, id from products where slug = ?
            on conflict (product_id) do update set
                title = excluded.title,
                text = excluded.text
            """;
        for (var promo : PROMOS) {
            jdbcTemplate.update(sql, promo.title(), promo.text(), promo.productSlug());
        }
    }
}
