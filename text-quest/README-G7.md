# Goal 7: Shopping time

Goals [5](./README-G5.md) and [6](./README-G6.md) were pretty heavy on theory, light on gaming.
Never fear, for this goal we get to use some of that newly learned theory and apply it to our game,
specifically: shopping. 

You heard me right, it is time to shop! You deserve it: what with all your exploring and chest
opening, it is well past time to level up your wardrobe, adventurer!

## About inventory items

A major feature of the game is the ability for a player to acquire armor, weapons, potions, or other
sorts of things that enhance the player's abilities in some way. These items can be **purchased** by
the player using **coins** they have accumulated, such as by opening chests. Once purchased, some
items can be **equipped** such that the item's abilities enhance the player in some way. For example:

 * a **leather armor** item, when equipped, could increase a player's defence against enemy attacks
 * a **dagger weapon** item, when equipped, could increase the amount of damage a player could inflict when
    attacking an enemy
 * a **health potion** item, when equipped, could increase a player's health (notice here that _equip_ can
   also be thought of as _use_ because a potion might have only one use)

The game defines "items that can be added to a player's inventory" in the `InventoryItem` interface,
which has some basic properties like `name`, `type`, and `price`, like this:

```java
public interface InventoryItem {

    /**
     * Get the name of the item.
     *
     * @return the name of the item
     */
    String name();

    /**
     * Get the item type.
     *
     * @return the type of item
     */
    ItemType type();

    /**
     * Get the cost to purchase this item, in coins.
     *
     * @return the cost to purchase, in coins
     */
    int price();

    /**
     * Get the minimum experience points required for a player to use.
     *
     * @return the minimum experience points required
     */
    default int minimumXp() {
        return 0;
    }

    /**
     * Get a defensive score offset.
     *
     * @return a positive number for more defense, negative for less defense, or 0
     *         for no change
     */
    default int getDefenseOffset() {
        return 0;
    }

    /**
     * Get an offense score offset.
     *
     * @return a positive number for more offense, negative for less offense, or 0
     *         for no change
     */
    default int getOffenseOffset() {
        return 0;
    }
}
```

### Interfaces and default methods

Hold up, did you notice there are **implemented methods** like `minimumXp()` in that _interface_? In
the last challenge, we said that [an `interface` has _no_
implementation](./README-G6.md#about-interfaces). Originally in Java, this was true. Java 8
introduced **default** methods however, that changed the rules. So really interfaces in Java can
also include method implementation by adding the `default` modifier along with the implementation.
Classes that implement an interface do **not** have to define implementations for any `default`
methods, but they can **override** them if desired, to provide an **alternate** implementation.

For example, imagine a weapon item `SuperPowerfulWeapon` that should only be used if a player
has 1000 XP, and adds +10 attack to the player. We could override the `minimumXp()` method in
this class to return 1000 instead of the _default_ of 0:

```java
public class SuperPowerfulWeapon implements InventoryItem {

    @Override
    public int minimumXp() {
        return 1000;
    }

    @Overide
    public int getOffenseOffset() {
        return 10;
    }

}
```

> :point_up: Those `@Override` things are called **annotations** and are used to help convey
> additional information about the code they are attached to, or _annotate_. The `@Override`
> annotation signals that the method it annotates _must_ override an interface or parent class
> method. It does not affect how the code runs, but Java compilers can flag methods with this
> annotation with an error if it does not actually override a method. This can help over time
> as code evolves, to catch potential bugs when class hierarchies change.

## The `PlayerItems` class

A [`Player`](./src/main/java/coding101/tq/domain/Player.java) has a [`PlayerItems`](./src/main/java/coding101/tq/domain/PlayerItems.java)
property you can access with the `getItems()` method:

```java
public class Player {

    /**
     * Get the player items.
     *
     * @return the items
     */
    public PlayerItems getItems() {
        return items;
    }

}
```

The `PlayerItems` class represents the player's **inventory**: the collection of items the
player owns. It contains an `items` property that is a `List<InventoryItem>`, like this:

```java
/**
 * The player item list.
 */
public class PlayerItems {

    private List<InventoryItem> items = new ArrayList<>(5);

    /**
     * Get the collection of all inventory items.
     *
     * @return the items
     */
    public List<InventoryItem> getItems() {
        return items;
    }

}
```

Your first set of tasks involve completing several incomplete methods, known as **stubs**, that
support the various behaviors the game needs to perform, like adding items, equiping items, and so
on.

## Task 1: add/remove items from inventory

The `PlayerItems` class has two method stubs for adding and removing items from the player's inventory:

```java
public class PlayerItems {

    private List<InventoryItem> items = new ArrayList<>(5);

    /**
     * Add an item to the inventory.
     *
     * @param item the item to add
     */
    public void addItem(InventoryItem item) {
        // TODO: implement
    }

    /**
     * Remove an item from the inventory.
     *
     * @param item the item to remove
     */
    public void removeItem(InventoryItem item) {
        // TODO: implement
    }

}
```

Implement these two methods.

## Task 2: equip/stash items

The `addItem()` method you implemented adds an item to the player's inventory, but that item
is not _equipped_. It is like the inventory is a backpack, and when the player acquires an
item they **stash** the item in their backpack. When the player wants to actually _use_ the item,
they **equip** it by taking it out of their backpack and hold/wear/drink the item.

The player can always stash an equipped item back in the backpack, for example if they want
to equip a different item of the same type, for example switch from a dagger to a sword.

The `PlayerItem` class has two methods to handling equipping and stashing items:

```java
public class PlayerItems {

    /**
     * Equip or use an item.
     *
     * If the item can be equipped and the type is a singleton, then any currently
     * equipped item of the same type will be un-equipped first.
     *
     * @param item   the item to equip
     * @param player the player to eqip the item on
     */
    public void apply(InventoryItem item, Player player) {
        // TODO: equip or use the item
        // TODO: might have to un-equip an item of same type first
    }

    /**
     * Unequip an item, but keep it in the inventory.
     *
     * @param item   the item to unequip
     * @param player the player
     */
    public void stash(InventoryItem item, Player player) {
        // TODO: unequip the item
    }

}
```

The `apply(item, player)` method is called to **equip** an item. The `stash(item, player)` method
is called to **stash** an item. You must implement these two methods.

The `InventoryItem` interface includes these methods maintaining the "equipped" status of an item,
which means you must use these methods to modify the state of the `item` passed into the
`apply(item, player)` and `stash(item, player)` methods you implement:

```java
public interface InventoryItem {

    /**
     * Test if the item can be equipped (activated) on the player, such as a piece
     * of armor or a weapon.
     *
     * @return {@code true} if the item can be equipped
     */
    default boolean canEquip() {
        return false;
    }

    /**
     * Test if an item is equipped (activated) on the player.
     *
     * @return {@literal true} if the item is currently equipped
     */
    default boolean isEquipped() {
        return false;
    }

    /**
     * Equip or use this item on the player.
     *
     * @param player the player to apply the item to
     * @return {@code true} if the item was equipped or used; {@code false} if the
     *         item was already equipped or could not be equipped
     */
    boolean apply(Player player);

    /**
     * Unequip an item from a player, but keep it in their inventory.
     *
     * @param player the player to unequip the item from
     * @return {@code true} if the item was stashed; {@code false} if the item was
     *         already stashed or could not be stashed
     */
    boolean stash(Player player);

}
```

## Task 3: compute equipped defensive/offensive totals

During combat, the game needs to know how powerful the player's defencive and offensive (attack)
capabilities are. For example, imagine a player has the following items equipped:

| Item            | Defense | Offense |
|:----------------|--------:|:-------:|
| Strength potion |       0 |     +10 |
| Leather armor   |      +5 |       0 |
| Helmet          |      +1 |       0 |
| Sword           |       0 |      +5 |
|-----------------|---------|---------|
| **Total**       |      +6 |     +15 |



```java
public class PlayerItems {

    private List<InventoryItem> items = new ArrayList<>(5);

    /**
     * Get the total defensive offset of all equipped items.
     *
     * @return the defensive offset total
     */
    public int equippedDefensiveOffsetTotal() {
        // TODO: return the total defensive offset
        return 0;
    }

    /**
     * Get the total offensive offset of all equipped items.
     *
     * @return the offensive offset total
     */
    public int equippedOffensiveOffsetTotal() {
        // TODO: return the total offensive offset
        return 0;
    }

}
```
