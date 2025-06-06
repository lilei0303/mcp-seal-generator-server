package com.lilei.mcpsealgeneratorserver.tools;



import com.lilei.mcpsealgeneratorserver.config.SealCircle;
import com.lilei.mcpsealgeneratorserver.config.SealConfiguration;
import com.lilei.mcpsealgeneratorserver.config.SealFont;
import com.lilei.mcpsealgeneratorserver.utils.MinioUtil;
import com.lilei.mcpsealgeneratorserver.utils.SealUtil;
import com.lilei.mcpsealgeneratorserver.vo.RespImageVO;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

@Service
public class SealGenerationService {
    @Resource
    MinioUtil minioUtil;


    /**
     * 生成公章图片
     *
     * @param mainText          主文字内容
     * @param centerText        中心文字内容
     * @param viceText          副文字内容
     * @param titleText         抬头文字内容
     * @param sealShape         公章形状，只有圆形和椭圆形，默认是圆形
     * @param imageSize         图片尺寸(像素)，默认300
     * @param borderSize        边框粗细，默认为3
     * @param borderRadius      边框半径，默认140
     * @param innerBorderSize   内边线粗细，如果没有则为null
     * @param innerBorderRadius 内边线半径，如果没有则为null
     * @param innerCircleSize   内环线粗细径，如果没有则为null
     * @param innerCircleRadius 内环线半径，如果没有则为null
     * @return 包含公章图片数据的响应
     */
    @Tool(name = "generateOfficialSeal", description = "生成公章图片,如果没有提供相应参数，则按照默认参数")
    public String generateOfficialSeal(
            @ToolParam(description = "主文字内容", required = false) String mainText,
            @ToolParam(description = "主文字大小，默认30px", required = false) Integer mainTextSize,
            @ToolParam(description = "中心文字内容", required = false) String centerText,
            @ToolParam(description = "中心文字大小，默认25px", required = false) Integer centerTextSize,
            @ToolParam(description = "副文字内容", required = false) String viceText,
            @ToolParam(description = "副文字大小，默认20px", required = false) Integer viceTextSize,
            @ToolParam(description = "抬头文字内容", required = false) String titleText,
            @ToolParam(description = "抬头文字大小，默认22px", required = false) Integer titleTextSize,
            @ToolParam(description = "公章形状，只有圆形和椭圆形，默认圆形", required = false) String sealShape,
            @ToolParam(description = "图片尺寸(像素)，默认300px", required = false) Integer imageSize,
            @ToolParam(description = "公章背景颜色，默认为RED（红色）、还可以为BLUE（蓝色）、BLACK（黑色）") String backgroudColor,
            @ToolParam(description = "边框粗细，默认为3px", required = false) Integer borderSize,
            @ToolParam(description = "边框半径，默认140px", required = false) Integer borderRadius,
            @ToolParam(description = "是否有内边线，true或false，默认为true") boolean hasInnerBorder,
            @ToolParam(description = "内边线粗细，如果没有则为null", required = false) Integer innerBorderSize,
            @ToolParam(description = "内边线半径，如果没有则为null", required = false) Integer innerBorderRadius,
            @ToolParam(description = "是否有内环线，true或false，默认为false") boolean hasInnerCircle,
            @ToolParam(description = "内环线粗细径，如果没有则为null", required = false) Integer innerCircleSize,
            @ToolParam(description = "内环线半径，如果没有则为null", required = false) Integer innerCircleRadius
    ) {

        try {
            // 使用 Builder 创建公章配置
            SealConfiguration config = SealConfiguration.builder()
                    .mainFont(createMainFont(mainText, mainTextSize)) // 主文字字体配置
                    .centerFont(createCenterFont(centerText, centerTextSize)) // 中心文字字体配置
                    .viceFont(createViceFont(viceText, viceTextSize)) // 副文字字体配置
                    .titleFont(createTitleFont(titleText, titleTextSize)) // 抬头文字字体配置
                    .imageSize(imageSize != null ? imageSize : 300) // 图片尺寸，默认 300
                    .backgroudColor(getColor(backgroudColor)) // 背景颜色，默认红色
                    .borderCircle(createBorderCircle(sealShape, borderSize, borderRadius)) // 边框圆形配置
                    .borderInnerCircle(hasInnerBorder ? createBorderInnerCircle(sealShape, innerBorderSize, innerBorderRadius) : null) // 内边框圆形配置
                    .innerCircle(hasInnerCircle ? createInnerCircle(sealShape, innerCircleSize, innerCircleRadius) : null) // 内部圆形配置
                    .build();

            // 生成公章图片
            BufferedImage sealImage = SealUtil.buildSeal(config);

            String url = minioUtil.returnImageUrl(sealImage);

            return RespImageVO.builder()
                    .status("success")
                    .message("公章生成成功！以下是生成的公章图片URL：[图片URL]。如需进一步自定义公章，您可提供以下信息：" +
                            "主文字、中心文字、副文字、抬头文字、公章形状、图片尺寸、边框粗细、边框半径、内边线粗细、内边线半径、内环线粗细、内环线半径及文字大小等。")
                    .imageUrl(url)
                    .imageType("image/png")
                    .build().toString();
        } catch (Exception e) {
            return RespImageVO.builder()
                    .status("error")
                    .message("公章生成失败: " + e.getMessage())
                    .imageUrl(null)
                    .imageType(null)
                    .build().toString();
        }
    }


    /**
     * 生成个人印章
     *
     * @param nameText  姓名文字（必需）
     * @param sealText  印章文字（可选，默认 "印"）
     * @param imageSize 图片尺寸（可选，默认 300）
     * @param lineSize  线条粗细（可选，默认 16）
     * @param fontSize  字体大小（可选，默认 120）
     * @return 包含印章图片数据的响应
     */
    @Tool(name = "generatePersonalSeal", description = "生成个人印章图片")
    public String generatePersonalSeal(
            @ToolParam(description = "姓名文字") String nameText,
            @ToolParam(description = "印章文字(默认'印')", required = false) String sealText,
            @ToolParam(description = "图片尺寸(像素)，默认300", required = false) Integer imageSize,
            @ToolParam(description = "线条粗细，默认16", required = false) Integer lineSize,
            @ToolParam(description = "字体大小，默认120", required = false) Integer fontSize) {

        try {
            // 使用 Builder 创建字体配置
            SealFont font = SealFont.builder()
                    .fontText(nameText != null ? nameText : "你的名字")
                    .fontSize(fontSize != null ? fontSize : 120)
                    .isBold(true)
                    .fontFamily("楷体")
                    .build();

            // 生成私章图片
            BufferedImage sealImage = SealUtil.buildPersonSeal(
                    imageSize != null ? imageSize : 300,
                    lineSize != null ? lineSize : 16,
                    font,
                    sealText != null ? sealText : "印"
            );

            String url = minioUtil.returnImageUrl(sealImage);

            return RespImageVO.builder()
                    .status("success")
                    .message("私章生成成功！以下是生成的私章图片URL：[图片URL]。如需进一步自定义私章，您可提供以下信息:" +
                            "姓名文字、印章文字、图片尺寸、线条粗细和字体大小等。")
                    .imageUrl(url)
                    .imageType("image/png")
                    .build().toString();
        } catch (Exception e) {
            return RespImageVO.builder()
                    .status("error")
                    .message("私章生成失败: " + e.getMessage())
                    .imageUrl(null)
                    .imageType(null)
                    .build().toString();
        }
    }

    /********************************** 辅助方法 **********************************/

    // 创建主文字字体配置
    private SealFont createMainFont(String text, Integer size) {
        text = text != null ? text : "";
        size = size != null ? size : 30;
        return SealFont.builder()
                .fontText(text)
                .fontSize(size)
                .isBold(true)
                .fontFamily("楷体")
                .marginSize(10)
//                .fontSpace(12.0)
                .build();
    }

    // 创建副文字字体配置
    private SealFont createViceFont(String text, Integer size) {
        text = text != null ? text : "";
        size = size != null ? size : 20;
        return SealFont.builder()
                .fontText(text)
                .fontSize(size)
                .isBold(true)
                .fontFamily("宋体")
                .marginSize(5)
//                .fontSpace(12.0)
                .build();
    }

    // 创建中心文字字体配置
    private SealFont createCenterFont(String text, Integer size) {
        text = text != null ? text : "";
        size = size != null ? size : 25;
        return SealFont.builder()
                .fontText(text)
                .fontSize(size)
                .isBold(true)
                .fontFamily("宋体")
                .build();
    }

    // 创建抬头文字字体配置
    private SealFont createTitleFont(String text, Integer size) {
        text = text != null ? text : "";
        size = size != null ? size : 22;
        return SealFont.builder()
                .fontText(text)
                .fontSize(size)
                .isBold(true)
                .fontFamily("宋体")
                .marginSize(27)
                .build();
    }

    // 创建边框圆形配置
    private SealCircle createBorderCircle(String sealShape, Integer size, Integer radius) {
        if (sealShape != null && sealShape.contains("椭圆")) {
            size = size != null ? size : 3;
            Integer width = radius != null ? radius : 140;
            Integer height = radius != null ? radius - 40: 100;
            return SealCircle.builder()
                    .lineSize(size)
                    .width(width)
                    .height(height)
                    .build();
        } else {
            size = size != null ? size : 3;
            radius = radius != null ? radius : 140;
            return SealCircle.builder()
                    .lineSize(size)
                    .width(radius)
                    .height(radius)
                    .build();
        }
    }

    private SealCircle createBorderInnerCircle(String sealShape, Integer size, Integer radius) {
        if (sealShape != null && sealShape.contains("椭圆")) {
            size = size != null ? size : 1;
            Integer width = radius != null ? radius : 135;
            Integer height = radius != null ? radius - 40 : 95;
            return SealCircle.builder()
                    .lineSize(size)
                    .width(width)
                    .height(height)
                    .build();
        } else {
            size = size != null ? size : 1;
            radius = radius != null ? radius - 5 : 135;
            return SealCircle.builder()
                    .lineSize(size)
                    .width(radius)
                    .height(radius)
                    .build();
        }
    }

    private SealCircle createInnerCircle(String sealShape, Integer size, Integer radius) {
        if (sealShape != null && sealShape.contains("椭圆")) {
            size = size != null ? size : 2;
            Integer width = radius != null ? radius - 55 : 85;
            Integer height = radius != null ? radius - 55 : 45;
            return SealCircle.builder()
                    .lineSize(size)
                    .width(width)
                    .height(height)
                    .build();
        } else {
            size = size != null ? size : 2;
            radius = radius != null ? radius - 55 : 85;
            return SealCircle.builder()
                    .lineSize(size)
                    .width(radius)
                    .height(radius)
                    .build();
        }
    }

    private Color getColor(String color){
        if (color == null || color.isEmpty()) {
            return Color.RED; // 默认红色
        }
        return switch (color) {
            case "BLUE" -> Color.BLUE;
            case "BLACK" -> Color.BLACK;
            default -> Color.RED;
        };
    }
}