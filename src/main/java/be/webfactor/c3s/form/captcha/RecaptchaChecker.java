package be.webfactor.c3s.form.captcha;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Service
public class RecaptchaChecker {

    private static final String RECAPTCHA_SERVICE_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Value("${c3s.recaptcha}")
    private String recaptchaSecretKey;

    public void validate(String captcha) {
        if (StringUtils.isBlank(captcha)) {
            throw new RecaptchaException();
        }

        RecaptchaResult result = verify(captcha);

        boolean success = result.isSuccess();
        double score = result.getScore();

        System.out.println("success : " + success);
        System.out.println("score : " + score);

        if (!success || score < 0.5) {
            throw new RecaptchaException();
        }
    }

    private RecaptchaResult verify(String captcha) {
        try {
            URL obj = new URL(RECAPTCHA_SERVICE_URL);

            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes("secret=" + recaptchaSecretKey + "&response=" + captcha);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return new Gson().fromJson(response.toString(), RecaptchaResult.class);
        } catch(IOException e) {
            throw new RecaptchaException(e);
        }
    }
}
