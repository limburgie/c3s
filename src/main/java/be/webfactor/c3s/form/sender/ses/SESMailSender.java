package be.webfactor.c3s.form.sender.ses;

import be.webfactor.c3s.form.sender.EmailMessage;
import be.webfactor.c3s.form.sender.MailSender;
import be.webfactor.c3s.form.sender.MailSenderType;
import be.webfactor.c3s.siteassetstore.domain.EmailAddress;
import be.webfactor.c3s.siteassetstore.domain.MailSettings;
import jakarta.mail.internet.InternetAddress;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.SesV2ClientBuilder;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@Primary
@Slf4j
public class SESMailSender implements MailSender {

    private static final String DEFAULT_SES_REGION = "eu-west-3";

    @Override
    public void send(MailSettings settings, EmailMessage message) {
        String accessKey = settings.username();
        String secretKey = settings.password();

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        SesV2ClientBuilder builder = SesV2Client.builder()
                .region(Region.of(Optional.ofNullable(settings.host()).orElse(DEFAULT_SES_REGION)))
                .credentialsProvider(StaticCredentialsProvider.create(credentials));

        try (SesV2Client sesClient = builder.build()) {
            SendEmailRequest.Builder request = SendEmailRequest.builder()
                    .fromEmailAddress(formatAddress(message.from()))
                    .destination(Destination.builder()
                            .toAddresses(formatAddress(message.to()))
                            .build())
                    .content(emailContent(message));

            if (message.replyTo() != null) {
                request.replyToAddresses(formatAddress(message.replyTo()));
            }

            sesClient.sendEmail(request.build());
        } catch (Exception e) {
            log.error("Failed to send SES email {}", message, e);
        }
    }

    private EmailContent emailContent(EmailMessage message) {
        return EmailContent.builder()
                .simple(Message.builder()
                        .subject(content(message.subject()))
                        .body(Body.builder().html(content(message.body())).build())
                        .build())
                .build();
    }

    private Content content(String data) {
        return Content.builder().data(data).charset(StandardCharsets.UTF_8.name()).build();
    }

    @SneakyThrows
    private String formatAddress(EmailAddress emailAddress) {
        return new InternetAddress(emailAddress.address(), emailAddress.name(), StandardCharsets.UTF_8.name()).toString();
    }

    @Override
    public MailSenderType getType() {
        return MailSenderType.SES;
    }
}
