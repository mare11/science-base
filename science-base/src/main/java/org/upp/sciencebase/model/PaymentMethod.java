package org.upp.sciencebase.model;

import lombok.Getter;

@Getter
public enum PaymentMethod {

    READERS("Charge readers"),
    AUTHORS("Charge authors");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }
}
