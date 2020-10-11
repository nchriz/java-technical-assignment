package kata.supermarket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Basket {
    private final List<Item> items;

    public Basket() {
        this.items = new ArrayList<>();
    }

    public void add(final Item item) {
        this.items.add(item);
    }

    List<Item> items() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal total() {
        return new TotalCalculator().calculate();
    }

    private class TotalCalculator {
        private final List<Item> items;
        private final Map<Item, Integer> numberOfItems;

        TotalCalculator() {
            this.items = items();
            numberOfItems = new HashMap<>();
            itemInBasket();
        }

        /**
         * To process the basket faster for discount, group items that are the same together.
         */
        private void itemInBasket() {
            for (Item item : this.items) {
                numberOfItems.compute(item, (k,v) -> {
                    if (v == null)
                        return 1;
                    return v+1;
                });
            }
        }

        private BigDecimal subtotal() {
            return items.stream().map(Item::price)
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        /**
         * TODO: This could be a good place to apply the results of
         *  the discount calculations.
         *  It is not likely to be the best place to do those calculations.
         *  Think about how Basket could interact with something
         *  which provides that functionality.
         *
         *  
         */
        private BigDecimal discounts() {
            BigDecimal discount = BigDecimal.ZERO;
            for (Map.Entry<Item, Integer> entry : numberOfItems.entrySet()) {
                if (entry.getValue() % 3 == 0) {
                    discount = discount.add(buyItemByUnitForOneLess(entry.getKey(), entry.getValue(), 3));
                } else if (entry.getValue() % 2 == 0) {
                    discount = discount.add(buyItemByUnitForOneLess(entry.getKey(), entry.getValue(), 2));
                }
            }
            return discount;
        }

        private BigDecimal buyItemByUnitForOneLess(Item item, Integer numberOfItems, Integer itemValue) {
            BigDecimal currentDiscount = item.price();
            return currentDiscount.multiply(
                    new BigDecimal(numberOfItems / itemValue).setScale(0, RoundingMode.DOWN));
        }

        private BigDecimal calculate() {
            return subtotal().subtract(discounts());
        }
    }
}
