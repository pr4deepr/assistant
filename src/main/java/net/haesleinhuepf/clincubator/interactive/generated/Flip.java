package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class Flip extends AbstractIncubatorPlugin implements SuggestedPlugin {

    public Flip() {
        super(new net.haesleinhuepf.clij2.plugins.Flip3D());
    }

    public Class[] suggestedNextSteps() {
        return new Class[] {
            
        };
    }

    public Class[] suggestedPreviousSteps() {
        return new Class[]{
            
        };
    }
}
