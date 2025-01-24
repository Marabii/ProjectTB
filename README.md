# StudiTrade: Platform for Selling Courses and Revision Notes

StudiTrade is an innovative platform designed to empower students by enabling them to buy and sell courses and revision notes seamlessly. Inspired by platforms like [StuDocu](https://www.studocu.com/fr/home), StudiTrade integrates a website, a mobile application, and an embedded system to facilitate both digital and physical transactions of educational materials.

![Platform Overview](https://github.com/user-attachments/assets/877d9189-1082-455e-93b8-be81457fc159)


## Table of Contents

- [Features](#features)
- [Video Tutorials](#video-tutorials)
- [Web Component](#web-component)
- [Backend Initialization](#backend-initialization)
- [Frontend Initialization](#frontend-initialization)
- [Using the Website](#using-the-website)
- [Mobile Application](#mobile-application)
- [Initialization](#mobile-application-initialization)
- [Using the Application](#using-the-application)
- [Embedded System](#embedded-system)
- [System Components](#system-components)
- [Initialization](#embedded-system-initialization)
- [Using the Embedded System](#using-the-embedded-system)
- [Contact](#contact)
- [Authors](#authors)

## Features
0
- **Buy and Sell Courses:** Seamlessly purchase or list courses and revision notes.
- **Multi-Platform Support:** Access StudiTrade via web, mobile, and physical embedded systems.
- **Secure Transactions:** Integrated with Stripe for secure payment processing.
- **User-Friendly Interface:** Intuitive design for easy navigation and usage.
- **Favorites and Personal Notes:** Save your preferred documents and manage your uploads efficiently.

## Video Tutorials

A comprehensive set of videos is available to guide you through using the different interfaces of StudiTrade. Access them here:

[![Video Tutorials](path/to/video-thumbnail.png)](https://drive.google.com/drive/folders/1UCVssxO8sASxBBnHP7m6Lw6X-T_vOdLr)

## Web Component

Built with **Vue.js** for the frontend and **Spring Boot** for the backend, the web component of StudiTrade offers a robust and scalable platform for managing educational content.

### Backend Initialization

1. **Open Backend Folder:**
- Open the **Backend** folder in [Visual Studio Code](https://code.visualstudio.com/) or [IntelliJ IDEA](https://www.jetbrains.com/idea/).

![Opening Backend Folder](https://github.com/user-attachments/assets/69429fd2-813b-40f1-8ab7-0af6554bb5f6)


2. **Configure Secrets:**
- The backend requires sensitive credentials:
   - **Google Cloud Storage Credentials:** `lofty-optics-427311-d6-e7d240f5d04c.json`
   - **Application Properties:** `application.properties` containing over 50 credentials for email management, Spring Security, database connections, etc.
- **Note:** Due to security reasons, these files are not uploaded to GitHub. If you are an employer or educator and need access, please contact [minehamza97@gmail.com](mailto:minehamza97@gmail.com) to receive the necessary credentials.

3. **Run Backend:**
- Navigate to the **ProjetTbApplication** file.
- Click on the **Run** button or type: mvn spring-boot:run in the terminal.

4. **Enable Payments:**
- The payment system relies on webhooks to update payment statuses and send confirmation emails.
- To simulate webhooks locally, use the [Stripe CLI](https://stripe.com/docs/stripe-cli).
- Update the `application.properties` file with your own Stripe credentials:
   - `stripe.secretKey`
   - `stripe.webhookKey`

**Note:** The backend setup is complex. If you encounter issues, contact me at [minehamza97@gmail.com](mailto:minehamza97@gmail.com) for assistance.

### Frontend Initialization

1. **Open Frontend Folder:**
- Open the **Frontend** folder in [Visual Studio Code](https://code.visualstudio.com/) or [IntelliJ IDEA](https://www.jetbrains.com/idea/).

2. **Install Dependencies:**
- In the terminal, run:
   ```bash
   npm install
   npm run dev
   ```

3. **Access the Website:**
- Open your browser and navigate to [http://localhost:3000/](http://localhost:3000/).

![StudiTrade Website](https://github.com/user-attachments/assets/249e975d-5eed-4d8a-9890-03bc8142549f)


### Using the Website

1. **Login:**
- Log in with a valid email address capable of receiving emails (preferably a Google-registered email for backend permissions).
- You can create an account manually or use your Google account to log in.

![Login Page](https://github.com/user-attachments/assets/bcacf1fe-2c59-455b-a744-4e8806270b66)


2. **Homepage:**
- Browse available documents for sale.
- Click on a document and then the **Buy** button to initiate a purchase.

![Homepage](https://github.com/user-attachments/assets/1160d78a-8a4f-4497-b8c2-fa8653fd5d8e)


3. **Payment:**
- You will be redirected to a Stripe payment page to complete your purchase.

![Stripe Payment](https://github.com/user-attachments/assets/21451513-bb89-423b-8275-f619649c9413)


4. **Post-Purchase:**
- **Digital Documents:** Receive a confirmation email with links to access your purchased documents.
- **Physical Documents:** Receive a confirmation email with a confidential code (composed of A, B, and C) to retrieve the document using the embedded system.

![Confirmation Email for digital documents](https://github.com/user-attachments/assets/8d53cf30-869e-4508-b379-3b076923da3a)
![Confirmation Email for non-digital documents](https://github.com/user-attachments/assets/ae36fa07-f66e-4c23-8af4-5dc4030e45f2)

5. **Selling Documents:**
- Navigate to the **Sell Files** page to upload and list your documents for sale.

![Sell Files Page](https://github.com/user-attachments/assets/d979a5ae-375d-4732-a0d0-6b3ca3e75079)


6. **Personal Management:**
- **Personal Notes:** View the documents you have posted.
- **My Favorites:** Access your favorite documents by clicking the heart icon.
- **Purchased Files:** View all documents you have purchased.

![Personal Notes](https://github.com/user-attachments/assets/53ef25d0-1665-4c80-a414-4983535196b8)


7. **Navigation:**
- Click the StudiTrade logo to return to the homepage at any time.

## Mobile Application

Developed in **Kotlin**, the mobile application ensures that StudiTrade is accessible on the go.

### Mobile Application Initialization

1. **Open Project:**
- Open the **StudiTradeV2** folder in [Android Studio](https://developer.android.com/studio).

2. **Launch Application:**
- Click the **Run** button to build and launch the application on your emulator or connected device.

### Using the Application

1. **Login Page:**
- Similar to the website, you can create an account or log in using the same credentials registered on the website.

2. **Homepage:**
- Browse and view all available documents for sale.

3. **Purchasing Documents:**
- Tap the **Buy** button to purchase a document. You will be redirected to the website to complete the transaction securely.

4. **Uploading Documents:**
- Access the upload page to select documents from your phone and publish them to the database.

5. **Profile and Settings:**
- Access your profile by tapping the profile icon at the top right.
- View and edit your account information.
- Navigate to the settings page for additional configurations.

## Embedded System

StudiTrade's embedded system ensures the secure physical delivery of non-digital documents through an integrated locker mechanism.

### System Components

- **ESP32:** Main controller for the embedded system.
- **TEMT6000 Light Sensor:** Detects the presence of a document in the locker.
- **WS2812B LED:** Provides status indications (e.g., green for unlocked).
- **OLED Display:** Displays information and prompts to the user.

![Embedded System Components](https://github.com/user-attachments/assets/d34f3760-1b14-4d77-8523-8acce1096889)


### Embedded System Initialization

1. **Access Installation Images:**
- Refer to the installation images available in the GitHub repository to assemble the hardware components.

2. **Clone Repository:**
- Clone the `Arduino/sketch_dec26a` directory from the GitHub repository into the [Arduino IDE](https://www.arduino.cc/en/software).

```bash
git clone https://github.com/Marabii/ProjectTB/Arduino/sketch_dec26a
```

3. **Install Libraries:**
- Open the Arduino Library Manager and install the following libraries:
   - **Adafruit GFX**
   - **Adafruit SSD1306**
   - **Adafruit NeoPixel**
   - **WiFi** (built-in on ESP32)
   - **HTTPClient** (built-in on ESP32)

![Installing Arduino Libraries](path/to/installing-arduino-libraries.png)

4. **Upload Code:**
- Upload the cloned code to the ESP32 using the Arduino IDE.

### Using the Embedded System

1. **Seller Workflow:**
- **Deposit Document:** The seller places the document into a secure locker.
- **Detection:** The TEMT6000 light sensor detects the presence of the document.

2. **Buyer Workflow:**
- **Payment:** The buyer completes the purchase on the website and receives an authentication code via email.
- **Retrieve Document:**
   - Enter the authentication code (composed of A, B, and C) using the OLED screen buttons.
   - Upon successful entry, the locker unlocks, and the WS2812B LED turns green.
- **Document Retrieval:** After removing the document, the light sensor detects the absence, signaling the completion of the transaction.

## Contact

For any issues or inquiries regarding the setup and usage of StudiTrade, please contact:

- **Email:** [minehamza97@gmail.com](mailto:minehamza97@gmail.com)

# License



*This README was last updated on January 11, 2025.*
