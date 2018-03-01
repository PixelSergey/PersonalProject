const int joyX = A0;
const int joyY = A1; 
const int joyD = 12;

int x = 0;
int y = 0;
int d = 0;

void setup() {
  Serial.begin(9600);
  pinMode(joyX, INPUT);
  pinMode(joyY, INPUT);
  pinMode(joyD, INPUT);
}

void loop() {
  x = map(analogRead(joyX), 0, 1023, -10, 10);
  y = map(analogRead(joyY), 0, 1023, -10, 10) + 1;
  d = 1 - digitalRead(joyD);
  
  char msg[50];
  sprintf(msg, "{\"joyX\":\"%d\",\"joyY\":\"%d\",\"joyD\":\"%d\"}", x, y, d);
  Serial.println(msg);
  
  delay(2);
}
