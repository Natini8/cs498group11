import java.util.*;
/**
 * Entry point to auto-generated tests (generated by maven-hpi-plugin).
 * If this fails to compile, you are probably using Hudson &lt; 1.327. If so, disable
 * this code generation by configuring maven-hpi-plugin to &lt;disabledTestInjection&gt;true&lt;/disabledTestInjection&gt;.
 */
public class InjectedTest extends junit.framework.TestCase {
  public static junit.framework.Test suite() throws Exception {
    System.out.println("Running tests for "+"io.jenkins.plugins:Project13-demo:1.0-SNAPSHOT");
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("basedir","/home/tlco255/CS498/final/cs498group11/Project13-demo");
    parameters.put("artifactId","Project13-demo");
    parameters.put("packaging","hpi");
    parameters.put("outputDirectory","/home/tlco255/CS498/final/cs498group11/Project13-demo/target/classes");
    parameters.put("testOutputDirectory","/home/tlco255/CS498/final/cs498group11/Project13-demo/target/test-classes");
    parameters.put("requirePI","true");
    return org.jvnet.hudson.test.PluginAutomaticTestBuilder.build(parameters);
  }
}
