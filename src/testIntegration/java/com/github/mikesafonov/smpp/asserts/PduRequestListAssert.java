package com.github.mikesafonov.smpp.asserts;

import com.cloudhopper.smpp.pdu.PduRequest;
import org.assertj.core.api.ListAssert;

import java.util.List;
import java.util.stream.Stream;

public class PduRequestListAssert extends ListAssert<PduRequest> {
    public PduRequestListAssert(List<? extends PduRequest> actual) {
        super(actual);
    }

    public PduRequestListAssert(Stream<? extends PduRequest> actual) {
        super(actual);
    }
}
