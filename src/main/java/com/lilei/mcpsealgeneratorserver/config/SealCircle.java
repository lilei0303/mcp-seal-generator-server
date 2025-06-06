package com.lilei.mcpsealgeneratorserver.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 印章圆形配置类
 * @author lei.li
 */
@Data
@AllArgsConstructor
@Builder
public class SealCircle {
    /**
     * 线宽度
     */
    private Integer lineSize;
    /**
     * 半径
     */

    private Integer width;
    /**
     * 半径
     */
    private Integer height;
}