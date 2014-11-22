// 0a0
// aba
// 0a0
// 4*a + b in total, >>> c
void shiftBlur(final int[] s, final int[] t, final int a, final int b, final int c){
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

