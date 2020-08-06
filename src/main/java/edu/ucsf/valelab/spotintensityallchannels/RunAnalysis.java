 ///////////////////////////////////////////////////////////////////////////////
 //FILE:          RunAnalysis.java
 //PROJECT:       SpotIntensityAllChannels
 //-----------------------------------------------------------------------------
 //
 // AUTHOR:       Nico Stuurman
 //
 // COPYRIGHT:    University of California, San Francisco 2015
 //
 // LICENSE:      This file is distributed under the BSD license.
 //               License text is included with the source distribution.
 //
 //               This file is distributed in the hope that it will be useful,
 //               but WITHOUT ANY WARRANTY; without even the implied warranty
 //               of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 //
 //               IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 //               CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 //               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
 
package edu.ucsf.valelab.spotintensityallchannels;

 import edu.ucsf.valelab.spotintensityallchannels.algorithm.FindLocalMaxima;
 import edu.ucsf.valelab.spotintensityallchannels.algorithm.Utils;
 import edu.ucsf.valelab.spotintensityallchannels.data.SpotIntensityParameters;
 import ij.ImagePlus;
 import ij.WindowManager;
 import ij.gui.GenericDialog;
 import ij.gui.ImageCanvas;
 import ij.gui.ImageWindow;
 import ij.gui.Overlay;
 import ij.measure.ResultsTable;
 import ij.process.ImageProcessor;
 import ij.text.TextPanel;
 import ij.text.TextWindow;

 import java.awt.Color;
 import java.awt.Frame;
 import java.awt.Polygon;
 import java.awt.event.KeyListener;
 import java.awt.event.MouseListener;

 /**
 *
 * @author nico
 */
public class RunAnalysis extends Thread {
   final ImagePlus iPlus_;
   final SpotIntensityParameters parms_;
   final GenericDialog gd_;
   
   public RunAnalysis(ImagePlus iPlus,  
           SpotIntensityParameters parms, GenericDialog gd){
      iPlus_ = iPlus;
      parms_ = parms;
      gd_ = gd;
   }
   
   @Override
   public void run() {
      ImageProcessor ip = iPlus_.getProcessor();

      //QuartCircleOutlineMask qc = new QuartCircleOutlineMask(5);
      //qc.print();

      /*
      List<ImageProcessor> ipsByChannel = new ArrayList<ImageProcessor>(iPlus_.getNChannels());
      final int originalC = iPlus_.getC();
      for (int c = 0; c < iPlus_.getNChannels(); c++) {
         iPlus_.setPositionWithoutUpdate(c + 1, iPlus_.getSlice(), iPlus_.getFrame());
         ipsByChannel.add(c, iPlus_.getProcessor());
         ij.IJ.log("Channel: " + (c+1) + ": " + ipsByChannel.get(c).getStatistics().mean);
      }
      iPlus_.setPosition(originalC, iPlus_.getSlice(), iPlus_.getFrame());

       */
      final int originalC = iPlus_.getC();
      Polygon maxima = FindLocalMaxima.FindMax(ip, 
              parms_.detectionRadius_, parms_.noiseTolerance_,
              FindLocalMaxima.FilterType.NONE);
      Overlay ovl = Utils.GetSpotOverlay(maxima, parms_.detectionRadius_, Color.red);
      
      iPlus_.setOverlay(ovl);

      ResultsTable res = new ResultsTable();
      res.setPrecision(1);
      
      for (int i = 0; i < maxima.npoints; i++) {
         res.incrementCounter();
         final int x = maxima.xpoints[i];
         final int y = maxima.ypoints[i];
         res.addValue("x", x);
         res.addValue("y", y);
         for (int c = 0; c < iPlus_.getNChannels(); c++) {
            iPlus_.setPositionWithoutUpdate(c + 1, iPlus_.getSlice(), iPlus_.getFrame());
            ImageProcessor ipc = iPlus_.getProcessor();
            float intensity = Utils.GetAvgIntensity( ipc, x, y, parms_.detectionRadius_);
            res.addValue("I-ch" + (c+1), intensity);
            float background = Utils.GetBackgroundCircleRp2(ipc, x, y, parms_.backgroundRadius_);
            res.addValue("B-ch" + (c+1), background);
         }
      }
      iPlus_.setPositionWithoutUpdate(originalC, iPlus_.getSlice(), iPlus_.getFrame());
    /*
      
      ImagePlus backgroundIP = new ImagePlus("test", ip.duplicate());
      if (parms_.backgroundMethod_.equals(SpotIntensityInAllChannels.GAUSSIAN100)) {
         IJ.run(backgroundIP,"Gaussian Blur...", "sigma=100");
      } else if (parms_.backgroundMethod_.equals(SpotIntensityInAllChannels.MEDIAN40)) {
         IJ.run(backgroundIP, "Median...", "radius=40");
      }
      
      ImageCalculator iCalc = new ImageCalculator();
      for (int frame = 1; frame <= iPlus_.getNFrames(); frame++) {
         IJ.showProgress(frame, iPlus_.getNFrames());
         ImageProcessor frameProcessor = is.getProcessor(frame);
         ImagePlus sub = iCalc.run("Subtract create 32-bit",  
                 new ImagePlus("t", frameProcessor), backgroundIP );
         for (int i = 0; i < maxima.npoints; i++) {
            int x = maxima.xpoints[i];
            int y = maxima.ypoints[i];
            float intensity = Utils.GetIntensity((FloatProcessor) sub.getProcessor(), x, y, parms_.radius_);
            res.setValue("" + (frame - 1) * parms_.intervalS_, i , intensity * parms_.ePerADU_);
         }
      }

     */
       
      String name = "Spot analysis of " + iPlus_.getShortTitle();
      res.show(name);
      
      // Attach listener to TextPanel
      Frame frame = WindowManager.getFrame(name);
      if (frame instanceof TextWindow) {
         TextWindow twin = (TextWindow) frame;
         TextPanel tp = twin.getTextPanel();

         for (MouseListener ms : tp.getMouseListeners()) {
            tp.removeMouseListener(ms);
         }
         for (KeyListener ks : tp.getKeyListeners()) {
            tp.removeKeyListener(ks);
         }

         ResultsTableListener myk = new ResultsTableListener(iPlus_, res, twin, parms_);
         tp.addKeyListener(myk);
         tp.addMouseListener(myk);
         frame.toFront();

         // atach listener to ImageWindow
         ImageWindow iWin = iPlus_.getWindow();
         ImageCanvas canvas = iWin.getCanvas();
         for (MouseListener ms : canvas.getMouseListeners()) {
            if (ms instanceof ImageWindowListener) {
               canvas.removeMouseListener(ms);
            }
         }
         ImageWindowListener iwl = new ImageWindowListener(iPlus_, res, myk,
                 twin, maxima);
         canvas.addMouseListener(iwl);
      }

   }

   
   public void preview () {
      
      // first calculate the mean of the first n images
      final ImageProcessor ip = iPlus_.getProcessor();
      
      Polygon maxima = FindLocalMaxima.FindMax(ip, 
              parms_.detectionRadius_, parms_.noiseTolerance_,
              FindLocalMaxima.FilterType.NONE);
      Overlay ovl = Utils.GetSpotOverlay(maxima, parms_.detectionRadius_, Color.red);
      
      iPlus_.setOverlay(ovl);
   }
   
   
}
