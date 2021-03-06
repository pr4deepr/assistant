package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import org.scijava.util.VersionUtils;

public class MacroGenerator extends AbstractScriptGenerator {

    @Override
    public String push(ImagePlus source) {
        String output = "";

        String filename = getFilename(source);
        String imageID = makeImageID(source);

        if (filename != null && filename.length() > 0) {
            output = output + "" +
                    "// Load image from disc \n" +
                    "open(\"" + filename + "\");\n" +
                    imageID + " = getTitle();\n" +
                    "Ext.CLIJ2_push(" + imageID + ");\n";
        } else {
            output = output + "" +
                    imageID + " = \"" + source.getTitle() + "\";\n" +
                    "Ext.CLIJ2_push(" + imageID + ");\n";
        }

        return output;
    }

    @Override
    public String pull(AssistantGUIPlugin result) {
        String imageID = makeImageID(result.getTarget());
        return "Ext.CLIJ2_pull(" + imageID + ");\n" +
                (AssistantUtilities.resultIsLabelImage(result)?"run(\"glasbey_on_dark\");\n":"") +
                close(imageID) + "\n";
    }

    @Override
    public String comment(String text) {
        return "// " + text.replace("\n", "\n// ") + "\n";
    }

    @Override
    public String execute(AssistantGUIPlugin plugin) {

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        if (clijMacroPlugin == null) {
            return "// " + AssistantUtilities.niceNameWithoutDimShape(plugin.getClass().getName());
        }
        String methodName = clijMacroPlugin.getName();
        methodName = "Ext." + methodName;

        makeImageIDs(plugin);
        makeImageID(plugin.getTarget());
        String program = "\n// " + AssistantUtilities.niceNameWithoutDimShape(plugin.getName()) + "\n";

        //for (int s = 0; s < plugin.getNumberOfSources(); s++) {
        //    program = program +
        //            "// " + image1s[s] + " = \"" + plugin.getSource(s).getTitle() + "\";\n";
        //}
        //program = program +
        //        "// " + image2 + " = \"" + plugin.getTarget().getTitle() + "\";\n";

        String call = "";
        String after_call = "";
        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 0; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];

            if (i > 0) {
                call = call + ", ";
            }

            if (plugin.getArgs()[i] instanceof ClearCLBuffer ||
                    plugin.getArgs()[i] instanceof ClearCLBuffer[] ||
                    plugin.getArgs()[i] instanceof ClearCLBuffer[][] ||
                    plugin.getArgs()[i] instanceof ImagePlus
            ) {
                String image_id = objectToString(plugin.getArgs()[i]);
                if (image_id == null && i < plugin.getNumberOfSources()) {
                    image_id = objectToString(plugin.getSource(i));
                }
                call = call + image_id;
                after_call = after_call + close(image_id) + "\n";
            } else {
                call = call + name;
                program = program + name + " = " + objectToString(plugin.getArgs()[i]) + ";\n";
            }
        }
        program = program + methodName + "(" + call + ");\n" +
                after_call + "";
        //program = program + "Ext.CLIJ2_pull(" + image2 + "); // consider removing this line if you don't need to see that image\n";

        return program;
    }


    @Override
    public String fileEnding() {
        return ".ijm";
    }

    @Override
    public String header() {
        return  "// To make this script run in Fiji, please activate \n" +
                "// the clij and clij2 update sites in your Fiji \n" +
                "// installation. Read more: https://clij.github.io\n\n" +
                "// Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n\n" +
                "// Init GPU\n" +
                "run(\"CLIJ2 Macro Extensions\", \"cl_device=\");\n\n";
    }

    @Override
    public String close(String image) {
        return  "Ext.CLIJ2_release(" + image + ");";
    }

}
