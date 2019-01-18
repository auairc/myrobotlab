package org.myrobotlab.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.myrobotlab.kinematics.Point;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.LoggingFactory;
import org.slf4j.Logger;

public class InverseKinematics3DTest {

  public final static Logger log = LoggerFactory.getLogger(InverseKinematics3DTest.class);

  @Before
  public void setUp() {
    LoggingFactory.init("WARN");
  }

  @Test
  public void testForwardKinematics() {
    InverseKinematics3D ik3d = (InverseKinematics3D) Runtime.start("ik3d", "InverseKinematics3D");
    // InMoovArm ia = new InMoovArm("i01");
    ik3d.setCurrentArm(InMoovArm.getDHRobotArm());
    ik3d.centerAllJoints();
    System.out.println(ik3d.getCurrentArm().getPalmPosition());
  }

  @Test
  public void testIK3D() throws Exception {
    InverseKinematics3D ik3d = (InverseKinematics3D) Runtime.start("ik3d", "InverseKinematics3D");
    // InMoovArm ia = new InMoovArm("i01");
    ik3d.setCurrentArm(InMoovArm.getDHRobotArm());
    // start from a centered joint configuration so we can iterate without
    // loosing rank
    // in our jacobian!
    ik3d.centerAllJoints();
    ik3d.moveTo(100.0, 0.0, 50.0);
    Point p = ik3d.currentPosition();
    double[][] positions = ik3d.createJointPositionMap();
    int x = positions[0].length;
    int y = positions.length;
    for (int j = 0; j < y; j++) {
      for (int i = 0; i < x; i++) {
        log.info(positions[j][i] + " ");
      }

    }
    // Last point:
    log.warn("Last Point: " + p.toString());
    // TODO: this doesn't actually assert the position was reached! ouch.
    Assert.assertNotNull(p);
  }

}
