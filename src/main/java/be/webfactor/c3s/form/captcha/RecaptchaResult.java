package be.webfactor.c3s.form.captcha;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class RecaptchaResult {

    private static final String DEFAULT_ACTION = "submit";
    private static final double SCORE_THRESHOLD = 0.7;

    @SerializedName("challenge_ts")
    private Instant challengeTs;

    private boolean success;
    private double score;
    private String hostname;
    private String action;

    @SerializedName("error-codes")
    private List<String> errorCodes;

    public boolean isValid(String hostname) {
        return success &&
                score >= SCORE_THRESHOLD &&
                DEFAULT_ACTION.equals(action) &&
                challengeTs.isAfter(Instant.now().minusSeconds(120)) &&
                this.hostname.equalsIgnoreCase(hostname);
    }
}
