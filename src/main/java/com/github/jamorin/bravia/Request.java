package com.github.jamorin.bravia;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class Request {

    @NotBlank
    private String command;

    /**
     * This is for the CreatedDate of IFTTT.
     */
    private String date;

}
