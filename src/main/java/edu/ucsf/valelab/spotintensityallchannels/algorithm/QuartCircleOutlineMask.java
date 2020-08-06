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

package edu.ucsf.valelab.spotintensityallchannels.algorithm;


/**
 * @author nico
 * <p>
 * Maintains a square mask of pixels representing one quadrant of the unit
 * square (i.e. 0 and positive integer values of the x and y axis)
 * It sets 'pixels' at a radius to true, other are false
 * <p>
 * Used to avoid having to do the Math.sqrt calculation more often than needed.
 */
public class QuartCircleOutlineMask {

   private final int radius_;
   private final boolean[][] mask_;

   public QuartCircleOutlineMask(int radius) {
      radius_ = radius;
      mask_ = new boolean[radius + 1][radius + 1];
      for (int i = 0; i <= radius; i++) {
         for (int j = 0; j <= radius; j++) {
            if (i == 0 && j == 0) {
               mask_[i][j] = false;
            }
            else {
               double d = Math.sqrt((i * i) + (j * j));
               if (Math.abs(d - radius) < 0.5) {
                  mask_[i][j] = true;
               }
            }
         }
      }
   }

   public int getRadius() {
      return radius_;
   }

   public boolean[][] getMask() {
      return mask_;
   }

   public void print() {
      for (int y = radius_; y >= 0; y--) {
         String txt = "" + y + "  ";
         for (int x = 0; x <= radius_; x++) {
            String tmp = "-  ";
            if (mask_[x][y]) {
               tmp = "x  ";
            }
            txt += tmp;
         }
         ij.IJ.log(txt);
      }
      String xAxis = "   ";
      for (int x = 0; x <= radius_; x++) {
         xAxis += x + "  ";
      }
      ij.IJ.log(xAxis);
   }

}