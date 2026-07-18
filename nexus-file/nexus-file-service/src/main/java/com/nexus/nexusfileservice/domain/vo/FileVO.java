package com.nexus.nexusfileservice.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileVO {
    private String url;
    private String key;
    private String name;
}
