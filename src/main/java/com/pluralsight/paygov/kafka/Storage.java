package com.pluralsight.paygov.kafka;

import  com.pluralsight.paygov.domain.Payment;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    private final List<Payment> users = new ArrayList<>();

    public void add(Payment pay) {
        users.add(pay);
    }

    public void clear() {
        users.remove(0);
    }

    public List<Payment> getAll() {
        return users;
    }
}
