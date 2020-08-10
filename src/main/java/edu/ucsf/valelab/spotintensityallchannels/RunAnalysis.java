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
 import edu.ucsf.valelab.spotintensityallchannels.data.MeasurementParameters;
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
   final MeasurementParameters parameters_;
   final GenericDialog gd_;
   
   public RunAnalysis(ImagePlus iPlus,
                      MeasurementParameters parameters, GenericDialog gd){
      iPlus_ = iPlus;
      parameters_ = parameters;
      gd_ = gd;
   }
   
   @Override
   public void run() {
      final int originalC = iPlus_.getChannel();
      final int originalZ = iPlus_.getSlice();
      Polygon maxima = FindLocalMaxima.FindMax(iPlus_,
              parameters_.detectionRadius_, parameters_.noiseTolerance_,
              FindLocalMaxima.FilterType.NONE);
      Overlay ovl = Utils.GetSpotOverlay(maxima, parameters_.detectionRadius_, Color.red);
      
      iPlus_.setOverlay(ovl);

      ResultsTable res = new ResultsTable();
      res.setPrecision(1);
      
      for (int i = 0; i < maxima.npoints; i++) {
         res.incrementCounter();
         final int x = maxima.xpoints[i];
         final int y = maxima.ypoints[i];
         res.addValue("x", x);
         res.addValue("y", y);
         if (parameters_.useSlicesAsChannels_) {
            for (int z = 0; z < iPlus_.getNSlices(); z++) {
               iPlus_.setPositionWithoutUpdate(iPlus_.getChannel(), z + 1, iPlus_.getFrame());
               ImageProcessor ipc = iPlus_.getProcessor();
               float intensity = Utils.GetAvgIntensity(ipc, x, y, parameters_.detectionRadius_);
               res.addValue("I-ch" + (z + 1), intensity);
               float background = Utils.GetBackgroundCircleRp2(ipc, x, y, parameters_.backgroundRadius_);
               res.addValue("B-ch" + (z + 1), background);
            }
         } else {
            for (int c = 0; c < iPlus_.getNChannels(); c++) {
               iPlus_.setPositionWithoutUpdate(c + 1, iPlus_.getSlice(), iPlus_.getFrame());
               ImageProcessor ipc = iPlus_.getProcessor();
               float intensity = Utils.GetAvgIntensity(ipc, x, y, parameters_.detectionRadius_);
               res.addValue("I-ch" + (c + 1), intensity);
               float background = Utils.GetBackgroundCircleRp2(ipc, x, y, parameters_.backgroundRadius_);
               res.addValue("B-ch" + (c + 1), background);
            }
         }
      }

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

         ResultsTableListener myk = new ResultsTableListener(iPlus_, res, twin, parameters_);
         tp.addKeyListener(myk);
         tp.addMouseListener(myk);
         frame.toFront();

         // attach listener to ImageWindow
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

      if (parameters_.useSlicesAsChannels_) {
         iPlus_.setPosition(iPlus_.getChannel(), originalZ, iPlus_.getFrame());
      } else {
         iPlus_.setPosition(originalC, iPlus_.getSlice(), iPlus_.getFrame());
      }

   }

   
   public void preview () {
      Polygon maxima = FindLocalMaxima.FindMax(iPlus_,
              parameters_.detectionRadius_, parameters_.noiseTolerance_,
              FindLocalMaxima.FilterType.NONE);
      Overlay ovl = Utils.GetSpotOverlay(maxima, parameters_.detectionRadius_, Color.red);
      
      iPlus_.setOverlay(ovl);
   }
   
   
}
