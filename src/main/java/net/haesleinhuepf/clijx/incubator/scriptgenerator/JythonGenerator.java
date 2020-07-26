package net.haesleinhuepf.clijx.incubator.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;

public class JythonGenerator implements ScriptGenerator {

    @Override
    public String push(ImagePlus source) {
        String image1 = makeImageID(source);

        return ""+
                "# Push " + source.getTitle() + " to GPU memory\n" +
                image1 + " = clijx.push(WindowManager.getImage(\"" + source.getTitle() + "\"))\n";
    }

    @Override
    public String comment(String name) {
        return "# " + name + "\n";
    }

    @Override
    public String execute(IncubatorPlugin plugin) {

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        if (clijMacroPlugin == null) {
            return "# " + IncubatorUtilities.niceName(plugin.getClass().getSimpleName());
        }
        String methodName = clijMacroPlugin.getClass().getSimpleName();
        methodName = methodName.substring(0,1).toLowerCase() + methodName.substring(1);
        String pakage = clijMacroPlugin.getClass().getPackage().getName();

        methodName = "clijx." + pythonize(methodName);


        String image1 = makeImageID(plugin.getSource());
        String image2 = makeImageID(plugin.getTarget());
        String program = "# " + IncubatorUtilities.niceName(plugin.getClass().getSimpleName()) + "\n";

        ImagePlus imp = plugin.getTarget();
        if (imp.getNSlices() > 1) {
            program = program +
                image2 + " = clijx.create([" + imp.getWidth() + ", " + imp.getHeight() + ", "  + imp.getNSlices() + "], clijx." + bitDepthToType(imp.getBitDepth()) + ")\n";
        } else {
            program = program +
                    image2 + " = clijx.create([" + imp.getWidth() + ", " + imp.getHeight() + "], clijx." + bitDepthToType(imp.getBitDepth()) + ")\n";
        }
        String call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 2; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            call = call + ", " + name + "=" + name;
            program = program + name + " = " + plugin.getArgs()[i] + "  \n";
        }
        program = program + methodName + "(" + image1 + ", " + image2 + call + ")\n";

        return program;
    }

    private String bitDepthToType(int bitDepth) {
        if (bitDepth == 8) {
            return "UnsignedByte";
        } else if (bitDepth == 16) {
            return "UnsignedShort";
        } else
            return "Float";
    }

    private String pythonize(String methodName) {
        return methodName; // IncubatorUtilities.niceName(methodName).trim().replace(" ", "_").toLowerCase();
    }

    @Override
    public String fileEnding() {
        return ".py";
    }

    @Override
    public String header() {
        return  "# To make this script run in Fiji, please activate \n" +
                "# the clij and clij2 update sites in your Fiji \n" +
                "# installation. Read more: https://clij.github.io\n\n" +
                "\n\n" +
                "from ij import IJ\n" +
                "from ij import WindowManager\n" +
                "from net.haesleinhuepf.clijx import CLIJx\n\n" +
                "# Init GPU\n" +
                "clijx = CLIJx.getInstance()\n";
    }
}