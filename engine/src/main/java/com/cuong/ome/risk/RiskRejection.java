package com.cuong.ome.risk;

import java.util.Objects;

public record RiskRejection(RejectReason reason, String message) {

    public RiskRejection {
        Objects.requireNonNull(reason, "reason");
        Objects.requireNonNull(message, "message");
    }
}
