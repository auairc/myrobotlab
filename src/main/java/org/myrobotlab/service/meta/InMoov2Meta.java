package org.myrobotlab.service.meta;
import org.myrobotlab.framework.Plan;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.service.Pid.PidData;
import org.myrobotlab.service.config.InMoov2Config;
import org.myrobotlab.service.config.PidConfig;
import org.myrobotlab.service.config.RuntimeConfig;
import org.myrobotlab.service.config.TrackingConfig;
import org.myrobotlab.service.meta.abstracts.MetaData;
import org.slf4j.Logger;

public class InMoov2Meta extends MetaData {
  private static final long serialVersionUID = 1L;
  public final static Logger log = LoggerFactory.getLogger(InMoov2Meta.class);

  /**
   * This class is contains all the meta data details of a service. It's peers,
   * dependencies, and all other meta data related to the service.
   */
  public InMoov2Meta() {

    addDescription("InMoov2 Service");
    addCategory("robot");

    // skeletal parts
    addPeer("head", "InMoov2Head");
    addPeer("torso", "InMoov2Torso");
    // addPeer("eyelids", "InMoovEyelids", "eyelids");
    addPeer("leftArm", "InMoov2Arm");
    addPeer("leftHand", "InMoov2Hand");
    addPeer("rightArm", "InMoov2Arm");
    addPeer("rightHand", "InMoov2Hand");

    addPeer("leap", "LeapMotion");

    addPeer("opencv", "OpenCV");

    addPeer("left", "Arduino");
    addPeer("right", "Arduino");

    addPeer("mouthControl", "MouthControl");
    addPeer("ultrasonicRight", "UltrasonicSensor");
    addPeer("ultrasonicLeft", "UltrasonicSensor");
    addPeer("pir", "Pir");
    addPeer("pid", "Pid");

    addPeer("servoMixer", "ServoMixer");

    // the two legacy controllers .. :(
    addPeer("left", "Arduino");
    addPeer("right", "Arduino");
    addPeer("controller3", "Arduino");
    addPeer("controller4", "Arduino");

    addPeer("htmlFilter", "HtmlFilter");
    addPeer("chatBot", "ProgramAB");

    // Sensors -----------------
    addPeer("opencv", "OpenCV", "opencv");
    addPeer("ultrasonicRight", "UltrasonicSensor", "measure distance on the right");
    addPeer("ultrasonicLeft", "UltrasonicSensor", "measure distance on the left");
    addPeer("pir", "Pir", "infrared sensor");

    addPeer("mouth", "MarySpeech");
    addPeer("ear", "WebkitSpeechRecognition");

    addPeer("imageDisplay", "ImageDisplay");

    addPeer("headTracking", "Tracking");
    addPeer("eyeTracking", "Tracking");

    addPeer("neopixel", "NeoPixel");

    addPeer("audioPlayer", "AudioFile");
    addPeer("random", "Random");
    addPeer("simulator", "JMonkeyEngine");

    addDependency("fr.inmoov", "inmoov2", null, "zip");

    // the two legacy controllers .. :(
    addPeer("left", "Arduino");
    addPeer("right", "Arduino");
    addPeer("controller3", "Arduino");
    addPeer("controller4", "Arduino");
  }

  @Override
  public Plan getDefault(String name) {

    InMoov2Config inmoov = new InMoov2Config();
    
    // InMoov is a controller service and wants to 
    // starte / release its own peers - usually the
    // framework would auto start and auto release all peers
    inmoov.autoStartPeers = false;
    
    Plan plan = new Plan(name);
    // load default peers from meta here
    plan.putPeers(name, peers);


    // == Peer - head ==========================================
    inmoov.head = name + ".head";
    inmoov.torso = name + ".torso";
    inmoov.leftArm = name + ".leftArm";
    inmoov.rightArm = name + ".rightArm";
    inmoov.leftHand = name + ".leftHand";
    inmoov.rightHand = name + ".rightHand";
    inmoov.opencv = name + ".opencv";
    inmoov.headTracking = name + ".headTracking";
    inmoov.eyeTracking = name + ".eyeTracking";
    inmoov.mouth = name + ".mouth";
    inmoov.audioPlayer = name + ".audioPlayer";
    inmoov.mouthControl = name + ".mouthControl";
    
    
    
//    addPeerConfig(name, "head");
//    addPeerConfig(name, "torso");
//    addPeerConfig(name, "leftArm");
//    addPeerConfig(name, "rightArm");
//    addPeerConfig(name, "leftHand");
//    addPeerConfig(name, "rightHand");
//    addPeerConfig(name, "opencv");
//
//    addPeerConfig(name, "left");
//    addPeerConfig(name, "right");

    
    // == Peer - headTracking =============================
    TrackingConfig headTracking = (TrackingConfig) plan.getPeerConfig("headTracking");

    // setup name references to different services
    headTracking.tilt = name + ".head.neck";
    headTracking.pan = name + ".head.rothead";
    headTracking.cv = name + ".opencv";
    headTracking.pid = name + ".pid";
    headTracking.controller = name + ".left";

    // == Peer - eyeTracking =============================
    TrackingConfig eyeTracking = (TrackingConfig) plan.getPeerConfig("eyeTracking");

    // setup name references to different services
    eyeTracking.tilt = name + ".head.eyeY";
    eyeTracking.pan = name + ".head.eyeX";
    eyeTracking.cv = name + ".opencv";
    eyeTracking.pid = name + ".pid";
    eyeTracking.controller = name + ".left";

    // == Peer - pid =============================
    inmoov.pid = name + ".pid";
    PidConfig pid = (PidConfig) plan.getPeerConfig("pid");

    PidData tiltPid = new PidData();
    tiltPid.ki = 0.001;
    tiltPid.kp = 0.035;
    pid.data.put(headTracking.tilt, tiltPid);

    PidData panPid = new PidData();
    panPid.ki = 0.001;
    panPid.kp = 0.015;
    pid.data.put(headTracking.pan, panPid);

    PidData eyeTiltPid = new PidData();
    eyeTiltPid.ki = 0.001;
    eyeTiltPid.kp = 0.035;
    pid.data.put(eyeTracking.tilt, eyeTiltPid);

    PidData eyePanPid = new PidData();
    eyePanPid.ki = 0.001;
    eyePanPid.kp = 0.015;
    pid.data.put(eyeTracking.pan, eyePanPid);

    // remove undesired defaults from our default
    plan.removeConfig(name + ".headTracking.tilt");
    plan.removeConfig(name + ".headTracking.pan");
    plan.removeConfig(name + ".headTracking.pid");
    plan.removeConfig(name + ".headTracking.controller");
    plan.removeConfig(name + ".headTracking.controller.serial");
    plan.removeConfig(name + ".headTracking.cv");

    plan.removeConfig(name + ".eyeTracking.tilt");
    plan.removeConfig(name + ".eyeTracking.pan");
    plan.removeConfig(name + ".eyeTracking.pid");
    plan.removeConfig(name + ".eyeTracking.controller");
    plan.removeConfig(name + ".eyeTracking.controller.serial");
    plan.removeConfig(name + ".eyeTracking.cv");

    plan.addConfig(inmoov);
    
    RuntimeConfig runtime = new RuntimeConfig();
    runtime.registry = new String[]{name};
    plan.put("runtime", runtime);

    return plan;
  }


}
