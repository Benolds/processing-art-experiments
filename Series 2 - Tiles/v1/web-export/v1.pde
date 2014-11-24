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

void setup() {
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

void draw() {
  time++;
  ttime++;
  if (ttime > 100) {
    ttime -= 100;
  }

//  ttime = 50 + 50 * sin(float(time)/100.0);
  
  for(int r = 0; r < rows; r++) {
    for(int c = 0; c < cols; c++) {
      int shade = 255;
      if ((r+c) % ttime == 0){
        shade = 100;
      }
      fill(shade);
      rect(gridMargin + c*sWidth, gridMargin + r*sHeight, sWidth, sHeight);
    } 
  }
  
}

