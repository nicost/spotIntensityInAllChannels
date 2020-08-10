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

import edu.ucsf.valelab.spotintensityallchannels.data.MeasurementParameters;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.PlugIn;
import java.awt.AWTEvent;
import java.util.prefs.Preferences;


public class SpotIntensityInAllChannels implements PlugIn, DialogListener {

   private MeasurementParameters parameters_;
   private final Preferences myPrefs_;
   private final String SPOT_RADIUS = "SpotRadius";
   private final String NOISE_TOLERANCE = "NoiseTolerance";
   private final String MEASUREMENT_SPOT_RADIUS = "MeasurementSpotRadius";
   private final String BACKGROUND_RADIUS = "BackgroundRadius";
   private final String TREAT_SLICES_AS_CHANNELS = "TreatSLicesASChannels";
   
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
      gd.addCheckbox("Treat Slices as Channels",
              myPrefs_.getBoolean(TREAT_SLICES_AS_CHANNELS, true));
      
      gd.addPreviewCheckbox(null, "Preview");
      
      gd.addDialogListener(this);
      
      parameters_ = getParams(gd);
      
      gd.showDialog();
      
      if (gd.wasOKed()) {
         RunAnalysis ra = new RunAnalysis(ij.IJ.getImage(), parameters_, gd);
         ra.start ();
      }
      
   }

   @Override
   public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
      parameters_ = getParams(gd);
      ImagePlus img = WindowManager.getCurrentImage();
      if (img != null) {
         if (!gd.isPreviewActive()) {
            img.setOverlay(null);
         } else {
            RunAnalysis ra = new RunAnalysis(img, parameters_, gd);
            ra.preview();
         }
      }
      return true;
   }

   private MeasurementParameters getParams(GenericDialog gd) {
      MeasurementParameters parms = new MeasurementParameters();
      parms.detectionRadius_ = (int) gd.getNextNumber();
      myPrefs_.putInt(SPOT_RADIUS, parms.detectionRadius_);
      parms.noiseTolerance_ = (int) gd.getNextNumber();
      myPrefs_.putInt(NOISE_TOLERANCE, parms.noiseTolerance_);
      parms.measurementRadius_ = (int) gd.getNextNumber();
      myPrefs_.putInt(MEASUREMENT_SPOT_RADIUS, parms.measurementRadius_);
      parms.backgroundRadius_ = (int) gd.getNextNumber();
      myPrefs_.putInt(BACKGROUND_RADIUS, parms.backgroundRadius_);
      parms.useSlicesAsChannels_ = gd.getNextBoolean();
      myPrefs_.putBoolean(TREAT_SLICES_AS_CHANNELS, parms.useSlicesAsChannels_);

      return parms;
   }
}

