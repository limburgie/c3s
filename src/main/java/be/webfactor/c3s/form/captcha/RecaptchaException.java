package be.webfactor.c3s.form.captcha;

import lombok.extern.slf4j.Slf4j;

public class RecaptchaException extends RuntimeException {

    public RecaptchaException(String message) {
        super(message);
    }

    public RecaptchaException(Throwable cause) {
        super(cause);
    }
}
