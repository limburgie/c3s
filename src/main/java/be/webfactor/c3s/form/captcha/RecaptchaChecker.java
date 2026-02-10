package be.webfactor.c3s.form.captcha;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;

@Service
public class RecaptchaChecker {

    private static final String RECAPTCHA_SERVICE_URL = "https://www.google.com/recaptcha/api/siteverify";

    private Gson gson;

    @Value("${c3s.recaptcha}")
    private String recaptchaSecretKey;

    @PostConstruct
    public void init() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class,
                        (JsonDeserializer<Instant>) (json, type, ctx) ->
                                Instant.parse(json.getAsString()))
                .create();
    }

    public void validate(String captcha, String hostname) {
        if (StringUtils.isBlank(captcha)) {
            throw new RecaptchaException("No captcha provided");
        }

        RecaptchaResult result = verify(captcha);

        if (!result.isValid(hostname)) {
            throw new RecaptchaException("Captcha check failed: " + result);
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

            return gson.fromJson(response.toString(), RecaptchaResult.class);
        } catch (IOException e) {
            throw new RecaptchaException(e);
        }
    }
}
