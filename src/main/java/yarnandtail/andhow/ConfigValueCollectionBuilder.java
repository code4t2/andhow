package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public interface ConfigValueCollectionBuilder extends ConfigValueCollection {
	void add(Loader loader, ConfigPointValue value);
}