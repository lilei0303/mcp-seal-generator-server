package com.lilei.mcpsealgeneratorserver.config;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 印章字体配置类
 * @Author lei.li
 */
@Data
@Builder
public class SealFont {

    /**
     * 字体内容
     */
    private String fontText;
    /**
     * 是否加粗
     */
    private Boolean isBold = true;
    /**
     * 字形名，默认为宋体
     */
    private String fontFamily = "宋体";
    /**
     * 字体大小
     */
    private Integer fontSize;
    /**
     * 字距
     */
    private Double fontSpace;
    /**
     * 边距（环边距或上边距）
     */
    private Integer marginSize;

    public SealFont setFontText(String fontText) {
        this.fontText = fontText;
        return this;
    }


    public Boolean isBold() {
        return isBold;
    }
}