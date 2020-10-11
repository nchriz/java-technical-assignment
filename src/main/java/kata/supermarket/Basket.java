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
         */
        private BigDecimal discounts() {
            BigDecimal discount = new BigDecimal(0);
            for (Map.Entry<Item, Integer> entry : numberOfItems.entrySet()) {
                BigDecimal currentDiscount = BigDecimal.ZERO;
                if (entry.getValue() % 3 == 0) {
                    currentDiscount = entry.getKey().price();
                    currentDiscount = currentDiscount.multiply(
                            new BigDecimal(entry.getValue() / 3).setScale(0, RoundingMode.DOWN));
                }
                discount = discount.add(currentDiscount);
            }
            return discount;
        }

        private BigDecimal calculate() {
            return subtotal().subtract(discounts());
        }
    }
}
