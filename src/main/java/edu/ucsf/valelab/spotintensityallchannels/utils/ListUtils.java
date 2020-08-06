package edu.ucsf.valelab.spotintensityallchannels.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListUtils {

   /**
    * Calculates the average of a list of doubles
    *
    * @param <T> type of list
    * @param vals listvalues
    * @return average average value of the list
    */
   public static <T extends Number> double avg(List<T> vals) {
      double result = 0;
      T sample = vals.get(0);
      if (sample instanceof Double) {
         for (T val : vals) {
            result += (Double) val;
         }
      } else if (sample instanceof Float) {
         for (T val : vals) {
            result += (Float) val;
         }
      } else if (sample instanceof Integer) {
         for (T val : vals) {
            result += (Integer) val;
         }
      }

      return result / vals.size();
   }

   /**
    * Calculates the average of a list of doubles
    *
    * @param vals listvalues
    * @return average average value of the list
    */
   public static float median(List<Float> vals) {

      Collections.sort(vals, new Comparator<Float>() {
         @Override
         public int compare(Float o, Float p) {
            if (o.floatValue() == p.floatValue()) {
               return 0;
            } else if (o < p) {
               return 1;
            }
            return -1;
         }
      });

      return vals.get(vals.size() / 2).floatValue();
   }



}
