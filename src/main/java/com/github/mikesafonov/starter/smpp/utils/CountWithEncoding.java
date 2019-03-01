package com.github.mikesafonov.starter.smpp.utils;

import com.cloudhopper.commons.charset.Charset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Mike Safonov
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountWithEncoding {
    private int count;
    private Charset charset;

    public static CountWithEncoding empty(Charset charset){
        return new CountWithEncoding(0, charset);
    }
}
