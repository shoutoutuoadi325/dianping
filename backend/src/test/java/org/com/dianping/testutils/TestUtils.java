package org.com.dianping.testutils;

import org.springframework.mock.web.MockHttpSession;

public class TestUtils {
    public static MockHttpSession createSession(String captcha) {
        MockHttpSession session = new MockHttpSession();
        if (captcha != null) {
            session.setAttribute("CAPTCHA_CODE", captcha);
        }
        return session;
    }
}
