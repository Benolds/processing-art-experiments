import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class v1 extends PApplet {

// PARAMS
int rows = 16;
int cols = 40;
int gridMargin = 5;
int margin = 3;
boolean useSquareCells = true;
int _width = 800;
int _height = 600;
int sWidth;
int sHeight;

// MISC VARIABLES
int time = 0;
float ttime = 0;

public void setup() {
  size(_width,_height);
  background(70);
  
  if (useSquareCells) {
    rows = cols * _height / _width;
  }
  
  sWidth = (width-2*gridMargin)/cols;
  sHeight = (height-2*gridMargin)/rows;
  
  for(int r = 0; r < rows; r++) {
    for(int c = 0; c < cols; c++) {
      fill(255);
      rect(gridMargin + c*sWidth, gridMargin + r*sHeight, sWidth, sHeight);
    } 
  }
  
}

public void draw() {
  time++;
  ttime++;
//  if (ttime > 100) {
//    ttime -= 100;
//  }

  ttime = max(rows,cols) + max(rows,cols) * sin(PApplet.parseFloat(time)/100.0f);
  
  for(int r = 0; r < rows; r++) {
    for(int c = 0; c < cols; c++) {
      int shade = 255;
      
      if ((r+c) <= ttime){
      //if ((r+c) % ttime == 0){
        shade = max(100, 255 - 20 * (PApplet.parseInt(ttime) - (r+c)));
      }
      fill(shade);
      rect(gridMargin + c*sWidth, gridMargin + r*sHeight, sWidth, sHeight);
    } 
  }
  
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "v1" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
