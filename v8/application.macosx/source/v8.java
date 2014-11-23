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

public class v8 extends PApplet {

//Uses fast blur from http://forum.processing.org/one/topic/fast-blurring.html

int columns = 8;
int rows = 5;
float sphereBaseRadius = 200.0f;
int time = 0;

float sinkX = 0;
float sinkY = 0;

float sink2X = 0;
float sink2Y = 0;

int[] screenBuf;
int numPixels;

public void setup() {
  size(900, 600, P3D);
  //size(720, 820, P3D);
  noStroke();
  fill(204);
  background(0);
//  setupBlur();

  numPixels = width*height;
  screenBuf = new int[numPixels];
  loadPixels();
}

public void draw() {
  time += 1;
  noStroke();
  
  //background(0, 127);

  float dirY = (mouseY / PApplet.parseFloat(height) - 0.5f) * 2;
  float dirX = (mouseX / PApplet.parseFloat(width) - 0.5f) * 2;
  
  //directionalLight(204, 204, 204, -dirX, -dirY, -1); 
  
  //directionalLight(255, 255, 255, 0, 0, -1); 

  pointLight(255, 255, 255, mouseX, mouseY, 800);
  pointLight(255, 255, 255, mouseX, mouseY, 0);

  sinkX = width/2 + width/2 * cos(time*0.1f); //mouseX;
  sinkY = height/2 + height/2 * sin(time*0.1f);//mouseY;
  
  // sink2 is reflected across the origin from sink1
  sink2X = width/2; //mouseX; //width/2 - width/2 * cos(time*0.01);
  sink2Y = height/2; //mouseY; //width/2 - height/2 * sin(time*0.01);
    
  for (int r = 0; r < rows; r++){
    
    pushMatrix();
    
    translate(0, (r+1) * height/(rows+1), 0); 
    
    for (int c = 0; c < columns; c++){
      
      float x = (c+1) * width/(columns+1);
      float y = (r+1) * height/(rows+1);
      float distToSink = sqrt( (sinkX-x)*(sinkX-x) + (sinkY-y)*(sinkY-y) );
      float distToSink2 = sqrt( (sink2X-x)*(sink2X-x) + (sink2Y-y)*(sink2Y-y) );
      
      translate(width/(columns+1), 0, 0);
      pushMatrix();
      // z is translated separately so it can be reset for each sphere
      translate(0,0,-2000+0.01f*(distToSink*distToSink)-0.01f*(distToSink2*distToSink2));
      sphere(sphereBaseRadius/max(rows,columns));
      popMatrix();
      
    }
    
    popMatrix();
  }
  
  //filter(THRESHOLD);
  
  filter(THRESHOLD);  //<-- builtin is 10x slower than customized blur code
//  loadPixels();
//  shiftBlur3(pixels, screenBuf);
//  arrayCopy(screenBuf, pixels);
//  updatePixels();
//  loadPixels();
//  shiftBlur3(pixels, screenBuf);
//  arrayCopy(screenBuf, pixels);
//  updatePixels();
  filter(ERODE);  //<-- builtin is 10x slower than customized blur code
//  loadPixels();
//  shiftBlur3(pixels, screenBuf);
//  arrayCopy(screenBuf, pixels);
//  updatePixels();
  
}


// 010
// 141
// 010
// 8 in total, >>3
// yOffset optimisation suggested by Mario Klingemann


public void shiftBlur1(int[] s, int[] t) { // source & target buffer
  int yOffset;

  for (int i = 1; i < (width-1); ++i) {

    yOffset = width*(height-1);
    // top edge (minus corner pixels)
    t[i] = (((((s[i] & 0xFF) << 2 ) + 
      (s[i+1] & 0xFF) + 
      (s[i-1] & 0xFF) + 
      (s[i + width] & 0xFF) + 
      (s[i + yOffset] & 0xFF)) >> 3)  & 0xFF) +
      (((((s[i] & 0xFF00) << 2 ) + 
      (s[i+1] & 0xFF00) + 
      (s[i-1] & 0xFF00) + 
      (s[i + width] & 0xFF00) + 
      (s[i + yOffset] & 0xFF00)) >> 3)  & 0xFF00) +
      (((((s[i] & 0xFF0000) << 2 ) + 
      (s[i+1] & 0xFF0000) + 
      (s[i-1] & 0xFF0000) + 
      (s[i + width] & 0xFF0000) + 
      (s[i + yOffset] & 0xFF0000)) >> 3)  & 0xFF0000) +
      0xFF000000; //ignores transparency

    // bottom edge (minus corner pixels)
    t[i + yOffset] = (((((s[i + yOffset] & 0xFF) << 2 ) + 
      (s[i - 1 + yOffset] & 0xFF) + 
      (s[i + 1 + yOffset] & 0xFF) +
      (s[i + yOffset - width] & 0xFF) +
      (s[i] & 0xFF)) >> 3) & 0xFF) +
      (((((s[i + yOffset] & 0xFF00) << 2 ) + 
      (s[i - 1 + yOffset] & 0xFF00) + 
      (s[i + 1 + yOffset] & 0xFF00) +
      (s[i + yOffset] & 0xFF00) +
      (s[i] & 0xFF00)) >> 3) & 0xFF00) +
      (((((s[i + yOffset] & 0xFF0000) << 2 ) + 
      (s[i - 1 + yOffset] & 0xFF0000) + 
      (s[i + 1 + yOffset] & 0xFF0000) +
      (s[i + yOffset - width] & 0xFF0000) +
      (s[i] & 0xFF0000)) >> 3) & 0xFF0000) +
      0xFF000000;    

    // central square
    for (int j = 1; j < (height-1); ++j) {
      yOffset = j*width;
      t[i + yOffset] = (((((s[i + yOffset] & 0xFF) << 2 ) +
        (s[i + 1 + yOffset] & 0xFF) +
        (s[i - 1 + yOffset] & 0xFF) +
        (s[i + yOffset + width] & 0xFF) +
        (s[i + yOffset - width] & 0xFF)) >> 3) & 0xFF) +
        (((((s[i + yOffset] & 0xFF00) << 2 ) +
        (s[i + 1 + yOffset] & 0xFF00) +
        (s[i - 1 + yOffset] & 0xFF00) +
        (s[i + yOffset + width] & 0xFF00) +
        (s[i + yOffset - width] & 0xFF00)) >> 3) & 0xFF00) +
        (((((s[i + yOffset] & 0xFF0000) << 2 ) +
        (s[i + 1 + yOffset] & 0xFF0000) +
        (s[i - 1 + yOffset] & 0xFF0000) +
        (s[i + yOffset + width] & 0xFF0000) +
        (s[i + yOffset - width] & 0xFF0000)) >> 3) & 0xFF0000) +
        0xFF000000;
    }
  }

  // left and right edge (minus corner pixels)
  for (int j = 1; j < (height-1); ++j) {
    yOffset = j*width;
    t[yOffset] = (((((s[yOffset] & 0xFF) << 2 ) +
      (s[yOffset + 1] & 0xFF) +
      (s[yOffset + width - 1] & 0xFF) +
      (s[yOffset + width] & 0xFF) +
      (s[yOffset - width] & 0xFF) ) >> 3) & 0xFF) +
      (((((s[yOffset] & 0xFF00) << 2 ) +
      (s[yOffset + 1] & 0xFF00) +
      (s[yOffset + width - 1] & 0xFF00) +
      (s[yOffset + width] & 0xFF00) +
      (s[yOffset - width] & 0xFF00) ) >> 3) & 0xFF00) +
      (((((s[yOffset] & 0xFF0000) << 2 ) +
      (s[yOffset + 1] & 0xFF0000) +
      (s[yOffset + width - 1] & 0xFF0000) +
      (s[yOffset + width] & 0xFF0000) +
      (s[yOffset - width] & 0xFF0000) ) >> 3) & 0xFF0000) +
      0xFF000000;

    t[yOffset + width - 1] = (((((s[(j+1)*width - 1] & 0xFF) << 2 ) +
      (s[j*width] & 0xFF) +
      (s[yOffset + width - 2] & 0xFF) +
      (s[yOffset + (width<<1) - 1] & 0xFF) +
      (s[yOffset - 1] & 0xFF)) >> 3) & 0xFF) +
      (((((s[yOffset + width - 1] & 0xFF00) << 2) +
      (s[yOffset] & 0xFF00) +
      (s[yOffset + width - 2] & 0xFF00) +
      (s[yOffset + (width<<1) - 1] & 0xFF00) +
      (s[yOffset - 1] & 0xFF00)) >> 3) & 0xFF00) +
      (((((s[yOffset + width - 1] & 0xFF0000) << 2) +
      (s[yOffset] & 0xFF0000) +
      (s[yOffset + width - 2] & 0xFF0000) +
      (s[yOffset + (width<<1) - 1] & 0xFF0000) +
      (s[yOffset - 1] & 0xFF0000)) >> 3) & 0xFF0000) +
      0xFF000000;
  }

  // corner pixels
  t[0] = (((((s[0] & 0xFF) << 2) + 
    (s[1] & 0xFF) + 
    (s[width-1] & 0xFF) + 
    (s[width] & 0xFF) + 
    (s[width*(height-1)] & 0xFF)) >> 3)  & 0xFF) +
    (((((s[0] & 0xFF00) << 2) + 
    (s[1] & 0xFF00) + 
    (s[width-1] & 0xFF00) + 
    (s[width] & 0xFF00) + 
    (s[width*(height-1)] & 0xFF00)) >> 3)  & 0xFF00) +
    (((((s[0] & 0xFF0000) << 2) + 
    (s[1] & 0xFF0000) + 
    (s[width-1] & 0xFF0000) + 
    (s[width] & 0xFF0000) + 
    (s[width*(height-1)] & 0xFF0000)) >> 3)  & 0xFF0000) +
    0xFF000000;

  t[width - 1 ] = (((((s[width-1] & 0xFF) << 2) + 
    (s[width-2] & 0xFF) + 
    (s[0] & 0xFF) + 
    (s[(width<<1) - 1] & 0xFF) + 
    (s[width*height-1] & 0xFF) ) >> 3) & 0xFF) +
    (((((s[width-1] & 0xFF00) << 2) + 
    (s[width-2] & 0xFF00) + 
    (s[0] & 0xFF00) + 
    (s[(width<<1) - 1] & 0xFF00) + 
    (s[width*height-1] & 0xFF00) ) >> 3) & 0xFF00) +
    (((((s[width-1] & 0xFF0000) << 2) + 
    (s[width-2] & 0xFF0000) + 
    (s[0] & 0xFF0000) + 
    (s[(width<<1) - 1] & 0xFF0000) + 
    (s[width*height-1] & 0xFF0000) ) >> 3) & 0xFF0000) +
    0xFF000000;

  t[width * height - 1] = (((((s[width*height-1] & 0xFF) << 2) + 
    (s[width-1] & 0xFF) + 
    (s[width*(height-1)-1] & 0xFF) + 
    (s[width*height-2] & 0xFF) + 
    (s[width*(height-1)] & 0xFF) ) >> 3) & 0xFF) +
    (((((s[width*height-1] & 0xFF00) << 2) + 
    (s[width-1] & 0xFF00) + 
    (s[width*(height-1)-1] & 0xFF00) + 
    (s[width*height-2] & 0xFF00) + 
    (s[width*(height-1)] & 0xFF00) ) >> 3) & 0xFF00) +
    (((((s[width*height-1] & 0xFF0000) << 2) + 
    (s[width-1] & 0xFF0000) + 
    (s[width*(height-1)-1] & 0xFF0000) + 
    (s[width*height-2] & 0xFF0000) + 
    (s[width*(height-1)] & 0xFF0000) ) >> 3) & 0xFF0000) +
    0xFF000000;

  t[width *(height-1)] = (((((s[width*(height-1)] & 0xFF) << 2) + 
    (s[width*(height-1) + 1] & 0xFF) + 
    (s[width*height-1] & 0xFF) + 
    (s[width*(height-2)] & 0xFF) + 
    (s[0] & 0xFF) ) >> 3) & 0xFF) +
    (((((s[width*(height-1)] & 0xFF00) << 2) + 
    (s[width*(height-1) + 1] & 0xFF00) + 
    (s[width*height-1] & 0xFF00) + 
    (s[width*(height-2)] & 0xFF00) + 
    (s[0] & 0xFF00) ) >> 3) & 0xFF00) +
    (((((s[width*(height-1)] & 0xFF0000) << 2) + 
    (s[width*(height-1) + 1] & 0xFF0000) + 
    (s[width*height-1] & 0xFF0000) + 
    (s[width*(height-2)] & 0xFF0000) + 
    (s[0] & 0xFF0000) ) >> 3) & 0xFF0000) +
    0xFF000000;
}
// 00 51 00
// 51 52 00
// 00 51 00
// 256 in total, >>8

public void shiftBlur3(int[] s, int[] t){ // source & target buffer  int yOffset;
  int yOffset;
  for (int i = 1; i < (width-1); ++i){
    
    yOffset = width*(height-1);
    // top edge (minus corner pixels)
    t[i] = (((((s[i] & 0xFF) * 52) + 
      ((s[i+1] & 0xFF) + 
      (s[i-1] & 0xFF) + 
      (s[i + width] & 0xFF) + 
      (s[i + yOffset] & 0xFF)) * 51) >>> 8)  & 0xFF) +
      (((((s[i] & 0xFF00) * 52) + 
      ((s[i+1] & 0xFF00) + 
      (s[i-1] & 0xFF00) + 
      (s[i + width] & 0xFF00) + 
      (s[i + yOffset] & 0xFF00)) * 51) >>> 8)  & 0xFF00) +
      (((((s[i] & 0xFF0000) * 52) + 
      ((s[i+1] & 0xFF0000) + 
      (s[i-1] & 0xFF0000) + 
      (s[i + width] & 0xFF0000) + 
      (s[i + yOffset] & 0xFF0000)) * 51) >>> 8)  & 0xFF0000) +
      0xFF000000; //ignores transparency

    // bottom edge (minus corner pixels)
    t[i + yOffset] = (((((s[i + yOffset] & 0xFF) * 52) + 
      ((s[i - 1 + yOffset] & 0xFF) + 
      (s[i + 1 + yOffset] & 0xFF) +
      (s[i + yOffset - width] & 0xFF) +
      (s[i] & 0xFF)) * 51) >>> 8) & 0xFF) +
      (((((s[i + yOffset] & 0xFF00) * 52) + 
      ((s[i - 1 + yOffset] & 0xFF00) + 
      (s[i + 1 + yOffset] & 0xFF00) +
      (s[i + yOffset - width] & 0xFF00) +
      (s[i] & 0xFF00)) * 51) >>> 8) & 0xFF00) +
      (((((s[i + yOffset] & 0xFF0000) * 52) + 
      ((s[i - 1 + yOffset] & 0xFF0000) + 
      (s[i + 1 + yOffset] & 0xFF0000) +
      (s[i + yOffset - width] & 0xFF0000) +
      (s[i] & 0xFF0000)) * 51) >>> 8) & 0xFF0000) +
      0xFF000000;    
    
    // central square
    for (int j = 1; j < (height-1); ++j){
      yOffset = j*width;
      t[i + yOffset] = (((((s[i + yOffset] & 0xFF) * 52) +
        ((s[i + 1 + yOffset] & 0xFF) +
        (s[i - 1 + yOffset] & 0xFF) +
        (s[i + yOffset + width] & 0xFF) +
        (s[i + yOffset - width] & 0xFF)) * 51) >>> 8) & 0xFF) +
        (((((s[i + yOffset] & 0xFF00) * 52) +
        ((s[i + 1 + yOffset] & 0xFF00) +
        (s[i - 1 + yOffset] & 0xFF00) +
        (s[i + yOffset + width] & 0xFF00) +
        (s[i + yOffset - width] & 0xFF00)) * 51) >>> 8) & 0xFF00) +
        (((((s[i + yOffset] & 0xFF0000) * 52) +
        ((s[i + 1 + yOffset] & 0xFF0000) +
        (s[i - 1 + yOffset] & 0xFF0000) +
        (s[i + yOffset + width] & 0xFF0000) +
        (s[i + yOffset - width] & 0xFF0000)) * 51) >>> 8) & 0xFF0000) +
        0xFF000000;
    }
  }
  
  // left and right edge (minus corner pixels)
  for (int j = 1; j < (height-1); ++j){
      yOffset = j*width;
      t[yOffset] = (((((s[yOffset] & 0xFF) * 52) +
        ((s[yOffset + 1] & 0xFF) +
        (s[yOffset + width - 1] & 0xFF) +
        (s[yOffset + width] & 0xFF) +
        (s[yOffset - width] & 0xFF) ) * 51) >>> 8) & 0xFF) +
        (((((s[yOffset] & 0xFF00) * 52) +
        ((s[yOffset + 1] & 0xFF00) +
        (s[yOffset + width - 1] & 0xFF00) +
        (s[yOffset + width] & 0xFF00) +
        (s[yOffset - width] & 0xFF00) ) * 51) >>> 8) & 0xFF00) +
        (((((s[yOffset] & 0xFF0000) * 52) +
        ((s[yOffset + 1] & 0xFF0000) +
        (s[yOffset + width - 1] & 0xFF0000) +
        (s[yOffset + width] & 0xFF0000) +
        (s[yOffset - width] & 0xFF0000) ) * 51) >>> 8) & 0xFF0000) +
        0xFF000000;

      t[yOffset + width - 1] = (((((s[yOffset + width - 1] & 0xFF) * 52) +
        ((s[yOffset] & 0xFF) +
        (s[yOffset + width - 2] & 0xFF) +
        (s[yOffset + (width<<1) - 1] & 0xFF) +
        (s[yOffset - 1] & 0xFF)) * 51) >>> 8) & 0xFF) +
        (((((s[yOffset + width - 1] & 0xFF00) * 52) +
        ((s[yOffset] & 0xFF00) +
        (s[yOffset + width - 2] & 0xFF00) +
        (s[yOffset + (width<<1) - 1] & 0xFF00) +
        (s[yOffset - 1] & 0xFF00)) * 51) >>> 8) & 0xFF00) +
        (((((s[yOffset + width - 1] & 0xFF0000) * 52) +
        ((s[yOffset] & 0xFF0000) +
        (s[yOffset + width - 2] & 0xFF0000) +
        (s[yOffset + (width<<1) - 1] & 0xFF0000) +
        (s[yOffset - 1] & 0xFF0000)) * 51) >>> 8) & 0xFF0000) +
        0xFF000000;
  }
  
  // corner pixels
  t[0] = (((((s[0] & 0xFF) * 52) + 
    ((s[1] & 0xFF) + 
    (s[width-1] & 0xFF) + 
    (s[width] & 0xFF) + 
    (s[width*(height-1)] & 0xFF)) * 51) >>> 8)  & 0xFF) +
    (((((s[0] & 0xFF00) * 52) + 
    ((s[1] & 0xFF00) + 
    (s[width-1] & 0xFF00) + 
    (s[width] & 0xFF00) + 
    (s[width*(height-1)] & 0xFF00)) * 51) >>> 8)  & 0xFF00) +
    (((((s[0] & 0xFF0000) * 52) + 
    ((s[1] & 0xFF0000) + 
    (s[width-1] & 0xFF0000) + 
    (s[width] & 0xFF0000) + 
    (s[width*(height-1)] & 0xFF0000)) * 51) >>> 8)  & 0xFF0000) +
    0xFF000000;

  t[width - 1 ] = (((((s[width-1] & 0xFF) * 52) + 
    ((s[width-2] & 0xFF) + 
    (s[0] & 0xFF) + 
    (s[(width<<1) - 1] & 0xFF) + 
    (s[width*height-1] & 0xFF) ) * 51) >>> 8) & 0xFF) +
    (((((s[width-1] & 0xFF00) * 52) + 
    ((s[width-2] & 0xFF00) + 
    (s[0] & 0xFF00) + 
    (s[(width<<1) - 1] & 0xFF00) + 
    (s[width*height-1] & 0xFF00) ) * 51) >>> 8) & 0xFF00) +
    (((((s[width-1] & 0xFF0000) * 52) + 
    ((s[width-2] & 0xFF0000) + 
    (s[0] & 0xFF0000) + 
    (s[(width<<1) - 1] & 0xFF0000) + 
    (s[width*height-1] & 0xFF0000) ) * 51) >>> 8) & 0xFF0000) +
    0xFF000000;

  t[width * height - 1] = (((((s[width*height-1] & 0xFF) * 52) + 
    ((s[width-1] & 0xFF) + 
    (s[width*(height-1)-1] & 0xFF) + 
    (s[width*height-2] & 0xFF) + 
    (s[width*(height-1)] & 0xFF) ) * 51) >>> 8) & 0xFF) +
    (((((s[width*height-1] & 0xFF00) * 52) + 
    ((s[width-1] & 0xFF00) + 
    (s[width*(height-1)-1] & 0xFF00) + 
    (s[width*height-2] & 0xFF00) + 
    (s[width*(height-1)] & 0xFF00) ) * 51) >>> 8) & 0xFF00) +
    (((((s[width*height-1] & 0xFF0000) * 52) + 
    ((s[width-1] & 0xFF0000) + 
    (s[width*(height-1)-1] & 0xFF0000) + 
    (s[width*height-2] & 0xFF0000) + 
    (s[width*(height-1)] & 0xFF0000) ) * 51) >>> 8) & 0xFF0000) +
    0xFF000000;
  
  t[width *(height-1)] = (((((s[width*(height-1)] & 0xFF) * 52) + 
    ((s[width*(height-1) + 1] & 0xFF) + 
    (s[width*height-1] & 0xFF) + 
    (s[width*(height-2)] & 0xFF) + 
    (s[0] & 0xFF) ) * 51) >>> 8) & 0xFF) +
    (((((s[width*(height-1)] & 0xFF00) * 52) + 
    ((s[width*(height-1) + 1] & 0xFF00) + 
    (s[width*height-1] & 0xFF00) + 
    (s[width*(height-2)] & 0xFF00) + 
    (s[0] & 0xFF00) ) * 51) >>> 8) & 0xFF00) +
    (((((s[width*(height-1)] & 0xFF0000) * 52) + 
    ((s[width*(height-1) + 1] & 0xFF0000) + 
    (s[width*height-1] & 0xFF0000) + 
    (s[width*(height-2)] & 0xFF0000) + 
    (s[0] & 0xFF0000) ) * 51) >>> 8) & 0xFF0000) +
    0xFF000000;
}

// 0a0
// aba
// 0a0
// 4*a + b in total, >>> c
public void shiftBlur(final int[] s, final int[] t, final int a, final int b, final int c){
  int yOffset;
  for (int i = 1; i < (width-1); ++i){
    
    yOffset = width*(height-1);
    // top edge (minus corner pixels)
    t[i] = (((((s[i] & 0xFF) * b) + 
      ((s[i+1] & 0xFF) + 
      (s[i-1] & 0xFF) + 
      (s[i + width] & 0xFF) + 
      (s[i + yOffset] & 0xFF)) * a) >>> c)  & 0xFF) +
      (((((s[i] & 0xFF00) * b) + 
      ((s[i+1] & 0xFF00) + 
      (s[i-1] & 0xFF00) + 
      (s[i + width] & 0xFF00) + 
      (s[i + yOffset] & 0xFF00)) * a) >>> c)  & 0xFF00) +
      (((((s[i] & 0xFF0000) * b) + 
      ((s[i+1] & 0xFF0000) + 
      (s[i-1] & 0xFF0000) + 
      (s[i + width] & 0xFF0000) + 
      (s[i + yOffset] & 0xFF0000)) * a) >>> c)  & 0xFF0000) +
      0xFF000000; //ignores transparency

    // bottom edge (minus corner pixels)
    t[i + yOffset] = (((((s[i + yOffset] & 0xFF) * b) + 
      ((s[i - 1 + yOffset] & 0xFF) + 
      (s[i + 1 + yOffset] & 0xFF) +
      (s[i + yOffset - width] & 0xFF) +
      (s[i] & 0xFF)) * a) >>> c) & 0xFF) +
      (((((s[i + yOffset] & 0xFF00) * b) + 
      ((s[i - 1 + yOffset] & 0xFF00) + 
      (s[i + 1 + yOffset] & 0xFF00) +
      (s[i + yOffset - width] & 0xFF00) +
      (s[i] & 0xFF00)) * a) >>> c) & 0xFF00) +
      (((((s[i + yOffset] & 0xFF0000) * b) + 
      ((s[i - 1 + yOffset] & 0xFF0000) + 
      (s[i + 1 + yOffset] & 0xFF0000) +
      (s[i + yOffset - width] & 0xFF0000) +
      (s[i] & 0xFF0000)) * a) >>> c) & 0xFF0000) +
      0xFF000000;    
    
    // central square
    for (int j = 1; j < (height-1); ++j){
      yOffset = j*width;
      t[i + yOffset] = (((((s[i + yOffset] & 0xFF) * b) +
        ((s[i + 1 + yOffset] & 0xFF) +
        (s[i - 1 + yOffset] & 0xFF) +
        (s[i + yOffset + width] & 0xFF) +
        (s[i + yOffset - width] & 0xFF)) * a) >>> c) & 0xFF) +
        (((((s[i + yOffset] & 0xFF00) * b) +
        ((s[i + 1 + yOffset] & 0xFF00) +
        (s[i - 1 + yOffset] & 0xFF00) +
        (s[i + yOffset + width] & 0xFF00) +
        (s[i + yOffset - width] & 0xFF00)) * a) >>> c) & 0xFF00) +
        (((((s[i + yOffset] & 0xFF0000) * b) +
        ((s[i + 1 + yOffset] & 0xFF0000) +
        (s[i - 1 + yOffset] & 0xFF0000) +
        (s[i + yOffset + width] & 0xFF0000) +
        (s[i + yOffset - width] & 0xFF0000)) * a) >>> c) & 0xFF0000) +
        0xFF000000;
    }
  }
  
  // left and right edge (minus corner pixels)
  for (int j = 1; j < (height-1); ++j){
      yOffset = j*width;
      t[yOffset] = (((((s[yOffset] & 0xFF) * b) +
        ((s[yOffset + 1] & 0xFF) +
        (s[yOffset + width - 1] & 0xFF) +
        (s[yOffset + width] & 0xFF) +
        (s[yOffset - width] & 0xFF) ) * a) >>> c) & 0xFF) +
        (((((s[yOffset] & 0xFF00) * b) +
        ((s[yOffset + 1] & 0xFF00) +
        (s[yOffset + width - 1] & 0xFF00) +
        (s[yOffset + width] & 0xFF00) +
        (s[yOffset - width] & 0xFF00) ) * a) >>> c) & 0xFF00) +
        (((((s[yOffset] & 0xFF0000) * b) +
        ((s[yOffset + 1] & 0xFF0000) +
        (s[yOffset + width - 1] & 0xFF0000) +
        (s[yOffset + width] & 0xFF0000) +
        (s[yOffset - width] & 0xFF0000) ) * a) >>> c) & 0xFF0000) +
        0xFF000000;

      t[yOffset + width - 1] = (((((s[yOffset + width - 1] & 0xFF) * b) +
        ((s[yOffset] & 0xFF) +
        (s[yOffset + width - 2] & 0xFF) +
        (s[yOffset + (width<<1) - 1] & 0xFF) +
        (s[yOffset - 1] & 0xFF)) * a) >>> c) & 0xFF) +
        (((((s[yOffset + width - 1] & 0xFF00) * b) +
        ((s[yOffset] & 0xFF00) +
        (s[yOffset + width - 2] & 0xFF00) +
        (s[yOffset + (width<<1) - 1] & 0xFF00) +
        (s[yOffset - 1] & 0xFF00)) * a) >>> c) & 0xFF00) +
        (((((s[yOffset + width - 1] & 0xFF0000) * b) +
        ((s[yOffset] & 0xFF0000) +
        (s[yOffset + width - 2] & 0xFF0000) +
        (s[yOffset + (width<<1) - 1] & 0xFF0000) +
        (s[yOffset - 1] & 0xFF0000)) * a) >>> c) & 0xFF0000) +
        0xFF000000;
  }
  
  // corner pixels
  t[0] = (((((s[0] & 0xFF) * b) + 
    ((s[1] & 0xFF) + 
    (s[width-1] & 0xFF) + 
    (s[width] & 0xFF) + 
    (s[width*(height-1)] & 0xFF)) * a) >>> c)  & 0xFF) +
    (((((s[0] & 0xFF00) * b) + 
    ((s[1] & 0xFF00) + 
    (s[width-1] & 0xFF00) + 
    (s[width] & 0xFF00) + 
    (s[width*(height-1)] & 0xFF00)) * a) >>> c)  & 0xFF00) +
    (((((s[0] & 0xFF0000) * b) + 
    ((s[1] & 0xFF0000) + 
    (s[width-1] & 0xFF0000) + 
    (s[width] & 0xFF0000) + 
    (s[width*(height-1)] & 0xFF0000)) * a) >>> c)  & 0xFF0000) +
    0xFF000000;

  t[width - 1 ] = (((((s[width-1] & 0xFF) * b) + 
    ((s[width-2] & 0xFF) + 
    (s[0] & 0xFF) + 
    (s[(width<<1) - 1] & 0xFF) + 
    (s[width*height-1] & 0xFF) ) * a) >>> c) & 0xFF) +
    (((((s[width-1] & 0xFF00) * b) + 
    ((s[width-2] & 0xFF00) + 
    (s[0] & 0xFF00) + 
    (s[(width<<1) - 1] & 0xFF00) + 
    (s[width*height-1] & 0xFF00) ) * a) >>> c) & 0xFF00) +
    (((((s[width-1] & 0xFF0000) * b) + 
    ((s[width-2] & 0xFF0000) + 
    (s[0] & 0xFF0000) + 
    (s[(width<<1) - 1] & 0xFF0000) + 
    (s[width*height-1] & 0xFF0000) ) * a) >>> c) & 0xFF0000) +
    0xFF000000;

  t[width * height - 1] = (((((s[width*height-1] & 0xFF) * b) + 
    ((s[width-1] & 0xFF) + 
    (s[width*(height-1)-1] & 0xFF) + 
    (s[width*height-2] & 0xFF) + 
    (s[width*(height-1)] & 0xFF) ) * a) >>> c) & 0xFF) +
    (((((s[width*height-1] & 0xFF00) * b) + 
    ((s[width-1] & 0xFF00) + 
    (s[width*(height-1)-1] & 0xFF00) + 
    (s[width*height-2] & 0xFF00) + 
    (s[width*(height-1)] & 0xFF00) ) * a) >>> c) & 0xFF00) +
    (((((s[width*height-1] & 0xFF0000) * b) + 
    ((s[width-1] & 0xFF0000) + 
    (s[width*(height-1)-1] & 0xFF0000) + 
    (s[width*height-2] & 0xFF0000) + 
    (s[width*(height-1)] & 0xFF0000) ) * a) >>> c) & 0xFF0000) +
    0xFF000000;
  
  t[width *(height-1)] = (((((s[width*(height-1)] & 0xFF) * b) + 
    ((s[width*(height-1) + 1] & 0xFF) + 
    (s[width*height-1] & 0xFF) + 
    (s[width*(height-2)] & 0xFF) + 
    (s[0] & 0xFF) ) * a) >>> c) & 0xFF) +
    (((((s[width*(height-1)] & 0xFF00) * b) + 
    ((s[width*(height-1) + 1] & 0xFF00) + 
    (s[width*height-1] & 0xFF00) + 
    (s[width*(height-2)] & 0xFF00) + 
    (s[0] & 0xFF00) ) * a) >>> c) & 0xFF00) +
    (((((s[width*(height-1)] & 0xFF0000) * b) + 
    ((s[width*(height-1) + 1] & 0xFF0000) + 
    (s[width*height-1] & 0xFF0000) + 
    (s[width*(height-2)] & 0xFF0000) + 
    (s[0] & 0xFF0000) ) * a) >>> c) & 0xFF0000) +
    0xFF000000;
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "v8" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
