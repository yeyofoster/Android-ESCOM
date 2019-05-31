int pinTemp = 0;
int pinVolt = 1;
int pinHall = 2;
int pinRPM = 3;  //pin digital 3 (tambien es el pin de interrupcion1);

volatile int contador = 0;

void setup() {
  Serial.begin(9600);
  attachInterrupt(digitalPinToInterrupt(pinRPM), interrupcion, RISING);
}

void loop() {
  String datos = String (lecturaTemperatura()) + " C°\r\n" + String (lecturaVoltaje()) + " V\r\n" + String (lecturaHall()) + " V\r\n" + String (lecturaRPM()) + " RPM\r\n";
  Serial.println(datos);
  contador = 0;
  delay(999);
}

float lecturaTemperatura() {
  float temp;
  for (int i = 0; i < 10; i++) {
    temp = (float)analogRead(pinTemp);
    temp = (float) (temp * 500) / 1024;
  }
  return temp - 10;
}

float lecturaVoltaje(){
  float volt = (float) analogRead(pinVolt);
  volt = (5*volt)/1024;
  return volt;
}

float lecturaHall(){
  float Hall = (float) analogRead(pinHall);
  Hall = (5 * Hall) / 1023;
  return Hall;
}

int lecturaRPM(){
  return (contador*20);
}

void interrupcion(){
  contador++;
}
