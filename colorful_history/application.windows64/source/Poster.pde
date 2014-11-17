class Poster {
  float x, y, d;
  PImage displayImg;
  String displayTxt;
  JSONObject object;
  int[] hues;
  float[] sats;
  float[] brights;
  int date;
  color dominantColor;
  color subdominantColor;
  color accentColor;
  boolean over = false;
  
  public Poster(JSONObject j) {
    x = random(-200, width + 200);
    y = random(-200, height + 200);
    d = 30;
    object = j;
    try {
      JSONObject dateObject = object.getJSONObject("sourceResource").getJSONObject("date");
      String d = dateObject.getString("begin").substring(0, 4);
      date = Integer.parseInt(d);
    } catch (Exception e) {
      date = 0;
    }
  }
  
  public void findColors() {
    hues = new int[360];
    sats = new float[360];
    brights = new float[360];
    if(displayImg != null && displayImg.width != -1) {
      displayImg.loadPixels();
      for (int i = 0; i < displayImg.width * displayImg.height; i++) {
         color pixel = displayImg.pixels[i];
         int pixelHue = Math.round(hue(pixel));
         float saturation = saturation(pixel);
         float brightness = brightness(pixel);
         if (saturation > 5) {
           hues[pixelHue]++;   
           sats[pixelHue] += saturation;
           brights[pixelHue] += brightness;
         }
      }
      findDominantColors();
    } 
  }
  
  /**
   * This method determines the dominant colors in the image
   * Based on code by Cate Huston
   * http://www.catehuston.com/blog/2013/08/26/extracting-the-dominant-color-from-an-image-in-processing/
   */
  private void findDominantColors() {
    int most = 0; //pixel count of the color with the most pixels
    int secondMost = 0; //pixel count of the color with the second-most pixels
    int thirdMost = 0; //pixel count of the color with the third-most pixels
    int mainHue = 0; //the hue value of the color with the most pixels
    int secondHue = 0; //the hue value of the color with the second-most pixels
    int thirdHue = 0; //the hue value of the color with the third-most pixels
    color one = color(0, 0, 0);
    color two = color(0, 0, 0);
    color three = color(0, 0, 0);
    for (int i = 0; i < hues.length; i++) {
      color newcomer = color(i, sats[i]/hues[i], brights[i]/hues[i]);
      if (differentEnough(newcomer, one) && differentEnough(newcomer, two) &&
      differentEnough(newcomer, three)) {
      if (hues[i] > most) {
          thirdMost = secondMost;
          thirdHue = secondHue;
          three = two;
          secondMost = most;
          secondHue = mainHue;
          two = one;
          most = hues[i];
          mainHue = i;
          one = newcomer;
          } else if (hues[i] > secondMost) {
            thirdMost = secondMost;
            thirdHue = secondHue;
            three = two;
            secondMost = hues[i];
            secondHue = i;
            two = newcomer;
          } else if (hues[i] > thirdMost) {
            thirdMost = hues[i];
            thirdHue = i;
            three = newcomer;
          }
      }
     }
     dominantColor = one;
     subdominantColor = two;
     accentColor = three;
  }
  
  private boolean differentEnough(color a, color b) {
    if (a == b) {
      return false;
    }
    if (abs(hue(a) - hue(b)) < 10 && abs(saturation(a) - hue(b)) < 40 &&
    abs(brightness(a) - brightness(b)) < 20) {
      return false;
    }
    if (brightness(a) < 10 && brightness(b) < 10) {
      return false;
    }
    return true;      
  }
  
  void checkover(float px, float py) {
    float m = dist(px, py, x, y);
    if (m < d/2) {
      over = true;
    } else {
      over = false;
    }
  }
  
  void display() {
    if (displayImg != null) {
    noStroke();
    fill(dominantColor);
    ellipse(x, y, d, d);
    fill(subdominantColor);
    ellipse(x, y, 2*d/3, 2*d/3);
    fill(accentColor);
    ellipse(x, y, d/3, d/3);
    
    if (over) {
      imageMode(CENTER);
      image(displayImg, x, y);
    }
    if(mouseX > (width - 250) && mouseX < (width - 200)) {
      if (x < -200) {
        x = width;
      } else {
        x--;
      }
    } else if (mouseX < 50) {
      if (x > width) {
        x = -200;
      } else {
        x++;
      }
    }
    if(mouseY > (height - 50)) {
      if (y < -200) {
        y = height + 200;
      } else {
        y--;
      }
    } else if (mouseY < 50) {
      if (y > height + 200) {
        y = - 200;
      } else {
        y++;
      }
    }
  }  
}

void displayRelationship() {
      fill(0, 0, 90);
      textSize(20);
      textAlign(CENTER);
      text(displayTxt, 420, 40, 180, 40);
}

String colorRelationship() {
   color a = dominantColor;
   color b = subdominantColor;
   color c = accentColor;
   if (b < a) {
     color temp = a;
     a = b;
     b = temp;
   }
   if (b > c) {
     color temp = c;
     c = b;
     b = temp;
   }
   
   float abDiff = abs(hue(a) - hue(b));
   float bcDiff = abs(hue(b) - hue(c));
   float bigDiff;
   float smallDiff;
   if (abDiff < bcDiff) {
     bigDiff = bcDiff;
     smallDiff = abDiff;
   } else {
     bigDiff = abDiff;
     smallDiff = bcDiff;
   }
   
   if (smallDiff <= 20) {
     if (bigDiff <= 20) {
       return "Monochromatic";
     }
     if (bigDiff <= 90) {
       return "Analogous";
     }
     if (bigDiff >= 140 && bigDiff <= 200) {
       return "Complementary";
     }
   }
   if (smallDiff <= 90) {
     if (bigDiff <= 90) {
       return "Analogous";
     }
     if (bigDiff >= 140 && bigDiff <= 200) {
       return "Split Complementary";
     }
   }
   return "Other";
 }
}

