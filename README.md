Colorful History - A Digital Portrait

Created by Amanda Grace Wall for LMC 2700 at Georgia Tech

NOTE: Please give the application several minutes to load the data from the DPLA.
Don't have the time to wait? Watch the 30 second video to get a feel for the project.

Out of all historic artifacts, posters are my favorite. What did a society care enough about to slap on a sheet of paper and spread around town? What did the designers value aesthetically at the time? I believe posters provide one of the best quick glances at a society and its values. So I found a collection in the DPLA database that had a wide, but manageable, assortment of posters from the past 100 years: the Cooper Hewitt Design Museum. I wanted to create an environment where the user can explore the content and color scheme of each poster individually but also explore a wider overview of the poster colors pixel by pixel, so my project has two modes.

Both modes work by analyzing the colors in the image pulled from the DPLA. The images are pulled from the thumbnails of the result, loaded as a PImage, and then analyzed by pixel to determine the number of pixels per hue. This data, as well as saturation and brightness values, are kept in arrays that are used to determine the three most dominant colors in the image. The differences in hue between these colors is then used to determine their color relationship. The majority of this needs to be done during setup, so this program takes a few minutes before loading to process the hundreds of images.

Spectrum mode shows how many pixels of each hue there are from all of the posters of the selected time period. The slider sets a year between 1918 and 2013, or the see all years button lets the user view them all at once. With the adjustment of the slider, the bars change in height (height = pixels of a color / number of posters in the set). Upon hover, the bar will display any dominant shades of that hue found in the poster set. To explore the shades and color relationships more, the user can click the see posters button and toggle to the next mode.

Poster mode shows dot representations of each poster, colored with the three dominant colors in the respective poster. The user can explore the dots by moving the cursor to the edges of the screen. The slider is fully functional in this mode also, displaying and hiding dots as the years change. Upon hover of a dot, a thumbnail of the poster displays as well as the color relationship. The color relationship determination is not as accurate as I hoped it would be, since some accent colors are not among the most represented hues, but overall I’m proud of the result.
