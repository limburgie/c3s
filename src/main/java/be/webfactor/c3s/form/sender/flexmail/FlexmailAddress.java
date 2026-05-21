package be.webfactor.c3s.form.sender.flexmail;

import com.fasterxml.jackson.annotation.JsonProperty;

record FlexmailAddress(
        @JsonProperty("email_address") String emailAddress,
        String name) {
}
