package com.btxtech.game.controllers;

import com.btxtech.game.jsre.common.PayPalUtils;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.finance.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;

/**
 * User: beat
 * Date: 14.08.12
 * Time: 11:04
 */
@Component("payPalIpnHandlerController")
public class PayPalIpnHandlerController implements Controller {
    private static final String PAY_PAL_URL = "https://www.paypal.com/cgi-bin/webscr";
    private static final String SANDBOX_PAY_PAL_URL = "https://www.sandbox.paypal.com/cgi-bin/webscr";
    @Autowired
    private FinanceService financeService;

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse httpServletResponse) throws Exception {
        String params = "No set yet";
        try {
            params = buildParamString(request);
            String res = sendVerification(params);
            if (res.equals("VERIFIED")) {
                financeService.razarionBought(request.getParameter("custom"),
                        request.getParameter("item_number"),
                        request.getParameter("mc_gross"),
                        request.getParameter("mc_currency"),
                        request.getParameter("txn_id"),
                        request.getParameter("payer_email"),
                        request.getParameter("receiver_email"),
                        request.getParameter("payment_status"),
                        request.getParameter("quantity"));
            } else {
                throw new IllegalStateException("Return value from verification is wrong: " + res);
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e, "PayPal IPN failed: " + params);
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        return null;
    }

    private String sendVerification(String params) throws IOException {
        // post back to PayPal system to validate
        // NOTE: change http: to https: in the following URL to verify using SSL (for increased security).
        // using HTTPS requires either Java 1.4 or greater, or Java Secure Socket Extension (JSSE)
        // and configured for older versions.
        URL u = new URL(PayPalUtils.IS_SANDBOX ? SANDBOX_PAY_PAL_URL : PAY_PAL_URL);
        HttpsURLConnection uc = (HttpsURLConnection) u.openConnection();
        uc.setDoOutput(true);
        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        uc.setRequestProperty("Host", "www.paypal.com");
        PrintWriter pw = new PrintWriter(uc.getOutputStream());
        pw.println(params);
        pw.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String res = in.readLine();
        in.close();
        return res;
    }

    private String buildParamString(HttpServletRequest request) {
        // read post from PayPal system and add 'cmd'
        Enumeration en = request.getParameterNames();
        StringBuilder builder = new StringBuilder("cmd=_notify-validate");
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            String paramValue = request.getParameter(paramName);
            builder.append("&").append(paramName).append("=").append(URLEncoder.encode(paramValue));
        }
        return builder.toString();
    }
}
