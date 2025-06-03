package com.hsbc.detection.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hsbc.detection.domain.entity.Transaction;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public class TransactionDeserializer implements JsonDeserializer<Transaction> {
    @Override
    public Transaction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String transactionId = getNonEmptyString(jsonObject, "transactionId");
        String fromAccountId = getNonEmptyString(jsonObject, "fromAccountId");
        String toAccountId = getNonEmptyString(jsonObject, "toAccountId");
        BigDecimal amount = getPositiveBigDecimal(jsonObject, "amount");
        long timestamp = jsonObject.get("timestamp").getAsLong(); // Optional (no @NonNull)

        return Transaction.builder()
                .transactionId(transactionId)
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(amount)
                .timestamp(timestamp)
                .build();
    }

    private String getNonEmptyString(JsonObject jsonObject, String field) {
        if (!jsonObject.has(field)) {
            throw new JsonParseException("Missing required field: " + field);
        }
        String value = jsonObject.get(field).getAsString();
        if (value == null || value.trim().isEmpty()) {
            throw new JsonParseException("Field '" + field + "' must not be empty");
        }
        return value;
    }

    private BigDecimal getPositiveBigDecimal(JsonObject jsonObject, String field) {
        if (!jsonObject.has(field)) {
            throw new JsonParseException("Missing required field: " + field);
        }
        BigDecimal value = jsonObject.get(field).getAsBigDecimal();
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new JsonParseException("Field '" + field + "' must be a positive number");
        }
        return value;
    }
}
