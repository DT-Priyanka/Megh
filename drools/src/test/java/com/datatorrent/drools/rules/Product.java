package com.datatorrent.drools.rules;

public class Product
{
  private long productId;
  private String type;
  private int discount;

  public Product(long productId, String type, int discount)
  {
    this.productId = productId;
    this.type = type;
    this.discount = discount;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public int getDiscount()
  {
    return discount;
  }

  public void setDiscount(int discount)
  {
    this.discount = discount;
  }

  public long getProductId()
  {
    return productId;
  }

  public void setProductId(long productId)
  {
    this.productId = productId;
  }

  @Override
  public String toString()
  {
    return "Product [productId=" + productId + ", type=" + type + ", discount=" + discount + "]";
  }
}
