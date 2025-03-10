package com.lets.go.right.now.domain.trip.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum SortOption {

    LATEST("latest", "최신순"),
    OLDEST("oldest", "오래된순"),
    HIGHEST_EXPENSE("highest_expense", "지출 많은 순"),
    LOWEST_EXPENSE("lowest_expense", "지출 적은 순");

    private final String value;
    private final String description;

    SortOption(String value, String description) {
        this.value = value;
        this.description = description;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static SortOption fromValue(String value) {
        return Arrays.stream(SortOption.values())
                .filter(option -> option.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid sort option: " + value));
    }
}
