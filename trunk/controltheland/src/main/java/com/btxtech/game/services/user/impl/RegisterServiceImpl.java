package com.btxtech.game.services.user.impl;

import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.user.RegisterService;
import com.btxtech.game.services.user.User;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 26.12.12
 * Time: 10:17
 */
@Component("registerService")
public class RegisterServiceImpl implements RegisterService {
    private static final String REPLY_EMAIL = "no-reply@razarion.com";
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private VelocityEngine velocityEngine;

    private void sendEmailVerificationMail(final User user, final String link) {
        try {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setTo(user.getEmail());
                    message.setFrom(REPLY_EMAIL);
                    message.setSubject("Razarion - Bestätige deine E-Mail-Adresse");
                    Map<Object, Object> model = new HashMap<>();
                    model.put("user", user.getUsername());
                    model.put("link", link);
                    String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "com/btxtech/game/services/user/registration-confirmation.vm", model);
                    message.setText(text, true);
                }
            };
            mailSender.send(preparator);
        } catch (Exception ex) {
            ExceptionHandler.handleException(ex);
        }
    }
}
