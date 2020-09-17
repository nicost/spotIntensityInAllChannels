/*
 ///////////////////////////////////////////////////////////////////////////////
 //FILE:          
 //PROJECT:       
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
 */
package edu.ucsf.valelab.spotintensityallchannels.data;


/**
 *
 * @author nico
 */
public class MeasurementParameters {
   public static final String CLASSIC = "Channels in Columns";
   public static final String CLASSIC_BC = "Channels in Columns BG subtracted";
   public static final String CHANNELS_ROWS = "Channels in Rows";
   public static final String CHANNELS_ROWS_BC = "Channels in Rows BG subtracted";
   public static final String[] outputFormats = {CLASSIC, CLASSIC_BC, CHANNELS_ROWS, CHANNELS_ROWS_BC};


   public int noiseTolerance_;
   public int detectionRadius_;
   public int measurementRadius_;
   public int backgroundRadius_;
   public boolean useSlicesAsChannels_;
   public String outputFormat_;
   
}
