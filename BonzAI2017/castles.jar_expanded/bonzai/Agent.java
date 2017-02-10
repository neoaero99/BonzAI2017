package bonzai;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation specifying that the given class is an agent to be read by the
 * competition software. An agent must specify a name.
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Agent {
	/**
	 * @return the specified agent name
	 **/
	public String name();
}

// Functional Interface ???