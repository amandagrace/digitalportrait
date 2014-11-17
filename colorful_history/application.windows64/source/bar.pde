class bar {
  int totalPixels;
  int hue;
  int barHeight;
  color c;
  color[] popularShades;
  boolean over = false;
 
 bar(int hue, int year) {
   this.hue = hue;
   c = color(hue, 100, 100);
   setHeight(hue, year);
 }
 
 void setHeight(int hue, int year) {
   totalPixels = 0;
   popularShades = new color[10];
   int j = 0;
   if (year == 0) {
     for (int i = 0; i < posters.length; i++) {
       if (posters[i] != null) {
         totalPixels += posters[i].hues[hue];
         if (Math.round(hue(posters[i].dominantColor)) == hue && j < 10) {
             popularShades[j] = posters[i].dominantColor;
             j++; 
           }
           if (Math.round(hue(posters[i].subdominantColor)) == hue && j < 10) {
               popularShades[j] = posters[i].subdominantColor;
               j++;
           }
           if (Math.round(hue(posters[i].accentColor)) == hue && j < 10) {
               popularShades[j] = posters[i].accentColor;
               j++;
           }
         }
     }
     barHeight = totalPixels / posters.length;
   } else {
       int posterCount = 0;
       for (int i = 0; i < posters.length; i++) {
         if (posters[i] != null && posters[i].date == year)  {
           totalPixels += posters[i].hues[hue];
           if (Math.round(hue(posters[i].dominantColor)) == hue && j < 10) {
               popularShades[j] = posters[i].dominantColor;
               j++;
           }
           if (Math.round(hue(posters[i].subdominantColor)) == hue && j < 10) {
               popularShades[j] = posters[i].subdominantColor;
               j++;
           }
           if (Math.round(hue(posters[i].accentColor)) == hue && j < 10) {
               popularShades[j] = posters[i].accentColor;
               j++;
           }
           posterCount++;
         }
       }
       if (posterCount > 0) {
         barHeight = totalPixels / posterCount;
       } else {
         barHeight = 0;
       }
   }
   if (barHeight > height) {
     barHeight = height;
   }
 }
 
 void checkover(int px, int py) {
    if (px >= hue * 3 && px < (hue * 3 + 3) && py > (height - barHeight) && py <= height) {
      over = true;
    } else {
      over = false;
    }
  }
  
  void displayShades(int px, int py) {
    int xLocation = px;
    int yLocation = py;
    if (py < 30) {
      yLocation += 20;
    } else {
      yLocation -= 20;
    }
    
    for (int i = 0; i < popularShades.length; i++) {
      if (popularShades[i] != 0) {
        fill(popularShades[i]);
        stroke(hue, 100, 100);
        strokeWeight(2);
        rectMode(CENTER);
        rect(xLocation, yLocation, 30, 30);
        if (px > width/2) {
          xLocation -= 34;
        } else {
          xLocation += 34;
        }
      }
    }
    rectMode(CORNER);  
    noStroke();
  } 
  
  void display() {
  }
}
 
