import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

public class FunctionalTest extends FunctionalTestCase {
	private static final Logger logger = LoggerFactory
			.getLogger(FunctionalTest.class);

	private MuleClient client;

	@Test
	public void testCorrect() throws Exception {
		final String payload = getFileContent("request_correct.xml");
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("Content-Type", new String("text/xml"));

		final MuleMessage result = client.send("vm://in", payload, properties);

		final String returncode = applyXpath("//*[local-name()='element1']",
				result.getPayloadAsString());
		Assert.assertEquals("String", returncode);
	}

	@Test
	public void testFalse() throws Exception {
		final String payload = getFileContent("request_wrong.xml");
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("Content-Type", new String("text/xml"));

		final MuleMessage result = client.send("vm://in", payload, properties);

		Assert.assertEquals(
				"Request does not comply with service schema: -1/-1: cvc-datatype-valid.1.2.1: 'String' is not a valid value for 'integer'.\n-1/-1: cvc-type.3.1.3: The value 'String' of element 'element2' is not valid.\n-1/-1: cvc-complex-type.2.4.b: The content of element 'Request' is not complete. One of '{element1}' is expected.",
				result.getPayloadAsString());

	}

	@Override
	protected String getConfigResources() {

		return "collectvalidationerrors.xml";
	}

	@Before
	public void before() throws Exception {
		logger.info("Running @Before target");

		client = new MuleClient(muleContext);
	}

	public static String getFileContent(final String fileName) {
		return getFileContent(fileName, Thread.currentThread()
				.getContextClassLoader());
	}

	public static String getFileContent(final String fileName,
			final ClassLoader loader) {
		final StringBuilder builder = new StringBuilder();
		try {
			String line;
			final InputStream inputStream = loader
					.getResourceAsStream(fileName);
			Assert.assertNotNull("Unable to find resource file " + fileName,
					inputStream);
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(inputStream));
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			inputStream.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		return builder.toString();
	}

	public static String applyXpath(final String xPathString,
			final String content) {
		try {
			final XPath xPath = XPathFactory.newInstance().newXPath();
			final InputSource source = new InputSource(
					new StringReader(content));
			return xPath.evaluate(xPathString, source);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}