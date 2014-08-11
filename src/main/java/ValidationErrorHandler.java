
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Based on the article here:
 * http://pragmaticintegrator.wordpress.com/2013/11/10/validating-xml-message-in
 * -mule-3/
 * 
 * Gathering all errors and logging
 * 
 * @author villu
 * 
 */
public class ValidationErrorHandler implements ErrorHandler {

	static Logger logger = Logger.getLogger(ValidationErrorHandler.class);

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	private List<String> errors = new ArrayList<String>();

	@Override
	public void warning(SAXParseException e) throws SAXException {
		logger.warn(e.getLineNumber() + "/" + e.getColumnNumber() + ": "
				+ e.getMessage());
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		logger.info("fatalError occurred: " + e.toString());
		errors.add(e.getLineNumber() + "/" + e.getColumnNumber() + ": "
				+ e.getMessage());
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		logger.info("error occurred: " + e.toString());
		errors.add(e.getLineNumber() + "/" + e.getColumnNumber() + ": "
				+ e.getMessage());
	}
}
