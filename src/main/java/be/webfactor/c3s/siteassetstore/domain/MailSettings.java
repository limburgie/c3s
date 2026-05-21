package be.webfactor.c3s.siteassetstore.domain;

import be.webfactor.c3s.form.sender.MailSenderType;

public record MailSettings(MailSenderType type, String host, int port, String username, String password, String fromAddress) {
}
