package contact.phone.nabi.PdfGenerator;

import java.io.ByteArrayOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class PdfGenerator {

	  public static byte[] generateLoginPdf(String userName, String ip, String location, boolean isSuccess) throws Exception {
	        Document document = new Document();
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, out);
	        document.open();

	        document.add(new Paragraph("Login " + (isSuccess ? "Success" : "Failed")));
	        document.add(new Paragraph("User: " + userName));
	        document.add(new Paragraph("IP Address: " + ip));
	        document.add(new Paragraph("Location: " + location));
	        document.add(new Paragraph("Time: " + java.time.LocalDateTime.now()));
	        document.add(new Paragraph("Note: Do not share your OTP with anyone."));

	        document.close();
	        return out.toByteArray();
	    }

}
