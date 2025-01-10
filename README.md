Plateforme de vente de cours et fiches de révision : StudiTrade

Ce projet vise à développer une plateforme innovante qui permet aux étudiants d'acheter et de vendre des cours et fiches de révision. Inspirée de sites comme [StuDocu](https://www.studocu.com/fr/home), la plateforme inclut un site web, une application mobile et un système embarqué pour gérer les transactions physiques de cours et fiches de révisions.

Un ensemble de vidéos sont disponibles pour vous orienter sur l'utilisation des différentes interfaces sur le lien suivant : https://drive.google.com/drive/folders/1UCVssxO8sASxBBnHP7m6Lw6X-T_vOdLr?usp=drive_link

## PARTIE WEB

Initialisation du backend

1. Ouvrez le dossier « Backend » dans Visual Studio Code ou Intellij
2. Rendez-vous dans le fichier « ProjetTbApplication »
3. Cliquez sur « Run » à la ligne 9

Initialisation du frontend

1. Ouvrez le dossier "Frontent" dans Visual Studio Code ou Intellij
1. Dans le terminal, exécutez "npm install" puis "npm run dev"
1. Vous avez maintenant accès au site internet de StudiTrade sur : http://localhost:3000/

# Utilisation du site internet :

Lorsque vous êtes sur le site, vous devrez vous connecter avec une adresse mail valide capable de recevoir correctement des emails (nécéssaire pour la suite). Vous avez la possibilité de vous créez un compte manuellement ou bien de vous connecter avec votre compte Google.
Une fois cela fait, vous allez arrivez sur la page d'acceuil du site où vous verrez des documents mis en vente. Vous pouvez cliquer sur un des documents et de nouveau cliquer sur le bouton "Buy".

Vous accéderez à une page de paiement Stripe

EXPLICATION HAMZA

Vous recevrez par la suite un mail de confirmation avec un code confidentiel composé de A, B et de C, qui servira à récuperer le document physique grâce au système embarqué.

Vous avez aussi la possibilité de poster des documents pour les vendre via la page "Sell files".
Vous retrouverez les notes que vous avez posté dans l'onglet "Personal Notes".
Vous pouvez ajouter des documents à vos favoris en cliquant sur le coeur, et vous les retrouverez dans l'onglet "My favorites".
Tous les documents que vous avez acheté seront présent dans la page "Purchased files".
Il vous suffit de cliquer sur le logo StudiTrade pour de nouveau accédez à tous les documents en vente.

## PARTIE MOBILE

Initialisation de l'application mobile:

1. Ouvrez le dossier StudiTradeV2 dans Android Studio
2. Lancez l'application grâce au bouton "Run"

# Utilisation de l'application :

Vous arriverez dans un premier temps sur une page de connexion similaire au site internet. Vous aurez la possibilité de créer un compte ou de vous connecter avec les mêmes identifiants que lors de l'inscription sur le site internet.

Une fois cela effectué, vous arriverez sur la page d'accueil avec tous les documents mis en vente. Pour faciliter les transferts d'argent, lorsque vous voudrez acheter un document sur l'aplication via le bouton "Buy", vous serez rediriger vers le site internet effectuer l'achat. Vous pouvez visualiser les documents en cliquant dessus.

Vous avez également accès à une page qui vous permet de selectionner des documents sur votre téléphone et de les publier sur la base de données.
Vous aurez accès à votre profile en haut à droite et vous aurez accès à vos informations rentrées lors de la création de votre compte.
Une dernière page de réglages est disponible.

## PARTIE EMBARQUÉE

Notre système est basé sur des composants électroniques permet de gérer la remise physique des fiches :

- ESP32 : Contrôleur principal.
- Capteur de lumière TEMT6000 : Détection de la présence d’une fiche.
- LED WS2812B : Indication d’état
- Écran OLED : Affichage d’informations.

Initialisation du système embarqué :

1. Vous trouverez des images de l'intallation afin de la reproduire dans le répertoire Github
2. Une fois l'installation réalisée, cloner le répertoire `Arduino/sketch_dec26a`dans Arduino IDE.
3. Téléchargez les librairies suivantes via Arduino Library Manager :
   1. Adafruit GFX
   2. Adafruit SSD1306
   3. Adafruit NeoPixel
   4. WiFi (built-in on ESP32)
   5. HTTPClient (built-in on ESP32)
4. Charger le code sur l'ESP32 à l'aide de l'IDE Arduino.

# Utilisation du système embarqué :

1. Vendeur :

   - Le vendeur dépose la fiche dans un casier sécurisé.
   - Le capteur de lumière détecte la présence de la fiche.

2. Achateur :
   - L'acheteur paie sur le site et reçoit un code d'authentification par email.
   - L'acheteur saisit ce code grâce au bouton de l’écran OLED (A,B et C), celui-ci se déverrouille et la LED passe au vert.
   - Une fois la fiche retirée, le capteur détecte que le document à été récupéré.

---

## Auteurs

- Lucas CHATEAU
- Hamza DADDA
- Charles BOULLON
