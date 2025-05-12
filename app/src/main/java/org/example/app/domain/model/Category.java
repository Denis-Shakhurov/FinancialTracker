package org.example.app.domain.model;

/**
 * Перечисление, представляющее категории транзакций.
 * Каждая категория соответствует определенному типу расходов или доходов.
 */
public enum Category {
    /** Категория для продуктов питания. */
    PRODUCTS,
    /** Категория для расходов на жилье. */
    HOUSE,
    /** Категория для транспортных расходов. */
    TRANSPORT,
    /** Категория для расходов в супермаркетах. */
    SUPERMARKETS,
    /** Категория для доходов. */
    INCOME,
    /** Категория для прочих расходов. */
    OTHER_EXPENSES;

    /**
     * Выводит список всех категорий с их порядковыми номерами.
     */
    public static void printCategory() {
        Category[] categories = Category.values();

        for (Category category : categories) {
            System.out.println(category.ordinal() + ": " + category.name());
        }
    }

    /**
     * Возвращает категорию по её порядковому номеру.
     *
     * @param ordinal строковое представление порядкового номера категории
     * @return объект категории, соответствующий переданному порядковому номеру,
     * или OTHER_EXPENSES, если переданный номер равен null
     */
    public static Category getCategory(String ordinal) {
        if (ordinal != null) {
            int ordinalInt = Integer.parseInt(ordinal);
            return Category.values()[ordinalInt];
        } else {
            return OTHER_EXPENSES;
        }
    }
}
