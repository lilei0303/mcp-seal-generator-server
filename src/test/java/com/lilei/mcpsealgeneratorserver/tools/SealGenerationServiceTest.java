package com.lilei.mcpsealgeneratorserver.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SealGenerationServiceTest {
    @Resource
    private SealGenerationService sealGenerationService;

    @Test
    void generateOfficialSeal() {
        String sealImage = sealGenerationService.generateOfficialSeal("李雷", null, "2023年10月1日",null,
                "1232134324",null,null,null,"圆形",null,null,null,
                null, true, null, null, false, null, null);
        assertNotNull(sealImage, "生成的印章图片不应为 null");
        assertFalse(sealImage.isEmpty(), "生成的印章图片不应为空");
    }

    @Test
    void generatePersonalSeal() {
        String sealImage = sealGenerationService.generatePersonalSeal("李雷",null,null,null,null);
        assertNotNull(sealImage, "生成的个人印章图片不应为 null");
        assertFalse(sealImage.isEmpty(), "生成的个人印章图片不应为空");
    }
}