package com.jonas.weigand.thesis.smartdrinkingcup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UUIDItemContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<UUIDItemContent.UUIDItem> ITEMS = new ArrayList<UUIDItemContent.UUIDItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, UUIDItemContent.UUIDItem> ITEM_MAP = new HashMap<String, UUIDItemContent.UUIDItem>();

    public static void addItem(UUIDItemContent.UUIDItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.uuid, item);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A placeholder item representing a piece of content.
     */
    public static class UUIDItem {
        public final String uuid;

        public UUIDItem(String uuid) {
            this.uuid = uuid;
        }

        @Override
        public String toString() {
            return uuid;
        }
    }
}
