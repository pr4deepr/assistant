package net.haesleinhuepf.clijx.incubator;

import net.haesleinhuepf.clijx.incubator.services.UsageAnalyser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StoreUsageAnalysis {
    public static void main(String[] args) throws IOException {
        UsageAnalyser combinedUsageStats = new UsageAnalyser(
                "../clij2-docs/src/main/macro/",
                "../clijx/src/main/macro/",
                "../scripts_hidden/",
                "../scripts/");

        String output = combinedUsageStats.all();

        File outputTarget = new File("src/main/resources/suggestions.config");
        try {
            FileWriter writer = new FileWriter(outputTarget);
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}