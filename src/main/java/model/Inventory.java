package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private Map<String, Integer> items;
    private Map<String, java.awt.image.BufferedImage> icons;
    private Map<String, TrashType> itemTypes;
    private int maxSlots;

    public Inventory() {
        this(20);
    }

    public Inventory(int maxSlots) {
        this.items = new HashMap<>();
        this.icons = new HashMap<>();
        this.itemTypes = new HashMap<>();
        this.maxSlots = maxSlots;
    }

    public boolean addItem(String itemName) {
        return addItem(itemName, 1, null);
    }

    public boolean addItem(String itemName, int quantity) {
        return addItem(itemName, quantity, null);
    }

    public boolean addItem(String itemName, int quantity, java.awt.image.BufferedImage icon) {
        return addItem(itemName, quantity, icon, null);
    }

    public boolean addItem(String itemName, int quantity, java.awt.image.BufferedImage icon, TrashType type) {
        if (items.size() >= maxSlots && !items.containsKey(itemName)) {
            return false;
        }

        items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
        if (icon != null) {
            icons.put(itemName, icon);
        }
        if (type != null) {
            itemTypes.put(itemName, type);
        }
        return true;
    }

    public boolean removeItem(String itemName) {
        return removeItem(itemName, 1);
    }

    public boolean removeItem(String itemName, int quantity) {
        if (!items.containsKey(itemName)) {
            return false;
        }

        int currentQuantity = items.get(itemName);
        if (currentQuantity < quantity) {
            return false;
        }

        if (currentQuantity == quantity) {
            items.remove(itemName);
        } else {
            items.put(itemName, currentQuantity - quantity);
        }

        return true;
    }

    public boolean hasItem(String itemName) {
        return items.containsKey(itemName);
    }

    public int getItemCount(String itemName) {
        return items.getOrDefault(itemName, 0);
    }

    public java.awt.image.BufferedImage getItemIcon(String itemName) {
        return icons.get(itemName);
    }

    public TrashType getItemType(String itemName) {
        return itemTypes.get(itemName);
    }

    public List<String> getItemList() {
        List<String> itemList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            itemList.add(entry.getKey() + " x" + entry.getValue());
        }
        return itemList;
    }

    public Map<String, Integer> getAllItems() {
        return new HashMap<>(items);
    }

    public void clear() {
        items.clear();
        itemTypes.clear();
    }

    public int getUsedSlots() {
        return items.size();
    }

    public int getMaxSlots() {
        return maxSlots;
    }
}
