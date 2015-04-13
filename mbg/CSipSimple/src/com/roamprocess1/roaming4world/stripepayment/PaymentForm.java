package com.roamprocess1.roaming4world.stripepayment;

public interface PaymentForm {
    public String getCardNumber();
    public String getCvc();
    public Integer getExpMonth();
    public Integer getExpYear();

}
