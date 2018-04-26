package io.jenkins.plugins.sample;

import hudson.Launcher;

import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import hudson.tasks.Recorder;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import javax.print.DocFlavor.READER;
import javax.servlet.ServletException;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

public class HelloWorldBuilder extends Builder implements SimpleBuildStep {
	
	private transient static final Logger LOGGER = Logger.getLogger(Recorder.class.getName());
    private final String name;
    private boolean useShortened;

    @DataBoundConstructor
    public HelloWorldBuilder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isuseShortened() {
        return useShortened;
    }

    @DataBoundSetter
    public void setuseShortened(boolean useShortened) {
        this.useShortened = useShortened;
    }

    public String parseFileShortened(String bufferStr){

        Scanner scanner = new Scanner(bufferStr);
        String output = "";
        boolean skipNext = false;
        boolean compError;

        while (scanner.hasNextLine()){

            String line = scanner.nextLine();

            if(!skipNext) {

                //Tests Passed perhaps [Tanner]

                //Tests Failed perhaps [Laura]

                //Tests Skipped perhaps [Harsh]

                //Counts for each perhaps [EVERYONE]

                //Files created [Tanner]

                //If the line announces that future lines will contain compilation errors
                if (line.contains("COMPILATION ERROR"){

                    //Skip the next line in the build and announce that future lines will be error related
                    skipNext = true;
                    compError = true;

                    output += line; //For testing to make sure it works

                }

            }

            else{

                skipNext = false;

            }

        }

        scanner.close();

        return output;

    }

    public String parseFile(String bufferStr){

        Scanner scanner = new Scanner(bufferStr);
        String output = "";
        boolean skipNext = false;
        boolean compError;

        while (scanner.hasNextLine()){

            String line = scanner.nextLine();

            if(!skipNext) {

                //Tests Passed perhaps [Tanner]

                //Tests Failed perhaps [Laura]

                //Tests Skipped perhaps [Harsh]

                //Counts for each perhaps [EVERYONE]

                //Files created [Tanner]

                //If the line announces that future lines will contain compilation errors
                if (line.contains("COMPILATION ERROR"){

                    //Skip the next line in the build and announce that future lines will be error related
                    skipNext = true;
                    compError = true;

                    output += line; //For testing to make sure it works

                }

            }

            else{

                skipNext = false;

            }

        }

        scanner.close();

        return output;

    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        long startTime = System.currentTimeMillis();
        long stopTime;
        String line;
        double elapsedTimeInSeconds;
        Reader logReader = run.getLogReader();
        BufferedReader reader = new BufferedReader(logReader);
        StringBuilder buffer = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
               
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            return;
        }

        // First search in buffer
        String bufferStr = buffer.toString();
        run.addAction(new HelloWorldAction(name));

        //Notify the user of the begenning of the plugin
        listener.getLogger().println("=================================================");
        if (useShortened) {
            listener.getLogger().println("Short " + name + "!");
            String output = parseFileShortened(bufferStr);
        } else {
            listener.getLogger().println("Hello, " + name + "!");
            String output = parseFile(bufferStr);
        }
        listener.getLogger().println("=================================================");

        listener.getLogger().println(output);

        stopTime = System.currentTimeMillis();
        elapsedTimeInSeconds = (stopTime -startTime) / 1000.0;
        listener.getLogger().println("This plugin completed in " + elapsedTimeInSeconds + " seconds");

    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckName(@QueryParameter String value, @QueryParameter boolean useShortened)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_errors_missingName());
            if (value.length() < 4)
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_tooShort());
            if (!useShortened && value.matches(".*[éáàç].*")) {
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_reallyFrench());
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.HelloWorldBuilder_DescriptorImpl_DisplayName();
        }

    }

}
