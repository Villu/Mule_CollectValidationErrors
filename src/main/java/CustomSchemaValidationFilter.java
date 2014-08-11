import java.util.List;

import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.module.xml.filters.SchemaValidationFilter;
import org.xml.sax.SAXException;

/**
 * Based on the article here:
 * http://pragmaticintegrator.wordpress.com/2013/11/10/validating-xml-message-in
 * -mule-3/
 * 
 * @author villu
 * 
 */
public class CustomSchemaValidationFilter extends SchemaValidationFilter {

	public final static String SAX_ERROR_PROPERTIES = "SAX_ERROR_PROPERTIES";

	public CustomSchemaValidationFilter() throws InitialisationException {
		super();
	}

	/**
	 * Accepts the message if schema validation passes.
	 * 
	 * @param message
	 *            The message.
	 * @return Whether the message passes schema validation.
	 */
	public boolean accept(MuleMessage message) {
		boolean result = super.accept(message);

		if (getErrorHandler() == null
				|| getErrorHandler() instanceof ValidationErrorHandler) {
			List<String> errors = ((ValidationErrorHandler) getErrorHandler())
					.getErrors();
			message.setOutboundProperty(SAX_ERROR_PROPERTIES,
					StringUtils.join(errors, "\n"));
			result = (errors == null || errors.size() == 0);
		} else {
			if (!result) {
				// This can happen if the filter fails before the Validator is
				// created for instance because the
				// input xml was empty or the supplied XSD doesn't exists
				message.setOutboundProperty(
						SAX_ERROR_PROPERTIES,
						"Error initializing the filter probably because of empty XML doc or non-existing XSD (see logging for detail).");
			}
		}
		return result;
	}

	public Validator createValidator() throws SAXException {
		Validator validator = super.createValidator();
		validator.setErrorHandler(getErrorHandler());
		return validator;
	}

}