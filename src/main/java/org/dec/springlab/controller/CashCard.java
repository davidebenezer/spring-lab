package org.dec.springlab.controller;

import org.springframework.data.annotation.Id;

public record CashCard(@Id Long id, double amount, String owner) {
}
