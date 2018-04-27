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

    public String getLineWithNewLine(String line){

	line += "\n";
	return line;

    }

    public String parseFileShortened(String bufferStr){

	return "";

    }

    public String parseFile(String bufferStr){

        Scanner scanner = new Scanner(bufferStr);
        String output = "";
        boolean skipNext = false;
        boolean compError = false;

        while (scanner.hasNextLine()){

            String line = scanner.nextLine();

            if(!skipNext) {

                //Tests Failed perhaps [Laura]

                //Tests Skipped perhaps [Harsh]

                //Counts for each perhaps [EVERYONE]

                //Files created [Tanner]

                //If it was annouced that future lines are compilation errors
                if (compError){

                    //Append line to output
                    output += getLineWithNewLine(line);

                    //If line contains [INFO], it is the final line related to compilation errors
                    if (line.contains("[INFO]")){

                        //Remove the announcement that future lines are compilation error related
                        compError = false;

                    }

                }

                //If the line announces that future lines will contain compilation errors
                if (line.contains("COMPILATION ERROR")){

                    //Skip the next line in the build file and announce that future lines will be compilation error related
                    skipNext = true;
                    compError = true;

                    //Append line to output
                    output += getLineWithNewLine(line);

                }

            }

            //If the the current line is skipped
            else{

                //Allow future lines to be read
                skipNext = false;

            }

        }

        scanner.close();

        return output;

    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        //Start the timer for the plugin and instantiate the stop time and time passed
        long startTime = System.currentTimeMillis();
        long stopTime;
        double elapsedTimeInSeconds;

        //Initialize a string that will contain a line of the build log
        String line;

        //Grab and parse the log so that it can be read line by line
        Reader logReader = run.getLogReader();
        BufferedReader reader = new BufferedReader(logReader);
        StringBuilder buffer = new StringBuilder();

        //Appending a newline to each line of the log
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

        //Initalize a string to contain the output
	    String output;

        //Notify the user of the begenning of the plugin
        listener.getLogger().println("=================================================\n");

        //If the user selected shortened output
        if (useShortened) {

            //Let the user know that they are about to recieve shortened output and then retrieve shortened output
            listener.getLogger().println("Short " + name + "!\n");
            output = parseFileShortened(bufferStr);

        //Otherwise
        } else {

            //let the user know that they are about to recieve output and the retrieve said output
            listener.getLogger().println("Hello, " + name + "!\n");
            output = parseFile(bufferStr);

        }

        //Print the resulting output
        listener.getLogger().println(output);
        run.addAction(new HelloWorldAction(output));

        //Stop the timer and divide by 1000 to convert the time to seconds
        stopTime = System.currentTimeMillis();
        elapsedTimeInSeconds = (stopTime -startTime) / 1000.0;

        //Print the time the plugin took in seconds
        listener.getLogger().println("This plugin completed in " + elapsedTimeInSeconds + " seconds.\n");

        //Notify the user of the end of the plugin
        listener.getLogger().println("=================================================");

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
