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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Reader;
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
        }

        // First search in buffer
        String bufferStr = buffer.toString();
        run.addAction(new HelloWorldAction(name));

        if (useShortened) {
            listener.getLogger().println("Short " + name + "!");
        } else {
            listener.getLogger().println("Hello, " + name + "!");
        }

        stopTime = System.currentTimeMillis();
        elapsedTimeInSeconds = (stopTime -startTime) / 1000.0;
        listener.getLogger().println("This plugin completed in " + elapsedTimeInSeconds + " seconds");
        listener.getLogger().println(bufferStr);
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
