package org.kafka.exp.model;

public class Order {
  String orderName;
  String address;
  int id;

  public Order() {}

  public String getOrderName() {
    return orderName;
  }

  public void setOrderName(String orderName) {
    this.orderName = orderName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Order{"
        + "orderName='"
        + orderName
        + '\''
        + ", address='"
        + address
        + '\''
        + ", id="
        + id
        + '}';
  }
}
