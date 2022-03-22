package org.myrobotlab.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.myrobotlab.codec.CodecUtils;
// import org.myrobotlab.codec.CodecUtils;
import org.myrobotlab.framework.Registration;
import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.repo.Repo;
import org.myrobotlab.io.FileIO;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.service.config.WebGuiConfig;
import org.slf4j.Logger;

public class Intro extends Service {

  private static final long serialVersionUID = 1L;

  public final static Logger log = LoggerFactory.getLogger(Intro.class);

  protected Map<String, Object> props = new TreeMap<>();

  public Intro(String n, String id) {
    super(n, id);
    subscribe("runtime", "registered", getName(), "registered");
    subscribe("runtime", "released", getName(), "released");
  }

  public String registered(Registration registration) {
    String name = registration.getName();
    set(name + "IsActive", true);
    return name;
  }

  public String released(String fullName) {
    String name = CodecUtils.shortName(fullName);
    set(name + "IsActive", false);
    return name;
  }

  public boolean checkInstalled(String serviceType) {
    Runtime runtime = Runtime.getInstance();
    Repo repo = runtime.getRepo();
    return repo.isInstalled(serviceType);
  }

  public Object set(String key, Object value) {
    Object ret = props.put(key, value);
    broadcastState();
    return ret;
  }

  public Object get(String key) {
    Object ret = props.get(key);
    broadcastState();
    return ret;
  }

  /**
   * @param introScriptName
   *          execute an Intro resource script
   */
  public void execScript(String introScriptName) {
    try {
      Python p = (Python) Runtime.start("python", "Python");
      String script = getResourceAsString(introScriptName);
      p.exec(script, true);
    } catch (Exception e) {
      error("unable to execute script %s", introScriptName);
    }
  }

  /**
   * This method will load a python file into the python interpreter.
   * 
   * @param file
   *          the python file to load
   * @return true/false
   */
  @Deprecated
  public boolean loadFile(String file) {
    File f = new File(file);
    Python p = (Python) Runtime.getService("python");
    log.info("Loading  Python file {}", f.getAbsolutePath());
    if (p == null) {
      log.error("Python instance not found");
      return false;
    }
    String script = null;
    try {
      script = FileIO.toString(f.getAbsolutePath());
    } catch (IOException e) {
      log.error("IO Error loading file : ", e);
      return false;
    }
    // evaluate the scripts in a blocking way.
    boolean result = p.exec(script, true);
    if (!result) {
      log.error("Error while loading file {}", f.getAbsolutePath());
      return false;
    } else {
      log.debug("Successfully loaded {}", f.getAbsolutePath());
    }
    return true;
  }

  public static void main(String[] args) {
    try {

      // for mary tts on java11...
      System.setProperty("java.version", "11.0");
      LoggingFactory.init("info");

      // Runtime.main(new String[]{"--config", "i01-9"});
      // Runtime.start("runtime", "Runtime"); i

      WebGuiConfig config = (WebGuiConfig)Runtime.load("webgui","WebGui");
      config.autoStartBrowser = false;
      Runtime.start("webgui", "WebGui");
      
      Runtime.load("i01","InMoov2");

      // Runtime.start("intro", "Intro");

//      Runtime.loadConfigSet("track-inmoov");
//      JUNIT - Runtime.load()      
//      Runtime.start();
      
      // JUNIT - load a full set - verify it was all loaded
      // JUNIT - verify a re-load - that it replaces - since filesystem is the highest priority
      
      // Runtime.load("track", "Tracking");
      // Runtime.start("track", "Tracking");
      
      // JUNIT - make sure a config set with peers can be saved and re-loaded

      // Runtime.load("i01", "InMoov2");
      
      // Runtime.start("i01.rightArm.bicep");
      
      // JUNIT verify priority yml over default
      
      Runtime runtime = Runtime.getInstance();
      log.info("service peers  count {}", runtime.getPlan().getPeers().size());
      log.info("service config count {}", runtime.getPlan().getConfig().size());
      
      // JUNIT - this causes problem ... as expected - it doesn't set the i01.head reference
      // because "of course" - i01 is not here
      // Runtime.start("i01.head");
//      InMoov2 i01 = (InMoov2)Runtime.start("i01");
//      i01.startSimulator();
//      i01.startServos();
      // i01.startAll();
      
      // JUNIT Test - test yml precedence over current plan
      
      // getActive()
      // getStarted()
      
      // Runtime.start("i01", "InMoov2");
      
      // Runtime.load("mega", "Arduino");
      // Runtime.start("mega", "Arduino");

      
//      Runtime.start("intro", "Intro");
//      
//      Runtime.setConfig("_test-01");
//      Runtime.load("runtime");
      // Runtime.load("mega");
      // Runtime.start("python", "Python");
      // Runtime.start("mega", "Arduino");
      // Runtime.start("ada", "Adafruit16CServoDriver");
      
      // Runtime.start("i01", "Tracking");
      
      boolean done = true;
      if (done) {
        return;
      }

      
      Runtime.start("i01", "InMoov2");


      DiscordBot bot = (DiscordBot) Runtime.start("bot", "DiscordBot");
      ProgramAB brain = (ProgramAB) Runtime.start("brain", "ProgramAB");
      brain.setCurrentBotName("Alice");
      bot.connect();
      brain.attach(bot);
      bot.attach(brain);

      // Runtime.main(new String[] { "--from-launcher" });// FIXME - get rid of
      // this !
      // similar to a peer reserve - ie - specifying type - now autoload
      // ProgramAB brain = (ProgramAB)Runtime.start("brain");
      Runtime.start("brain", "ProgramAB");
      // Arduino arduino = (Arduino)Runtime.start("arduino", "Arduino");
      Runtime.create("webgui", "WebGui");
      Runtime.setConfig("InMoov2_FingerStarter");

//      Runtime.create("i01.chatBot");
//      Runtime.load("i01.chatBot");
//      Runtime.start("i01.chatBot");
//
//      Runtime.start("intro", "Intro");
//      Runtime.start("python", "Python");

    } catch (Exception e) {
      log.error("main threw", e);
    }
  }

}
