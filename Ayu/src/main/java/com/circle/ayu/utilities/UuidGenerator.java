package com.circle.ayu.utilities;

import java.util.UUID;

public class UuidGenerator {

    public String UuidGenerator() {
        String uniqueId = UUID.randomUUID().toString();
        return uniqueId;
    }
}
