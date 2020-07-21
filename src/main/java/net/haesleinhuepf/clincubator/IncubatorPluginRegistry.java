package net.haesleinhuepf.clincubator;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clincubator.utilities.IncubatorPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

class IncubatorPluginRegistry {
    Timer heartbeat = null;
    private IncubatorPluginRegistry() { }

    // singleton
    private static IncubatorPluginRegistry instance = null;
    public static IncubatorPluginRegistry getInstance() {
        if (instance == null) {
            instance = new IncubatorPluginRegistry();
        }
        return instance;
    }

    // register and unregister
    ArrayList<IncubatorPlugin> registeredPlugins = new ArrayList<>();
    public void register(IncubatorPlugin plugin) {
        if (registeredPlugins.size() == 0) {
            int delay = 100;
            heartbeat = new Timer();
            heartbeat.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    regenerate();
                }
            }, delay, delay);
        }

        if (!registeredPlugins.contains(plugin)) {
            registeredPlugins.add(plugin);
        }
    }
    public void unregister(IncubatorPlugin plugin) {
        registeredPlugins.remove(plugin);
        if (registeredPlugins.size() == 0) {
            heartbeat.cancel();
            heartbeat = null;
        }
    }

    // execute actions
    public void invalidate(ImagePlus imp) {
        if (imp == null) {
            return;
        }
        IJ.log("Invalidate " + imp.getTitle());
        if (imp.getStack() instanceof  CLIJxVirtualStack) {
            ((CLIJxVirtualStack) imp.getStack()).getBuffer().setName("");
        }
        // search for plugins which have it as source and invalidate their targets
        for (IncubatorPlugin plugin : registeredPlugins) {
            if (plugin.getSource() == imp) {
                plugin.setTargetInvalid();
            }
        }
    }

    boolean regenerating = false;
    private void regenerate() {
        if (regenerating) {
            return;
        }

        regenerating = true;

        //System.out.println("Regen");

        boolean found_something_to_regenerate = true;
        while(found_something_to_regenerate) {
            found_something_to_regenerate = false;

            for (IncubatorPlugin plugin : registeredPlugins) {
                ImagePlus source = plugin.getSource();
                ImagePlus target = plugin.getTarget();
                if (source != null && target != null && isValid(source) && !isValid(target)) {
                    IJ.log("Regenerating " + target.getTitle());

                    plugin.setTargetIsProcessing();
                    plugin.refresh();
                    found_something_to_regenerate = true;

                    if (isValid(target)) {
                        plugin.setTargetValid();
                    } else {
                        plugin.setTargetInvalid();
                    }

                    break;
                }
            }
        }

        regenerating = false;
    }

    private boolean isValid(ImagePlus imp) {
        if (imp.getStack() instanceof CLIJxVirtualStack) {
            return ((CLIJxVirtualStack) imp.getStack()).getBuffer().getName().length() != 0;
        }
        return true;
    }


}
