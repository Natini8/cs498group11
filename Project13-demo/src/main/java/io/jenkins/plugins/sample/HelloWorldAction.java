package io.jenkins.plugins.sample;

import hudson.model.Action;
import hudson.model.Run;
import jenkins.model.RunAction2;

public class HelloWorldAction implements RunAction2 {

    private String output;
    private transient Run run;

    public HelloWorldAction(String output) {
        this.output = output;
    }

    public String getOutput() {
        return output;
    }

    @Override
    public String getIconFileName() {
        return "document.png";
    }

    @Override
    public String getDisplayName() {
        return "Refined Logger";
    }

    @Override
    public String getUrlName() {
        return "refined_logger";
    }

    @Override
    public void onAttached(Run<?, ?> run) {
        this.run = run;
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        this.run = run;
    }

    public Run getRun() {
        return run;
    }
}