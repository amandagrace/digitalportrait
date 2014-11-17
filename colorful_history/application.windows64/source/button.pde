class button {
  int x, y, _x, _y;
  boolean over;
  
  button(int x, int y, int _x, int _y) {
    this.x = x;
    this.y = y;
    this._x = _x;
    this._y = _y;
  }
  
  void checkover(int px, int py) {
    if (px > x && px < x+_x && py > y && py < y+_y) {
      over = true;
    } else {
      over = false;
    }
  }
  
}
