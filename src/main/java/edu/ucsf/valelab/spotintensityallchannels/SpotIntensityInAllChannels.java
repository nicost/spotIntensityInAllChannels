///////////////////////////////////////////////////////////////////////////////
//FILE:          SpotIntensityInAllChannels.java
//PROJECT:       SpotIntensityInAllChannels
//-----------------------------------------------------------------------------
//
// AUTHOR:       Nico Stuurman
//
// COPYRIGHT:    University of California, San Francisco 2020
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

import edu.ucsf.valelab.spotintensityallchannels.data.SpotIntensityParameters;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;
import java.awt.AWTEvent;
import java.util.prefs.Preferences;


public class SpotIntensityInAllChannels implements PlugIn, DialogListener {

   private SpotIntensityParameters parms_;
   private final Preferences myPrefs_;
   private final String SPOT_RADIUS = "SpotRadius";
   private final String NOISE_TOLERANCE = "NoiseTolerance";
   private final String MEASUREMENT_SPOT_RADIUS = "MeasurementSpotRadius";
   private final String BACKGROUND_RADIUS = "BackgroundRadius";
   
   public SpotIntensityInAllChannels() {
      myPrefs_ = Preferences.userNodeForPackage(this.getClass());
   }
   
   @Override
   public void run(String arg) {
      final NonBlockingGenericDialog gd = new NonBlockingGenericDialog( 
              "Spot Intensity In All Channels" );
      gd.addMessage("Select Channel and optionally draw ROI in image first");
      gd.addHelp("http://imagej.net/Spot_Intensity_In_All_Channels");
      gd.addNumericField("Detection Spot Radius (pixels)",
              myPrefs_.getInt(SPOT_RADIUS, 3), 0);
      gd.addNumericField("Noise tolerance", 
              myPrefs_.getInt(NOISE_TOLERANCE, 500), 0);
      gd.addNumericField("Measurement Spot Radius (pixels)",
              myPrefs_.getInt(MEASUREMENT_SPOT_RADIUS, 3), 0);
      gd.addNumericField("Background radius",
              myPrefs_.getInt(BACKGROUND_RADIUS, 5), 0);

      
      gd.addPreviewCheckbox(null, "Preview");
      
      gd.addDialogListener(this);
      
      parms_ = getParams(gd);
      
      gd.showDialog();
      
      if (gd.wasOKed()) {
         RunAnalysis ra = new RunAnalysis(ij.IJ.getImage(), parms_, gd);
         ra.start ();
      }
      
   }

   @Override
   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
      parms_ = getParams(gd);
      ImagePlus img = WindowManager.getCurrentImage();
      if (img != null) {
         if (!gd.isPreviewActive()) {
            img.setOverlay(null);
         } else {
            RunAnalysis ra = new RunAnalysis(img, parms_, gd);
            ra.preview();
         }
      }
      return true;
   }

   private SpotIntensityParameters getParams(GenericDialog gd) {
      SpotIntensityParameters parms = new SpotIntensityParameters();
      parms.detectionRadius_ = (int) gd.getNextNumber();
      myPrefs_.putInt(SPOT_RADIUS, parms.detectionRadius_);
      parms.noiseTolerance_ = (int) gd.getNextNumber();
      myPrefs_.putInt(NOISE_TOLERANCE, parms.noiseTolerance_);
      parms.measurementRadius_ = (int) gd.getNextNumber();
      myPrefs_.putInt(MEASUREMENT_SPOT_RADIUS, parms.measurementRadius_);
      parms.backgroundRadius_ = (int) gd.getNextNumber();
      myPrefs_.putInt(BACKGROUND_RADIUS, parms.backgroundRadius_);

      return parms;
   }
}

