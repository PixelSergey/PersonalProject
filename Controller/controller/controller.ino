// Pin locations
const int pin_joyX = A0;
const int pin_joyY = A1; 
const int pin_joyZ = 12;
const int pin_btnA = 10;
const int pin_btnB = 9;
const int pin_btnX = 7;
const int pin_btnY = 8;
const int pin_btnStart = 11;
const int pin_on = 2;

const int deadzone = 5;

// Storage for values
int joyX = 0;
int joyY = 0;
int joyZ = 0;
int btnA = 0;
int btnB = 0;
int btnX = 0;
int btnY = 0;
int btnStart = 0;
int on = 0;

char msg[150];

void setup() {
  Serial.begin(9600);
  pinMode(pin_joyX, INPUT);
  pinMode(pin_joyY, INPUT);
  pinMode(pin_joyZ, INPUT);
  pinMode(pin_btnA, INPUT);
  pinMode(pin_btnB, INPUT);
  pinMode(pin_btnX, INPUT);
  pinMode(pin_btnY, INPUT);
  pinMode(pin_btnStart, INPUT);
  pinMode(pin_on, INPUT);
}

void loop() {
  if(!digitalRead(pin_on)){
    Serial.println("OFF");
    delay(3);
    return;
  }
  
  joyX = map(analogRead(pin_joyX), 0, 1023, 100, -100);
  joyY = map(analogRead(pin_joyY), 0, 1023, -100, 100);

  if(joyX < deadzone && joyX > -deadzone){ // X-axis deadzone
    joyX = 0;
  }
  if(joyY < deadzone && joyY > -deadzone){ // Y-axis deadzone
    joyY = 0;
  }
  
  joyZ = 1 - digitalRead(pin_joyZ);
  btnA = digitalRead(pin_btnA);
  btnB = digitalRead(pin_btnB);
  btnX = digitalRead(pin_btnX);
  btnY = digitalRead(pin_btnY);
  btnStart = digitalRead(pin_btnStart);

  // A horrendous and long string format to put all the inputs into a single JSON-formatted string
  sprintf(msg, "{\"joyX\":\"%d\",\"joyY\":\"%d\",\"joyZ\":\"%d\",\"A\":\"%d\",\"B\":\"%d\",\"X\":\"%d\",\"Y\":\"%d\",\"Start\":\"%d\"}", joyX, joyY, joyZ, btnA, btnB, btnX, btnY, btnStart);
  Serial.println(msg);

  delay(3); // Give sensors time to refresh
}













