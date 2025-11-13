

# ✈️ Indogo - Flight Booking UI

**Indogo** est une application Android développée en **Kotlin** qui affiche une liste de vols avec leurs détails (compagnie, prix, durée, promotions, etc.) et permet d’interagir avec chaque vol pour afficher ou télécharger un billet électronique (E-Ticket).
L’application est conçue pour s’adapter aux différents écrans (smartphones, tablettes, modes portrait et paysage).

---

## 📱 Fonctionnalités principales

* Affichage d’une **liste de vols** avec :

  * Logo de la compagnie aérienne
  * Heure de départ et d’arrivée
  * Code IATA des aéroports
  * Durée du vol
  * Prix formaté selon la locale indienne
  * Indicateur de **repas gratuit** et **code promo**

* **Tri des vols** :

  * Du plus cher au moins cher
  * Du moins cher au plus cher

* **Support des orientations** :

  * Layout dynamique en mode portrait/paysage
  * Affichage en grille pour les écrans larges

* **Mode tablette** :

  * Détails du billet affichés dans un panneau latéral droit
  * Pas besoin d’ouvrir une nouvelle activité

* **Téléchargement du ticket PDF** (avec `PdfGenerator`)

---

## 🧩 Structure du projet

```
app/
├── java/
│   └── com/example/indogo/
│       ├── MainActivity.kt
│       ├── FlightAdapter.kt
│       ├── Flight.kt
│       ├── PdfGenerator.kt
│       ├── TicketActivity.kt
│     
│
└── res/
    ├── layout/
    │   ├── activity_main.xml
    │   ├── item_flight.xml
    │   ├── activity_ticket.xml
    ├── drawable/
    │   ├── ic_airline_placeholder.xml
    │   ├── ic_filter.xml
    │   ├── ic_edit.xml
    │   ├── ic_back_arrow.xml
    │   └── header_gradient_background.xml
    └── values/
        ├── colors.xml
        ├── strings.xml
        └── styles.xml
```

---

## ⚙️ Technologies utilisées

* **Kotlin**
* **RecyclerView** pour la liste des vols
* **ConstraintLayout / LinearLayout** pour la mise en page
* **AndroidX** et **Material Components**
* **PdfDocument API** pour la génération du ticket PDF

---

## 🖼️ Aperçu de l’interface

| Écran principal                                               |                                                                                                        
| ------------------------------------------------------------- | 
| <img width="440" height="562" alt="image" src="https://github.com/user-attachments/assets/eec14f25-f070-4734-bdd8-7e6db5147f62" />
 
 
 

------------------------------------------------------------------

| Liste des vols                                                 |
|----------------------------------------------------------------|
| <img width="274" height="665" alt="image" src="https://github.com/user-attachments/assets/dcb0a795-4f72-4dd5-823f-0897b40d933a" />

------------------------------------------------------------------

|Ticket Détail                                                       |
| ------------------------------------------------------------------ |
| <img width="247" height="441" alt="image" src="https://github.com/user-attachments/assets/2e71bced-8613-475b-9eb7-331c1b89db5d" />


## 🚀 Installation et exécution

1. Clone le dépôt :

   ```bash
   git clone https://github.com/aymen-afamm/flight.git
   ```

2. Ouvre le projet dans **Android Studio**.

3. Assure-toi d’avoir :

   * Android Studio **Arctic Fox (ou plus récent)**
   * SDK 33 ou supérieur
   * Gradle synchronisé

4. Lance l’application sur un **émulateur** ou un **appareil physique** :

   * Cible Android 8.0 (API 26) ou plus

---

## 📚 Classes principales

### 🔹 `MainActivity.kt`

* Point d’entrée de l’application.
* Gère les boutons de tri, le layout responsive et les événements de clic sur les vols.
* Met à jour la vue en fonction de l’orientation (portrait/paysage).

### 🔹 `FlightAdapter.kt`

* Adapter du `RecyclerView` pour afficher chaque vol.
* Gère la mise à jour de la liste (`updateFlights()`) et les interactions utilisateur.

### 🔹 `Flight.kt`

* Classe de données représentant un vol :

  ```kotlin
  data class Flight(
      val airlineName: String,
      val airlineLogo: Int,
      val departureCode: String,
      val departureTime: String,
      val arrivalCode: String,
      val arrivalTime: String,
      val duration: String,
      val price: Int,
      val hasFreeMeal: Boolean,
      val promoCode: String,
      val promoBackgroundColor: String
  ) : Serializable
  ```

### 🔹 `PdfGenerator.kt`

* Génère un billet électronique au format PDF pour le vol sélectionné.

---

## 🧠 Améliorations futures

* Intégrer une **API en temps réel** pour récupérer les vols (ex: Amadeus API, AviationStack)
* Ajouter une **recherche de vols**
* Implémenter une **base de données locale (Room)** pour sauvegarder les favoris
* Support du **thème sombre (Dark Mode)**

---

## 📄 Licence

Ce projet est distribué sous la licence **MIT**.
Tu es libre de l’utiliser, le modifier et le partager.




