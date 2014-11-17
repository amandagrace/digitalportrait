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


void setup() {
  size(1280, 720);
  colorMode(HSB, 359, 100, 100);
  bars = new bar[360];
  
  //set UI stuff
  seeAll = new button(1100, 20, 160, 40);  
  seePosters = new button(1100, 70, 160, 40);
  yearSlider = new slider(1150, 150, 60, 20);
  
  System.out.println("LOADING IMAGE DATA...");
  System.out.println("There's a lot of it – please be patient.");
  System.out.println("This should take about 5 minutes.");
  
  loadData(); 
  
  //set bars
  for(int i = 0; i < bars.length; i++) {
    bars[i] = new bar(i, year);
  }
}

void draw() {
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

void loadData() {
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

JSONArray search() {
   String queryURL = "http://api.dp.la/v2/items?q=" + searchTerm + "&" 
   + filter + "&api_key=" + apiKey + "&page_size=" + maxResults;
   
   JSONObject dplaData = loadJSONObject(queryURL);
   JSONArray results = dplaData.getJSONArray("docs");
   
   return results;
 }

void mousePressed() {
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

void mouseDragged() {
  if (yearSlider.locked) {
    if (mouseY > 140 && mouseY < 600) {
      yearSlider.y = mouseY;
    }
    year = (int) (yearSlider.y / 4.8) + 1889;
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

void mouseReleased() {
    yearSlider.locked = false;
}
