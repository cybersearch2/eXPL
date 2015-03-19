// Code generated by dagger-compiler.  Do not edit.
package au.com.cybersearch2.classy_logic.tutorial15;

import dagger.internal.ModuleAdapter;

/**
 * A manager of modules and provides adapters allowing for proper linking and
 * instance provision of types served by {@code @Provides} methods.
 */
public final class AgriModule$$ModuleAdapter extends ModuleAdapter<AgriModule> {
  private static final String[] INJECTS = { "members/au.com.cybersearch2.classy_logic.tutorial15.IncreasedAgriculture", "members/au.com.cybersearch2.classy_logic.tutorial15.AgriPercentCollector", "members/au.com.cybersearch2.classy_logic.tutorial15.Agri10YearCollector", "members/au.com.cybersearch2.classy_logic.tutorial15.PersistenceAgriculture", };
  private static final Class<?>[] STATIC_INJECTIONS = { };
  private static final Class<?>[] INCLUDES = { au.com.cybersearch2.classy_logic.TestModule.class, };

  public AgriModule$$ModuleAdapter() {
    super(au.com.cybersearch2.classy_logic.tutorial15.AgriModule.class, INJECTS, STATIC_INJECTIONS, false /*overrides*/, INCLUDES, true /*complete*/, false /*library*/);
  }

  @Override
  public AgriModule newModule() {
    return new au.com.cybersearch2.classy_logic.tutorial15.AgriModule();
  }
}
