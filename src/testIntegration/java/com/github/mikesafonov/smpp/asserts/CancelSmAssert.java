package com.github.mikesafonov.smpp.asserts;

import com.cloudhopper.smpp.pdu.CancelSm;
import org.assertj.core.api.AbstractAssert;

public class CancelSmAssert extends AbstractAssert<CancelSmAssert, CancelSm> {
    public CancelSmAssert(CancelSm cancelSm) {
        super(cancelSm, CancelSmAssert.class);
    }

    public CancelSmAssert hasId(String smscId) {
        isNotNull();
        String actualMessageId = actual.getMessageId();
        if (!actualMessageId.equals(smscId)) {
            failWithMessage("Expected id <%s> but was <%s>",
                    smscId, actualMessageId);
        }
        return this;
    }

    public CancelSmAssert hasSource(String source) {
        isNotNull();
        String address = actual.getSourceAddress().getAddress();
        if (!source.equals(address)) {
            failWithMessage("Expected source address <%s> but was <%s>", source, address);
        }
        return this;
    }

    public CancelSmAssert hasDest(String dest) {
        isNotNull();
        String address = actual.getDestAddress().getAddress();
        if (!dest.equals(address)) {
            failWithMessage("Expected dest address <%s> but was <%s>", dest, address);
        }
        return this;
    }
}
