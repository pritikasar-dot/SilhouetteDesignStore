package listeners;

import com.mystore.utility.TestResultReporter;
import org.testng.*;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;

/**
 * 📧 ReportEmailTrigger
 * ---------------------
 * Sends a polished HTML summary email with test descriptions,
 * screenshots, and attached Extent / CSV / DOCX reports
 * after the entire TestNG suite execution completes.
 */
public class ReportEmailTrigger implements ISuiteListener {

    // =================== ✉️ SMTP CONFIGURATION ===================
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USER = "priti.kasar@magnetoitsolutions.com";
    private static final String SMTP_PASS = "jcppaxakvelzvtwi"; // Gmail app password (not personal password)

    // =================== 📤 RECIPIENTS ===================
 private static final String TO_EMAILS = "kaspritiautomation@gmail.com";
 //   private static final String TO_EMAILS = "kaspritiautomation@gmail.com";
    private static final String CC_EMAILS = "priti.kasar+1@magnetoitsolutions.com,jaimin.b@magnetoitsolutions.com";
    private static final String BCC_EMAILS = "pritik.magneto@gmail.com";


    // =================== 📁 FILE PATHS ===================
    private static final String REPORT_HTML = System.getProperty("user.dir") + "/test-output/ExtentReport.html";
    private static final String SCREENSHOT_DIR = System.getProperty("user.dir") + "/Screenshots/";

    // =================== 📩 EMAIL CONTENT ===================
    private static final String SUBJECT = "🧾 Silhouette Design Store | Automation Test Execution Report";

    @Override
    public void onStart(ISuite suite) {
        System.out.println("🚀 Starting suite execution: " + suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("📧 Preparing test summary email...");

        try {
            // 1️⃣ Generate additional reports (if any)
            String csvReport = TestResultReporter.generateCSVReport(suite);
            String docxReport = TestResultReporter.generateDOCXReport(suite);

            // 2️⃣ Configure SMTP session
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USER, SMTP_PASS);
                }
            });

            // 3️⃣ Build main email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USER, "Automation Framework"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAILS));

            if (!CC_EMAILS.isEmpty())
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_EMAILS));
            if (!BCC_EMAILS.isEmpty())
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(BCC_EMAILS));

            message.setSubject(SUBJECT);

            Multipart multipart = new MimeMultipart();

            // 4️⃣ Create HTML Body
            MimeBodyPart htmlBody = new MimeBodyPart();
            StringBuilder html = new StringBuilder();

            html.append("<html><body style='font-family:Segoe UI,Arial,sans-serif;'>")
                .append("<h2 style='color:#007BFF;'>Silhouette Design Store Automation Summary</h2>")
                .append("<p>The automation suite has finished execution. Below is the summary:</p>")
                .append("<hr>")
                .append(buildTestSummary(suite))
                .append("<hr><h4>📸 Screenshot Preview</h4>")
                .append(attachScreenshots(multipart))
                .append("<hr><p>Attached detailed reports:</p><ul>")
                .append("<li>ExtentReport.html</li>")
                .append("<li>CSV Report</li>")
                .append("<li>DOCX Report</li>")
                .append("</ul>")
                .append("<p style='font-size:13px;color:gray;'>")
                .append("This is an automated email. Please do not reply directly.<br>")
                .append("— <b>Automation Framework</b>")
                .append("</p></body></html>");

            htmlBody.setContent(html.toString(), "text/html; charset=utf-8");
            multipart.addBodyPart(htmlBody);

            // 5️⃣ Attach reports
            attachIfExists(multipart, REPORT_HTML);
            attachIfExists(multipart, csvReport);
            attachIfExists(multipart, docxReport);

            // 6️⃣ Send email
            message.setContent(multipart);
            Transport.send(message);
            System.out.println("✅ Email sent successfully with detailed test descriptions and attachments!");

        } catch (Exception e) {
            System.err.println("❌ Failed to send summary email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =================== 🧾 BUILD TEST SUMMARY ===================
    private String buildTestSummary(ISuite suite) {
        StringBuilder summary = new StringBuilder();

        for (ISuiteResult suiteResult : suite.getResults().values()) {
            ITestContext context = suiteResult.getTestContext();

            int total = context.getAllTestMethods().length;
            int passed = context.getPassedTests().size();
            int failed = context.getFailedTests().size();
            int skipped = context.getSkippedTests().size();

            summary.append("<h3>📋 Test Summary</h3>")
                   .append("<ul>")
                   .append("<li><b>Total:</b> ").append(total).append("</li>")
                   .append("<li style='color:green;'><b>Passed:</b> ").append(passed).append("</li>")
                   .append("<li style='color:red;'><b>Failed:</b> ").append(failed).append("</li>")
                   .append("<li style='color:orange;'><b>Skipped:</b> ").append(skipped).append("</li>")
                   .append("</ul>");

            // ✅ Passed tests
            summary.append("<h4 style='color:green;'>✅ Passed Tests</h4><ul>");
            for (ITestResult result : context.getPassedTests().getAllResults()) {
                summary.append(formatTestDescription(result));
            }
            summary.append("</ul>");

            // ❌ Failed tests
            summary.append("<h4 style='color:red;'>❌ Failed Tests</h4><ul>");
            for (ITestResult result : context.getFailedTests().getAllResults()) {
                summary.append(formatTestDescription(result));
            }
            summary.append("</ul>");

            // ⚠️ Skipped tests
            summary.append("<h4 style='color:orange;'>⚠️ Skipped Tests</h4><ul>");
            for (ITestResult result : context.getSkippedTests().getAllResults()) {
                summary.append(formatTestDescription(result));
            }
            summary.append("</ul>");
        }

        return summary.toString();
    }

    // =================== 🧠 FORMAT TEST DESCRIPTION (UPDATED) ===================
    private String formatTestDescription(ITestResult result) {
        String description = result.getMethod().getDescription();

        // ✅ Only include test description (ignore method name entirely)
        if (description != null && !description.isEmpty()) {
            return "<li>" + description + "</li>";
        } else {
            // If description missing, skip listing entirely
            return "";
        }
    }

    // =================== 📎 ATTACH SCREENSHOTS ===================
    private String attachScreenshots(Multipart multipart) {
        StringBuilder sb = new StringBuilder();
        File folder = new File(SCREENSHOT_DIR);

        // Only pick screenshots with _FAILED_ in the name
        File[] screenshots = folder.listFiles((dir, name) -> name.contains("_FAILED_") && (name.endsWith(".png") || name.endsWith(".jpg")));

        if (screenshots != null && screenshots.length > 0) {
            int counter = 1;
            for (File screenshot : screenshots) {
                String cid = "img" + counter;
                sb.append("<p><b>").append(screenshot.getName()).append("</b><br>")
                  .append("<img src='cid:").append(cid)
                  .append("' width='600' style='border:1px solid #ccc;border-radius:8px;'/></p>");

                try {
                    MimeBodyPart imagePart = new MimeBodyPart();
                    DataSource fds = new FileDataSource(screenshot);
                    imagePart.setDataHandler(new DataHandler(fds));
                    imagePart.setHeader("Content-ID", "<" + cid + ">");
                    imagePart.setDisposition(MimeBodyPart.INLINE);
                    multipart.addBodyPart(imagePart);
                } catch (Exception e) {
                    System.err.println("⚠️ Error attaching screenshot: " + e.getMessage());
                }
                counter++;
            }
        } else {
            sb.append("<p><i>No failed test screenshots available.</i></p>");
        }

        return sb.toString();
    }

    // =================== 📁 ATTACH FILES IF EXISTS ===================
    private void attachIfExists(Multipart multipart, String filePath) {
        if (filePath == null) return;
        File file = new File(filePath);
        if (file.exists()) {
            try {
                MimeBodyPart attachment = new MimeBodyPart();
                attachment.setDataHandler(new DataHandler(new FileDataSource(file)));
                attachment.setFileName(file.getName());
                multipart.addBodyPart(attachment);
                System.out.println("📎 Attached: " + file.getName());
            } catch (Exception e) {
                System.err.println("⚠️ Failed to attach " + file.getName() + ": " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ Attachment not found: " + filePath);
        }
    }
}