package org.com.dianping.controller;

import org.com.dianping.testutils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CaptchaController.class)
class CaptchaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getCaptcha_ShouldGenerateCaptchaImage_AndSetSessionAttribute() throws Exception {
        // 使用工具类创建 session
        MockHttpSession session = TestUtils.createSession(null);

        MvcResult result = mockMvc.perform(get("/captcha")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andReturn();

        // 验证生成了图片
        MockHttpServletResponse response = result.getResponse();
        byte[] contentAsByteArray = response.getContentAsByteArray();
        assertTrue(contentAsByteArray.length > 0, "Image content should not be empty");

        // 验证session中存储了验证码
        String captchaCode = (String) session.getAttribute("CAPTCHA_CODE");
        assertNotNull(captchaCode, "Captcha code should be set in session");
        assertEquals(4, captchaCode.length(), "Captcha code should be 4 digits");
        assertTrue(captchaCode.matches("\\d{4}"), "Captcha code should contain only digits");
    }
}
