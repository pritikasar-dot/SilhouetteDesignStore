package com.mystore.utility;

import java.util.ArrayList;
import java.util.List;

public class OrderContext {

    // Store a list of order strings (e.g., "Credit: 2004341801")
    private static List<String> orderList = new ArrayList<>();

    public static void setOrderDetails(String orderId, String type) {
        orderList.add(type + " Order ID: " + orderId);
    }

    public static List<String> getAllOrders() {
        return orderList;
    }

    public static void clearOrders() {
        orderList.clear();
    }
}