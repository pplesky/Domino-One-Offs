package mtc.ruby;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;

import org.jruby.CompatVersion;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;

public class RubyValueBinding extends ValueBinding {
	String content;
	
	public RubyValueBinding(String content) {
		this.content = content;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class getType(FacesContext arg0) throws EvaluationException, PropertyNotFoundException {
		return Object.class;
	}

	@Override
	public Object getValue(FacesContext context) throws EvaluationException, PropertyNotFoundException {
		ScriptingContainer container = new ScriptingContainer(LocalContextScope.CONCURRENT);
		//container.setCompatVersion(CompatVersion.RUBY1_9);
		
		container.put("$facesContext", context);
		// Define a generic method_missing that looks to the surrounding context for variables
		container.runScriptlet(
			"def method_missing(name, *args)\n" +
			"	if args.size == 0\n" +
			"		val = $facesContext.get_application.get_variable_resolver.resolve_variable($facesContext, name.to_s)\n" +
			"		if val != nil\n" +
			"			return val\n" +
			"		end\n" +
			"	end\n" +
			"	super\n" +
			"end"
		);
		
		Object result = container.runScriptlet(this.content);
		container.terminate();
		return result;
	}

	@Override
	public boolean isReadOnly(FacesContext arg0) throws EvaluationException, PropertyNotFoundException {
		return true;
	}

	@Override
	public void setValue(FacesContext arg0, Object arg1) throws EvaluationException, PropertyNotFoundException {
		
	}

}
