package com.github.mikesafonov.smpp.core.sender;

import lombok.experimental.UtilityClass;

/**
 * @author Mike Safonov
 */
@UtilityClass
public class DataCoding {
    public static final byte FLASH_CODING = (byte) 0x18;
    public static final byte SILENT_CODING = (byte) 0xC0;

}
