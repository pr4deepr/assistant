package net.haesleinhuepf.clijx.incubator.interactive.generated;

import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.interactive.suggestions.RotateCounterClockwiseSuggestion;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class RotateCounterClockwise extends AbstractIncubatorPlugin implements RotateCounterClockwiseSuggestion {

    public RotateCounterClockwise() {
        super(new net.haesleinhuepf.clij2.plugins.RotateCounterClockwise());
    }

}