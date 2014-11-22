//Uses fast blur from http://forum.processing.org/one/topic/fast-blurring.html

int columns = 8;
int rows = 5;
float sphereBaseRadius = 200.0;
int time = 0;

float sinkX = 0;
float sinkY = 0;

float sink2X = 0;
float sink2Y = 0;

int[] screenBuf;
int numPixels;

void setup() {
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

void draw() {
  time += 1;
  noStroke();

  float dirY = (mouseY / float(height) - 0.5) * 2;
  float dirX = (mouseX / float(width) - 0.5) * 2;
  
  directionalLight(204, 204, 204, -dirX, -dirY, -1); 
  pointLight(255, 0, 0, mouseX, mouseY, 800);
  pointLight(255, 255, 0, mouseX, mouseY, 0);

  sinkX = width/2 + width/2 * cos(time*0.01); //mouseX;
  sinkY = height/2 + height/2 * sin(time*0.01);//mouseY;
  
  // sink2 is reflected across the origin from sink1
  sink2X = width/2 - width/2 * cos(time*0.01);
  sink2Y = width/2 - height/2 * sin(time*0.01);
    
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
      translate(0,0,400-0.01*(distToSink*distToSink)+0.01*(distToSink2*distToSink2));
      sphere(sphereBaseRadius/max(rows,columns));
      popMatrix();
      
    }
    
    popMatrix();
  }
  
//  filter(BLUR, 3);  <-- builtin is 10x slower than customized blur code
  loadPixels();
  shiftBlur3(pixels, screenBuf);
  arrayCopy(screenBuf, pixels);
  updatePixels();
  
}

