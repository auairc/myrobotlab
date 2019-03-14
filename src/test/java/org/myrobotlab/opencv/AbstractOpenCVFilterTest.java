package org.myrobotlab.opencv;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;

import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.myrobotlab.service.OpenCV;
import org.myrobotlab.service.Runtime;
import org.myrobotlab.test.AbstractTest;

// ignore the abstract classes.
@Ignore
public abstract class AbstractOpenCVFilterTest extends AbstractTest {

  public boolean debug = false;
  public abstract OpenCVFilter createFilter();
  public abstract IplImage createTestImage();

  private CanvasFrame sourceImage = null;
  private CanvasFrame outputImage = null;

  @Test
  public void testFilter() throws InterruptedException {


    OpenCVFilter filter = createFilter();
    IplImage input = createTestImage();

    if (debug) {
      sourceImage = filter.show(input, "Input Image");
    }

    OpenCV opencv = new OpenCV("opencv");
    filter.setOpenCV(opencv);

    // we need to set the CV Data object on the filter before we process. 
    // This calls imageChanged .. (some filters initialize their stuff in that method!
    filter.setData(new OpenCVData("testimg", 0 ,0 , OpenCV.toFrame(input)));

    // call process on the filter with the input image.
    long start = System.currentTimeMillis();
    IplImage output = filter.process(input);
    if (debug) {
      long delta = System.currentTimeMillis() - start;
      log.info("Process method took {} ms", delta);  
      filter.enabled = true;
      filter.displayEnabled = true;
      BufferedImage bi = filter.processDisplay();
      IplImage displayVal = OpenCV.toImage(bi);
      outputImage = filter.show(displayVal, "Output Image");
    }

    // TODO: we want to verify the resulting opencv data? and methods that are invoked ?
    verify(filter,input,output);

    filter.release();
    // TODO: release the filter?
    //Runtime.releaseService("opencv");
    Runtime.release("opencv");
    // other stuff that comes along with runtime to shutdown.
    Runtime.release("security");
    //Runtime.releaseAll();

    // clean up the other runtime stuffs
  }

  public IplImage defaultImage() {
    // a default image to use 
    IplImage lena = cvLoadImage("src/test/resources/OpenCV/rachel.jpg");
    return lena;
  }


  public abstract void verify(OpenCVFilter filter, IplImage input, IplImage output);

  // a helper function that can pause the VM until you press any key in the console.  (nice for debugging sometimes.)
  public void waitOnAnyKey() {
    // show the images side by side.. not sure what we should verify?  how do we get the opencv data?
    System.out.println("Press the any key...");
    try {
      System.in.read();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  // TODO: i'm not sure why i needed this?  i think the idea is we want to clean up the debug windows when done.
  // @After
  public void cleanup() {
    //
    if (debug) {
      // clean up debug images

      if (sourceImage != null) {
        sourceImage.paint(null);
      }
      if (outputImage != null) {
        outputImage.paint(null);
      }

    }
  }



}
