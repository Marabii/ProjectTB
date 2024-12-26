/*************************************************************
   Required Libraries (Install via Arduino Library Manager):
   1) Adafruit GFX
   2) Adafruit SSD1306
   3) Adafruit NeoPixel
   4) WiFi (built-in on ESP32)
   5) HTTPClient (built-in on ESP32)
*************************************************************/

#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <Adafruit_NeoPixel.h>
#include <WiFi.h>
#include <HTTPClient.h>

// -------------------------
// OLED Configuration
// -------------------------
#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 32
#define OLED_RESET    -1  // Not used with Featherwing
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

// -------------------------
// Light Sensor Configuration
// -------------------------
#define LIGHT_SENSOR_PIN 33  // TEMT6000 output pin to GPIO 33 (ADC1)
int lightThreshold = 1000;  // Adjust this threshold based on your environment

// -------------------------
// WS2812B LED Configuration
// -------------------------
#define LED_PIN    5       // WS2812 data in
#define NUMPIXELS  1
Adafruit_NeoPixel pixels(NUMPIXELS, LED_PIN, NEO_GRB + NEO_KHZ800);

// -------------------------
// Buttons Configuration
// -------------------------
// Typically, Featherwing OLED’s A, B, C map to specific GPIOs (check your board docs):
#define BUTTON_A_PIN  15
#define BUTTON_B_PIN  32
#define BUTTON_C_PIN  14

// Additional button for "sending request":
#define REQUEST_BUTTON_PIN 13 // Connect a push-button between pin 13 and GND

// -------------------------
// Network / HTTP Configuration
// -------------------------
const char* ssid     = "Redmi Note 9 Pro";      // Your WiFi SSID
const char* password = "12345678";             // Your WiFi Password

// Replace with your Spring Boot server’s IP and port
const String serverBase = "http://192.168.255.144:8080/api/notes/takeDocuments/";

// -------------------------
// Password Management
// -------------------------
String typedPassword = "";
const int MAX_PASSWORD_LENGTH = 8; // Adjust as needed

// For demonstration, let's say the "correct" password is "ABC". 
// In practice, consider more secure methods for password handling.
String secretPassword = "ABC"; 

// -------------------------
// Timing Configuration
// -------------------------
unsigned long previousMillis = 0;
const unsigned long UPDATE_INTERVAL = 500; // Update sensor and screen every 500ms

// -------------------------
// Function Prototypes
// -------------------------
void displayText(String msg, uint8_t textSize = 1, int16_t x = 0, int16_t y = 0);
void updateDisplayAndSensor();
void checkPasswordButtons();
void appendLetterToPassword(String letter);
void handleTakeDocumentsRequest();
void setLedColor(uint32_t color);

// -------------------------
// Setup Function
// -------------------------
void setup() {
  // Initialize Serial for debugging
  Serial.begin(115200);
  Serial.println("\nInitializing...");

  // Initialize OLED
  if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
    Serial.println("SSD1306 allocation failed!");
    while (true) {} // Halt the program
  }
  display.clearDisplay();
  display.display(); // Ensure the display is cleared

  // Initialize NeoPixel
  pixels.begin();
  pixels.clear();
  pixels.show();

  // Initialize Buttons with internal pull-up resistors
  pinMode(BUTTON_A_PIN, INPUT_PULLUP);
  pinMode(BUTTON_B_PIN, INPUT_PULLUP);
  pinMode(BUTTON_C_PIN, INPUT_PULLUP);
  pinMode(REQUEST_BUTTON_PIN, INPUT_PULLUP);

  // Initialize Wi-Fi
  WiFi.begin(ssid, password);
  Serial.println("Connecting to WiFi...");
  displayText("Connecting to WiFi...", 1, 0, 0); // Optional: Show connection status on OLED
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected!");
  displayText("WiFi connected!", 1, 0, 0); // Optional: Update OLED on successful connection
  delay(1000); // Brief pause to show the message

  // Initial display text
  displayText("System ready...", 1, 0, 0);
}

// -------------------------
// Loop Function
// -------------------------
void loop() {
  unsigned long currentMillis = millis();

  // 1) Check for button presses (A, B, C) to build the typed password
  checkPasswordButtons();

  // 2) Check if the user pressed the "request" button
  if (digitalRead(REQUEST_BUTTON_PIN) == LOW) {
    // Debounce delay
    delay(200); 
    if (digitalRead(REQUEST_BUTTON_PIN) == LOW) {
      // Make HTTP request to local server
      handleTakeDocumentsRequest();
    }
  }

  // 3) Update Display & Sensor every 500ms
  if (currentMillis - previousMillis >= UPDATE_INTERVAL) {
    previousMillis = currentMillis;
    updateDisplayAndSensor();
  }
}

// ---------------------------------------------------------
// Helper Functions
// ---------------------------------------------------------

// Display a short text message on the OLED (clears display first).
// textSize of 1 is small, 2 is larger, etc.
void displayText(String msg, uint8_t textSize, int16_t x, int16_t y) {
  display.clearDisplay();
  display.setTextSize(textSize);
  display.setTextColor(SSD1306_WHITE);
  display.setCursor(x, y);
  display.println(msg);
  display.display();
}

// Reads the light sensor and displays "documents are placed" or "no documents detected".
// Also shows the typed password on the second line.
void updateDisplayAndSensor() {
  int lightValue = analogRead(LIGHT_SENSOR_PIN);

  // Determine if light sensor is covered
  String docStatus = (lightValue < lightThreshold) ? "documents are placed" : "no documents detected";

  // Clear screen and display information
  display.clearDisplay();
  display.setTextSize(1);
  display.setTextColor(SSD1306_WHITE);

  // First line: Document status
  display.setCursor(0, 0);
  display.println(docStatus);

  // Second line: Show typed password
  display.setCursor(0, 10);
  display.setTextSize(1); // Adjusted to fit the screen
  display.print("PW: ");
  display.println(typedPassword);

  // Third line: Light value (optional for debugging)
  display.setCursor(0, 20);
  display.setTextSize(1);
  display.print("Light: ");
  display.println(lightValue);

  display.display();

  // Debug info
  Serial.print("Light: ");
  Serial.print(lightValue);
  Serial.print(" | Status: ");
  Serial.println(docStatus);
}

// Check which of the A, B, C buttons is pressed and append the corresponding letter
void checkPasswordButtons() {
  if (digitalRead(BUTTON_A_PIN) == LOW) {
    delay(150); // Debounce
    if (digitalRead(BUTTON_A_PIN) == LOW) {
      appendLetterToPassword("A");
    }
  }
  if (digitalRead(BUTTON_B_PIN) == LOW) {
    delay(150); 
    if (digitalRead(BUTTON_B_PIN) == LOW) {
      appendLetterToPassword("B");
    }
  }
  if (digitalRead(BUTTON_C_PIN) == LOW) {
    delay(150);
    if (digitalRead(BUTTON_C_PIN) == LOW) {
      appendLetterToPassword("C");
    }
  }
}

// Add a letter to typedPassword, but cap at MAX_PASSWORD_LENGTH
void appendLetterToPassword(String letter) {
  if (typedPassword.length() < MAX_PASSWORD_LENGTH) {
    typedPassword += letter;
    Serial.println("Password typed so far: " + typedPassword);
  }
}

// This function handles the request to the local Spring Boot server
// If successful (HTTP 200), turn LED green and display "Take documents" message
// Otherwise, turn LED red and display an error message
void handleTakeDocumentsRequest() {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("WiFi not connected! Cannot send request.");
    setLedColor(pixels.Color(255, 0, 0)); // Red
    displayText("WiFi error!", 1, 0, 0);
    delay(5000); // Wait 5 seconds
    displayText("System ready...", 1, 0, 0);
    return;
  }

  // Construct the full URL with the secretCode as a path variable
  String fullUrl = serverBase + typedPassword;
  Serial.println("Full URL: " + fullUrl); // For debugging

  HTTPClient http;
  http.begin(fullUrl);  // Initialize the HTTPClient with the full URL

  // Optional: Remove or adjust headers if not needed
  // http.addHeader("Content-Type", "application/json"); // Not necessary if no body

  // Send the PUT request without a payload
  int httpResponseCode = http.PUT(""); // Empty payload

  // Read the response body
  String responseBody = http.getString();
  Serial.println("Response Body: " + responseBody);

  // Check the response code
  if (httpResponseCode > 0) { // Check for valid response
    if (httpResponseCode == 200) {
      Serial.println("Server responded OK! Documents can be taken.");
      setLedColor(pixels.Color(0, 255, 0)); // Green
      displayText("Password correct", 1, 0, 0);
      delay(5000); // Display message for 5 seconds
      displayText("System ready...", 1, 0, 0);
    } else {
      Serial.print("Server error: ");
      Serial.println(httpResponseCode);
      setLedColor(pixels.Color(255, 0, 0)); // Red
      displayText("Password incorrect", 1, 0, 0);
      delay(5000); // Display message for 5 seconds
      displayText("System ready...", 1, 0, 0);
    }
  } else {
    Serial.print("HTTP Request failed, error: ");
    Serial.println(http.errorToString(httpResponseCode).c_str());
    setLedColor(pixels.Color(255, 0, 0)); // Red
    displayText("HTTP Error", 1, 0, 0);
    delay(5000); // Display message for 5 seconds
    displayText("System ready...", 1, 0, 0);
  }

  // Reset the typedPassword after sending the request
  typedPassword = "";

  http.end(); // Close the connection
}

// Simplified helper for setting the LED color
void setLedColor(uint32_t color) {
  pixels.setPixelColor(0, color);
  pixels.show();
}
