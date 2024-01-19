package coding101.tq.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Demonstration of a linked list.
 */
public class LinkedLists {

    /**
     * A linked list of arbitrary objects.
     *
     * @param <T> the element item type
     */
    public static class LinkedList<T> {

        /**
         * An element in a linked list.
         *
         * @param <T> the item type
         */
        public static class LinkedListElement<T> {

            private final T item;
            private LinkedListElement<T> next;

            /**
             * Constructor.
             *
             * @param item the item
             */
            public LinkedListElement(T item) {
                super();
                this.item = item;
            }

            /**
             * Get the item.
             *
             * @return
             */
            public T item() {
                return item;
            }

            /**
             * Get the next element in the list.
             *
             * @return the next element in the list
             */
            public LinkedListElement<T> next() {
                return next;
            }
        }

        // the head element, or start, of the list
        protected LinkedListElement<T> head;

        /**
         * Constructor.
         */
        public LinkedList() {
            super();
        }

        /**
         * Get the first item in the list.
         *
         * @return the first item, or {@code null} if the list is empty
         */
        public T firstItem() {
            return (head != null ? head.item : null);
        }

        /**
         * Remove all items from the list.
         */
        public void clear() {
            // simply set head to null to remove all items
            head = null;
        }

        /**
         * Add an item to the end of the list.
         *
         * @param item the item to add
         */
        public void add(T item) {
            var newElement = new LinkedListElement<T>(item);
            if (head != null) {
                for (var element = this.head; element != null; element = element.next) {
                    if (element.next == null) {
                        element.next = newElement;
                        return;
                    }
                }
            } else {
                head = newElement;
            }
        }

        /**
         * Iterate over all items in the list, invoking the {@code accept(item)}
         * method on the provided callback function for each item.
         *
         * @param fn the callback function to call for every item in the list
         */
        public void forEach(Consumer<T> fn) {
            for (var element = this.head; element != null; element = element.next) {
                fn.accept(element.item());
            }
        }

        /**
         * Get the number of elements in the list.
         *
         * @return the count of elements in the list
         */
        public int size() {
            int size = 0;

            for (var element = this.head; element != null; element = element.next) {
                size++;
            }
            return size;
        }

        /**
         * Insert an item into the list, after another item.
         *
         * @param item  the item to insert
         * @param after the item to insert after; if not found then the item will become
         *              the new end of the list
         */
        public void insertAfter(T item, T after) {
            // TODO insert item behind after, or the end of the list if not found
        }

        /**
         * Insert an item into the list, before another item.
         *
         * @param item   the item to insert
         * @param before the item to insert before; if not found then the item will
         *               become the new start of the list
         */
        public void insertBefore(T item, T before) {
            // TODO insert item in front of before, or the start of the list if not found
        }

        /**
         * Remove an item from the list.
         *
         * @param item the item to remove
         * @return {@code true} if the item was found in the list and removed,
         *         {@code false} otherwise
         */
        public boolean remove(T item) {
            boolean removed = false;

            // TODO: find and remove the given item from the list

            return removed;
        }

        /**
         * Reverse the list.
         */
        public void reverse() {
            // TODO: reverse the list
        }
    }

    /**
     * Interactive linked list demonstration.
     *
     * @param args the command line arguments (none supported)
     * @throws IOException if any IO error occurs
     */
    public static void main(String[] args) throws IOException {
        LinkedList<String> list = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            AtomicInteger count = new AtomicInteger();
            while (true) {
                count.set(0);
                int size = list.size();
                System.out.print("Word list has %d items: [".formatted(size));
                list.forEach(item -> System.out.print("\n  % 3d. %s".formatted(count.incrementAndGet(), item)));
                if (size > 0) {
                    System.out.println("");
                }
                System.out.println("]\n\nType 'a <word>' or 'r <word>' to add/remove words.");
                System.out.println("Type 'I <word1> <word2>' to insert <word1> before <word2>.");
                System.out.println("Type 'i <word1> <word2>' to insert <word1> after <word2>.");
                System.out.println("Type 'b' or 'c' to reverse/clear the list.");
                System.out.print("Command: ");
                String[] input = reader.readLine().split("\\s+");
                if (input.length < 1) {
                    continue;
                }
                if ("b".equalsIgnoreCase(input[0])) {
                    list.reverse();
                    System.out.println("The list is reversed.\n");
                    continue;
                } else if ("c".equalsIgnoreCase(input[0])) {
                    list.clear();
                    System.out.println("The list is cleared.\n");
                    continue;
                }
                if (input.length < 2) {
                    System.out.println("");
                    continue;
                }
                if ("a".equalsIgnoreCase(input[0])) {
                    list.add(input[1]);
                    System.out.println("Item [%s] added.".formatted(input[1]));
                } else if ("r".equalsIgnoreCase(input[0])) {
                    boolean removed = list.remove(input[1]);
                    if (removed) {
                        System.out.println("Item [%s] removed.".formatted(input[1]));
                    } else {
                        System.out.println("Item [%s] not found!".formatted(input[1]));
                    }
                }
                if (input.length < 3) {
                    System.out.println("");
                    continue;
                }
                if ("I".equals(input[0])) {
                    list.insertBefore(input[1], input[2]);
                    System.out.println("Item [%s] inserted before [%s].".formatted(input[1], input[2]));
                } else if ("i".equals(input[0])) {
                    list.insertAfter(input[1], input[2]);
                    System.out.println("Item [%s] inserted after [%s].".formatted(input[1], input[2]));
                }
                System.out.println("");
            }
        }
    }
}
