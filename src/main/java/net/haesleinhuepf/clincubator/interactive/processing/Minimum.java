package net.haesleinhuepf.clincubator.interactive.processing;

import ij.gui.GenericDialog;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Plugin(type = SuggestedPlugin.class)
public class Minimum extends AbstractIncubatorPlugin implements Denoiser{

    int former_radius = 1;
    Scrollbar radiusSlider = null;


    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Minimum filter");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addSlider("Radius", 0, 100, former_radius);

        radiusSlider = (Scrollbar) gdp.getSliders().get(0);

        return gdp;

        //radius = (int) gdp.getNextNumber();
    }

    @Override
    protected boolean parametersWereChanged() {
        return radiusSlider.getValue() != former_radius;
    }

    ClearCLBuffer result = null;
    public synchronized void refresh() {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed);
        }
        if (radiusSlider != null) {
            former_radius = radiusSlider.getValue();
        }
        clijx.minimum3DBox(pushed, result, former_radius, former_radius, former_radius);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Minimum filtered " + my_source.getTitle());
    }


}