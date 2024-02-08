package be.webfactor.c3s.form.captcha;

import lombok.Data;

@Data
public class RecaptchaResult {

    private boolean success;
    private double score;
}
