package com.github.jamorin.androidtv.client;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Api Request.
 * command is pause, sleep, poweron, enter, volumeup, etc...
 */
@Data
public class Request {

    @NotBlank
    private String command;
}
