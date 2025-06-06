package com.lilei.mcpsealgeneratorserver.vo;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class RespImageVO {
    private String status;
    private String message;
    private String imageUrl;
    private String imageType;
}
