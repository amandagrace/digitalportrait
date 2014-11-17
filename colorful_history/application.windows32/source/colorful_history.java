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

public class colorful_history extends PApplet {

/* 
 * A Colorful Past
 * by Amanda Grace Wall
 * LMC 2700 Project 3
 *
 * Wrapper adapted from DPLA_APIwrapper provided by Yanni Loukissas, based on code by William Karavites
 * wkaravites@gmail.com
 * The MIT License (MIT)
 * Copyright (c) <2013> <William Karavites>
 */

Integer year = 0;
bar[] bars;
button seeAll;
button seePosters;
slider yearSlider;
boolean barMode = true;

Poster[] posters;
JSONArray json;
 
private String searchTerm = "poster";
private String filter = "sourceResource.collection.title=Cooper-Hewitt%2C+National+Design+Museum";
private String apiKey = "c6653576bfb702d4ca0505ab1f538b0c";
private int maxResults = 700;
private boolean first = true;
private boolean loading = false;

public void setup() {
  size(1280, 720);
  colorMode(HSB, 359, 100, 100);
  bars = new bar[360];
  
  //set UI stuff
  seeAll = new button(1100, 20, 160, 40);  
  seePosters = new button(1100, 70, 160, 40);
  yearSlider = new slider(1150, 150, 60, 20);
}

public void draw() {
  if (first) {
      background(0, 0, 30);
      fill(0, 0, 90);    
      textSize(20);
      textAlign(CENTER);
      text("Loading data. This may take several minutes.", 0, 360, 1280, 100);
      loading = true;
      first = false;
  } else if (loading) {
      //load data
      loadData();
      loading = false;
      
      //set bars
      for(int i = 0; i < bars.length; i++) {
      bars[i] = new bar(i, year);
      }
  } else {
    background(0, 0, 90);
  
  if (barMode) {
  //draw bars
  for(int i = 0; i < bars.length; i++) {
    noStroke();
    fill(bars[i].c);
    rect(i*3, height - bars[i].barHeight, 3, bars[i].barHeight);
  }
  
  //adjust bars
  for (int i = 0; i < bars.length; i++) {
    bars[i].checkover(mouseX, mouseY);
    if (bars[i].over) {
      fill(hue(bars[i].c), 20, 100);
      rect(i*3, height - bars[i].barHeight, 3, bars[i].barHeight);
      bars[i].displayShades(mouseX, mouseY);
    }
  }
  } else {
    for (int i = 0; i < posters.length; i++) {  
      if (year == 0 || posters[i].date == year) {
        posters[i].display();
        posters[i].checkover(mouseX, mouseY);
      } 
    }
  }
  
  //draw control panel
  fill(0, 0, 30);
  rect(1080, 0, 200, height);
  
  //draw top panel
  fill(0, 0, 30, 100);
  rect(0, 0, 1080, 80);
  
  
  if (!barMode) {
    for (int i = 0; i < posters.length; i++) {
      if (posters[i].over) {
        posters[i].displayRelationship();
      }
    }
  }
  
  //set text size and alignment  
  textSize(20);
  textAlign(CENTER);
  
  //if over see all button...
  seeAll.checkover(mouseX, mouseY);
  if (seeAll.over) {
    strokeWeight(5);
    stroke(0, 0, 90);
  }
  //draw see all button
  fill(0, 0, 70);
  rect(seeAll.x, seeAll.y, seeAll._x, seeAll._y);
  noStroke();
  fill(0, 0, 90);
  text("See All Years", 1110, 30, 140, 40);
  
  //if over see posters button...
  seePosters.checkover(mouseX, mouseY);
  if (seePosters.over) {
    strokeWeight(5);
    stroke(0, 0, 90);
  }
  //draw see posters button
  fill (0, 0, 70);
  rect(seePosters.x, seePosters.y, seePosters._x, seePosters._y);
  noStroke();
  fill(0, 0, 90);
  if (barMode) {
    text("See Posters", 1110, 80, 140, 40);
  } else {
    text("See Spectrum", 1110, 80, 140, 40);
  }
  
  //create skinny bar for slider
  fill(0, 0, 70);
  rect(1170, 150, 20, 460);
  //if over year slider...
  yearSlider.checkover(mouseX, mouseY);
  if (yearSlider.over || yearSlider.locked) {
    strokeWeight(3);
    stroke(0, 0, 70);
  }
  //draw year slider
  fill(0, 0, 90);
  rect(yearSlider.x, yearSlider.y, yearSlider._x, yearSlider._y);
  noStroke();
  
  //draw control panel text
  fill(0, 0, 90);
  textSize(9);
  text("Hues extracted from posters in the Cooper Hewitt Design Museum collection. Hover to see shades.", 1100, 650, 160, 60);
  
  //draw title
  textSize(40);
  fill(0, 0, 90);
  textAlign(LEFT);
  text("A Colorful History", 20, 20, 500, 80);
  textAlign(RIGHT);
  if (year == 0) {
    text("1918-2013", 400, 20, 660, 80);
  } else {
    text(year.toString(), 400, 20, 660, 80);
  }
}
}

public void loadData() {
  JSONArray results = search();
  posters = new Poster[results.size()];
  for (int i = 0; i < results.size(); i++) {
    JSONObject o = results.getJSONObject(i);
    Poster p = new Poster(o);
    posters[i] = p;
    p.displayImg = loadImage(o.getString("object") + "?api_key="
    + apiKey, "jpg");
    if (p.displayImg != null && p.displayImg.width != -1) {
      if (p.displayImg.width > 200) {
      p.displayImg.resize(200, 0);
      }
    }
    posters[i].findColors();
    posters[i].displayTxt = posters[i].colorRelationship();
  }
}

public JSONArray search() {
   String queryURL = "http://api.dp.la/v2/items?q=" + searchTerm + "&" 
   + filter + "&api_key=" + apiKey + "&page_size=" + maxResults;
   
   JSONObject dplaData = loadJSONObject(queryURL);
   JSONArray results = dplaData.getJSONArray("docs");
   
   return results;
 }

public void mousePressed() {
  if (seeAll.over) {
    year = 0;
    for (int i = 0; i < bars.length; i++) {
      bars[i].setHeight(i, year);
    }
  }
  if (seePosters.over) {
    if (barMode) {
      barMode = false;
    } else {
      barMode = true;
    }
  }
  if (yearSlider.over) {
    yearSlider.locked = true;
  }
}

public void mouseDragged() {
  if (yearSlider.locked) {
    if (mouseY > 140 && mouseY < 600) {
      yearSlider.y = mouseY;
    }
    year = (int) (yearSlider.y / 4.8f) + 1889;
    if (year < 1918) {
      year = 1918;
    }
    if (year > 2013) {
      year = 2013;
    }
    for (int i = 0; i < bars.length; i++) {
      bars[i].setHeight(i, year);
    }
  }
}

public void mouseReleased() {
    yearSlider.locked = false;
}
class Poster {
  float x, y, d;
  PImage displayImg;
  String displayTxt;
  JSONObject object;
  int[] hues;
  float[] sats;
  float[] brights;
  int date;
  int dominantColor;
  int subdominantColor;
  int accentColor;
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
         int pixel = displayImg.pixels[i];
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
    int one = color(0, 0, 0);
    int two = color(0, 0, 0);
    int three = color(0, 0, 0);
    for (int i = 0; i < hues.length; i++) {
      int newcomer = color(i, sats[i]/hues[i], brights[i]/hues[i]);
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
  
  private boolean differentEnough(int a, int b) {
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
  
  public void checkover(float px, float py) {
    float m = dist(px, py, x, y);
    if (m < d/2) {
      over = true;
    } else {
      over = false;
    }
  }
  
  public void display() {
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

public void displayRelationship() {
      fill(0, 0, 90);
      textSize(20);
      textAlign(CENTER);
      text(displayTxt, 420, 40, 180, 40);
}

public String colorRelationship() {
   int a = dominantColor;
   int b = subdominantColor;
   int c = accentColor;
   if (b < a) {
     int temp = a;
     a = b;
     b = temp;
   }
   if (b > c) {
     int temp = c;
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

class bar {
  int totalPixels;
  int hue;
  int barHeight;
  int c;
  int[] popularShades;
  boolean over = false;
 
 bar(int hue, int year) {
   this.hue = hue;
   c = color(hue, 100, 100);
   setHeight(hue, year);
 }
 
 public void setHeight(int hue, int year) {
   totalPixels = 0;
   popularShades = new int[10];
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
 
 public void checkover(int px, int py) {
    if (px >= hue * 3 && px < (hue * 3 + 3) && py > (height - barHeight) && py <= height) {
      over = true;
    } else {
      over = false;
    }
  }
  
  public void displayShades(int px, int py) {
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
  
  public void display() {
  }
}
 
class button {
  int x, y, _x, _y;
  boolean over;
  
  button(int x, int y, int _x, int _y) {
    this.x = x;
    this.y = y;
    this._x = _x;
    this._y = _y;
  }
  
  public void checkover(int px, int py) {
    if (px > x && px < x+_x && py > y && py < y+_y) {
      over = true;
    } else {
      over = false;
    }
  }
  
}
class slider {
  int x, y, _x, _y;
  boolean over = false;
  boolean locked = false;
  
  slider(int x, int y, int _x, int _y) {
    this.x = x;
    this.y = y;
    this._x = _x;
    this._y = _y;
  }
  
  public void checkover(int px, int py) {
    if (px > x && px < x+_x && py > y && py < y+_y) {
      over = true;
    } else {
      over = false;
    }
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "colorful_history" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
