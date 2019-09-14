package com.github.mikesafonov.smpp.core.utils;

import com.cloudhopper.commons.charset.Charset;
import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
public class CountWithEncoding {
    private final int count;
    private final Charset charset;

    public static CountWithEncoding empty(Charset charset){
        return new CountWithEncoding(0, charset);
    }
}
