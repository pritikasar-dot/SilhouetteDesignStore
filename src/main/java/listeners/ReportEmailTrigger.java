package listeners;

import com.mystore.utility.OrderContext;
import com.mystore.utility.TestResultReporter;
import org.testng.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;

/**
 * 📧 ReportEmailTrigger
 * Sends HTML summary email with reports + screenshots + order details
 */
public class ReportEmailTrigger implements ISuiteListener {

    // ================= SMTP CONFIG =================
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USER = "priti.kasar@magnetoitsolutions.com";
    private static final String SMTP_PASS = "jcppaxakvelzvtwi";

    // ================= RECIPIENTS =================
    private static final String TO_EMAILS = "kaspritiautomation@gmail.com";
    private static final String CC_EMAILS = "priti.kasar+1@magnetoitsolutions.com";
    private static final String BCC_EMAILS = "pritik.magneto@gmail.com";

    // ================= FILE PATHS =================
    private static final String REPORT_HTML =
            System.getProperty("user.dir") + "/test-output/ExtentReport.html";
    private static final String SCREENSHOT_DIR =
            System.getProperty("user.dir") + "/Screenshots/";

    private static final String SUBJECT =
            "🧾 Silhouette Design Store | Automation Report";

    @Override
    public void onStart(ISuite suite) {
        System.out.println("🚀 Suite Started: " + suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {

        System.out.println("📧 Sending execution report email...");

        try {
            // Generate reports
            String csvReport = TestResultReporter.generateCSVReport(suite);
            String docxReport = TestResultReporter.generateDOCXReport(suite);

            // Setup SMTP
            Session session = Session.getInstance(getSMTPProperties(), new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USER, SMTP_PASS);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USER, "Automation Framework"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAILS));

            if (!CC_EMAILS.isEmpty())
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_EMAILS));

            if (!BCC_EMAILS.isEmpty())
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(BCC_EMAILS));

            message.setSubject(SUBJECT);

            Multipart multipart = new MimeMultipart();

            // HTML BODY
            MimeBodyPart htmlBody = new MimeBodyPart();
            htmlBody.setContent(buildEmailBody(suite, multipart), "text/html; charset=utf-8");

            multipart.addBodyPart(htmlBody);

            // Attach reports
            attachIfExists(multipart, REPORT_HTML);
            attachIfExists(multipart, csvReport);
            attachIfExists(multipart, docxReport);

            message.setContent(multipart);
            Transport.send(message);

            System.out.println("✅ Email sent successfully!");

        } catch (Exception e) {
            System.err.println("❌ Email sending failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================= EMAIL BODY =================
    private String buildEmailBody(ISuite suite, Multipart multipart) {

        return new StringBuilder()
                .append("<html><body style='font-family:Segoe UI;'>")

                .append("<h2 style='color:#007BFF;'>Automation Execution Summary</h2>")
                .append("<p>Suite execution completed successfully.</p>")

                // ✅ Order Details Section
                .append("<hr>")
                .append("<h3>🧾 Order Details</h3>")
                .append("<ul>")
                .append("<li><b>Order ID:</b> ").append(getSafe(OrderContext.getOrderId())).append("</li>")
                .append("<li><b>Order Type:</b> ").append(getSafe(OrderContext.getOrderType())).append("</li>")
                .append("</ul>")

                // ✅ Test Summary
                .append("<hr>")
                .append(buildTestSummary(suite))

                // ✅ Screenshots
                .append("<hr><h4>📸 Failed Test Screenshots</h4>")
                .append(attachScreenshots(multipart))

                // Footer
                .append("<hr><p style='font-size:12px;color:gray;'>")
                .append("This is an automated email.<br>")
                .append("<b>Automation Framework</b>")
                .append("</p>")

                .append("</body></html>")
                .toString();
    }

    // ================= TEST SUMMARY =================
    private String buildTestSummary(ISuite suite) {

        StringBuilder summary = new StringBuilder();

        for (ISuiteResult result : suite.getResults().values()) {

            ITestContext context = result.getTestContext();

            summary.append("<h3>📋 Test Summary</h3>")
                    .append("<ul>")
                    .append("<li><b>Total:</b> ").append(context.getAllTestMethods().length).append("</li>")
                    .append("<li style='color:green;'>Passed: ").append(context.getPassedTests().size()).append("</li>")
                    .append("<li style='color:red;'>Failed: ").append(context.getFailedTests().size()).append("</li>")
                    .append("<li style='color:orange;'>Skipped: ").append(context.getSkippedTests().size()).append("</li>")
                    .append("</ul>");

            appendTestList(summary, "✅ Passed", context.getPassedTests());
            appendTestList(summary, "❌ Failed", context.getFailedTests());
            appendTestList(summary, "⚠️ Skipped", context.getSkippedTests());
        }

        return summary.toString();
    }

    private void appendTestList(StringBuilder sb, String title, IResultMap results) {

        sb.append("<h4>").append(title).append("</h4><ul>");

        for (ITestResult result : results.getAllResults()) {
            String desc = result.getMethod().getDescription();
            if (desc != null && !desc.isEmpty()) {
                sb.append("<li>").append(desc).append("</li>");
            }
        }

        sb.append("</ul>");
    }

    // ================= SCREENSHOTS =================
    private String attachScreenshots(Multipart multipart) {

        StringBuilder sb = new StringBuilder();
        File folder = new File(SCREENSHOT_DIR);

        File[] screenshots = folder.listFiles((dir, name) ->
                name.contains("_FAILED_") && name.endsWith(".png"));

        if (screenshots != null && screenshots.length > 0) {

            int i = 1;

            for (File file : screenshots) {
                String cid = "img" + i;

                sb.append("<p><b>").append(file.getName()).append("</b><br>")
                        .append("<img src='cid:").append(cid)
                        .append("' width='600'/></p>");

                try {
                    MimeBodyPart img = new MimeBodyPart();
                    img.setDataHandler(new DataHandler(new FileDataSource(file)));
                    img.setHeader("Content-ID", "<" + cid + ">");
                    img.setDisposition(MimeBodyPart.INLINE);
                    multipart.addBodyPart(img);
                } catch (Exception e) {
                    System.err.println("⚠️ Screenshot attach failed: " + e.getMessage());
                }

                i++;
            }

        } else {
            sb.append("<p>No failed screenshots.</p>");
        }

        return sb.toString();
    }

    // ================= ATTACH FILE =================
    private void attachIfExists(Multipart multipart, String path) {

        if (path == null) return;

        File file = new File(path);

        if (!file.exists()) return;

        try {
            MimeBodyPart part = new MimeBodyPart();
            part.setDataHandler(new DataHandler(new FileDataSource(file)));
            part.setFileName(file.getName());
            multipart.addBodyPart(part);

        } catch (Exception e) {
            System.err.println("⚠️ Attachment failed: " + file.getName());
        }
    }

    // ================= SMTP CONFIG =================
    private Properties getSMTPProperties() {

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return props;
    }

    // ================= NULL SAFETY =================
    private String getSafe(String value) {
        return (value == null || value.isEmpty()) ? "N/A" : value;
    }
}