package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Properties and values loaded by a Loader.
 * 
 * Only Properties for which a Loader found a value will be in the collection.
 * 
 * @author eeverman
 */
public class LoaderValues implements ValueMap {
	public static final List<PropertyValue> EMPTY_PROP_VALUE_LIST = Collections.emptyList();
	
	private final Loader loader;
	private final List<PropertyValue> values;
	private final List<LoaderProblem> problems;
	private boolean problem = false;

	public LoaderValues(Loader loader, List<PropertyValue> inValues, List<LoaderProblem> problems) {
		
		if (loader == null) {
			throw new RuntimeException("The loader cannot be null");
		}
		
		this.loader = loader;
		
		if (inValues != null && inValues.size() > 0) {
			ArrayList newValues = new ArrayList();
			newValues.addAll(inValues);
			newValues.trimToSize();
			values = Collections.unmodifiableList(newValues);
			
			
			//check for problems
			for (PropertyValue pv : values) {
				if (pv.hasIssues()) {
					problem = true;
					break;
				}
			}
			
		} else {
			values = EMPTY_PROP_VALUE_LIST;
		}
		
		this.problems = problems;
		
		if (problems != null && problems.size() > 0) {
			problem = true;
		}
	}

	public Loader getLoader() {
		return loader;
	}

	public List<PropertyValue> getValues() {
		return values;
	}
	
	

	/**
	 * A linear search for the Property in the values loaded by this loader.
	 * 
	 * @param prop
	 * @return 
	 */
	@Override
	public <T> T getExplicitValue(Property<T> prop) {
		if (prop == null) {
			return null;
		}
		return prop.getValueType().cast(values.stream().filter(pv -> prop.equals(pv.getProperty())).
						findFirst().map(pv -> pv.getValue()).orElse(null)
		);
	}
	
	@Override
	public <T> T getEffectiveValue(Property<T> prop) {
		if (prop == null) {
			return null;
		}
		
		if (isExplicitlySet(prop)) {
			return getExplicitValue(prop);
		} else {
			return prop.getDefaultValue();
		}
	}

	/**
	 * A linear search for the Property in the values loaded by this loader.
	 * @param prop
	 * @return 
	 */
	@Override
	public boolean isExplicitlySet(Property<?> prop) {
		return values.stream().anyMatch(p -> p.getProperty().equals(prop));
	}
	
	/**
	 * Returns true if any value or loader has any sort of issue (invalid value,
	 * parsing error, etc).
	 * 
	 * @return 
	 */
	public boolean hasProblems() {
		return problem;
	}

	/**
	 * Returns loader related problems, if any.
	 * @return May be null
	 */
	public List<LoaderProblem> getProblems() {
		return problems;
	}
}