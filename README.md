# spotIntensityInAllChannels

ImageJ/Fiji plugin for analysis of intensity of spots identified in one channel, in all channels.  

Usage: open a multi-channel image in Fiji/ImageJ (check for the presence of multiple channels using Image > Properties). Select the channel to be used as the "cpot mask".  Open the plugin.  The parameters "Detection Spot Radius" nad "Noise Tolerance" are used to locate spots.  You can check their effect by pressing the "Preview" button.  Once you are satisfied wit the preview, press OK.  The average intensity of the spots with radius "Measurement Spot Radius" as well as the median value of the pixels lying on the circumference of a circle with size "Background" radius" will be shown.  Clicking on lines in the output will highlight the spot in the image and vice versa.
