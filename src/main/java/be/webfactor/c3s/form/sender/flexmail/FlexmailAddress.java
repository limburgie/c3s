package be.webfactor.c3s.form.sender.flexmail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
record FlexmailAddress(
        @JsonProperty("email_address") String emailAddress,
        String name) {
}
