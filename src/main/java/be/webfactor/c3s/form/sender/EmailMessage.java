package be.webfactor.c3s.form.sender;

import be.webfactor.c3s.siteassetstore.domain.EmailAddress;

public record EmailMessage(EmailAddress from, EmailAddress to, EmailAddress replyTo, String subject, String body) {
}
