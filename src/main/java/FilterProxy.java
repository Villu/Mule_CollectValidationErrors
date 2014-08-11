import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import exceptions.FilterUnacceptedException;

/**
 * As the validation filter has to be of scope "prototype" and there is no way
 * of doing this with standard Mule configuration, we set up a proxy that looks
 * up a spring bean that is the SchemaValidationFilter and is set up as a
 * prototype bean. Thanks to Pontus Ullgren for the tip.
 * 
 * @author villu
 * 
 */
public class FilterProxy implements Filter, ApplicationContextAware {
	private ApplicationContext context;
	private String realFilterId;
	private boolean enabled;

	@Override
	public boolean accept(MuleMessage message) {
		if (!enabled)
			return true;
		Filter filter = (Filter) context.getBean(realFilterId, Filter.class);
		boolean result = filter.accept(message);
		if (!result) {
			throw new FilterUnacceptedException();
		}
		return result;
	}

	public void setRealFilterId(String realFilterId) {
		this.realFilterId = realFilterId;
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;

	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}