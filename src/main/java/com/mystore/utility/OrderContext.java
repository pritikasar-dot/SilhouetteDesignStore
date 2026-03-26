package com.mystore.utility;

public class OrderContext {

    private static String orderId;
    private static String orderType;

    // ✅ Set both values
    public static void setOrderDetails(String id, String type) {
        orderId = id;
        orderType = type;
    }

    // ✅ Get Order ID
    public static String getOrderId() {
        return orderId;
    }

    // ✅ Get Order Type (Credit / Subscription)
    public static String getOrderType() {
        return orderType;
    }

    // ✅ Optional: clear after run
    public static void clear() {
        orderId = null;
        orderType = null;
    }
}