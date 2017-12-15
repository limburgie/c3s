package be.webfactor.c3s.master.templateparser.freemarker;

import java.util.List;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class ObjectConstructor implements TemplateMethodModelEx {

	public Object exec(List arguments) throws TemplateModelException {
		try {
			String className = String.valueOf(arguments.get(0));
			List constructorParams = arguments.subList(1, arguments.size());

			BeansWrapper beansWrapper = new BeansWrapperBuilder(Configuration.getVersion()).build();

			Object object = beansWrapper.newInstance(Class.forName(className), constructorParams);

			return beansWrapper.wrap(object);
		} catch (Exception e) {
			throw new TemplateModelException(e);
		}
	}
}