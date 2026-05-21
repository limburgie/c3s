package be.webfactor.c3s.form.sender.flexmail;

import com.fasterxml.jackson.annotation.JsonProperty;

record FlexmailSendEmailRequest(
		FlexmailRecipient recipient,
		FlexmailAddress from,
		@JsonProperty("reply_to") FlexmailAddress replyTo,
		String subject,
		FlexmailContent content) {

	FlexmailSendEmailRequest(FlexmailAddress sender, FlexmailAddress receiver, FlexmailAddress replyTo, String subject, String body) {
		this(new FlexmailRecipient(receiver), sender, replyTo, subject, new FlexmailContent(body));
	}
}
