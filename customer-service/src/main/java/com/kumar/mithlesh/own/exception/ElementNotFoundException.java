package com.kumar.mithlesh.own.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElementNotFoundException extends RuntimeException {
    private String message;
    @Override
    public String getMessage() {
        return this.message;
    }
}
