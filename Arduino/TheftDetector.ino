int irSensor = 2;
int buzzer = 3;

void setup() {
  pinMode(irSensor, INPUT);
  pinMode(buzzer, OUTPUT);
  Serial.begin(9600);
}

void loop() {
  int motion = digitalRead(irSensor);

  if (motion == LOW) {
    Serial.println("ALERT");
    digitalWrite(buzzer, HIGH);
    delay(1000);
    digitalWrite(buzzer, LOW);
  } else {
    Serial.println("SAFE");
  }

  delay(500);
}