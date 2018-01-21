const int joyX = A0;
const int joyY = A1; 
const int joySw = 12;

int x = 0;
int y = 0;
int sw = 0;

void setup() {
  Serial.begin(9600);
  pinMode(joyX, INPUT);
  pinMode(joyY, INPUT);
  pinMode(joySw, INPUT);
}

void loop() {
  x = map(analogRead(joyX), 0, 1023, 0, 255);
  y = map(analogRead(joyY), 0, 1023, 0, 255);
  sw = digitalRead(joySw);

  Serial.print("X value: ");
  Serial.print(x);
  Serial.print("\t Y value: ");
  Serial.print(y);
  Serial.print("\t Pressed? ");
  Serial.println(1-sw);
  
  delay(2);
}
