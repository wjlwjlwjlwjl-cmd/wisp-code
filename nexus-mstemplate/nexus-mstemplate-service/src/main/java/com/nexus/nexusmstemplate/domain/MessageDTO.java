package com.nexus.nexusmstemplate.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageDTO {
    private String type;
    private String content;
}
