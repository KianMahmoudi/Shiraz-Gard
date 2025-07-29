# ShirazGard 🌸
**Your Intelligent Travel Companion for Shiraz**

Discover the magic of Persia's cultural capital with this all-in-one tourism assistant. Built with modern Kotlin and MVVM architecture, ShirazGard helps tourists and locals explore the city effortlessly.

[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.9.0-blueviolet?logo=kotlin)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Apache-green)](LICENSE)
[![Back4App](https://img.shields.io/badge/Backend-Back4App-ff69b4?logo=parse)](https://back4app.com)

## 🧭 Table of Contents
| Section | Quick Links |  
|---------|-------------|  
| Features | [✨ Key Features](#-key-features) |  
| Screenshots | [📱 Screenshots](#-screenshots) |
| Tech Stack | [🛠 Technology Stack](#-technology-stack) |  
| Database | [🗃 Database Schema](#database-schema) |  
| Sample Data | [📊 Sample Data](#-sample-data) |
| Setup | [🔐 API Key Setup](#-api-key-setup) |  
| Build | [🚀 Build & Run](#-build) |  
| Contact | [📞 Contact](#-contact) |
| License | [📜 License](#-license)

## ✨ Key Features

### 🌍 Comprehensive POI Database
**8 essential categories** including:
- 🏛 Historical sites & 🎭 Cultural centers  
- 🍽 Restaurants & 🏨 Hotels  
- ⛽ Gas stations & 🅿️ Parking  
- 🏥 Hospitals & 🚽 Public toilets  

**Rich details for each location**:
- High-quality photos  
- Precise GPS coordinates  
- User-generated tips  
- Accessibility information  

### 🧭 Smart Navigation Tools
- **One-tap directions** (Google Maps/Waze/Snap integration)  
- **Offline-capable** base map of Shiraz   

### ⭐ Social Features
- Rate locations (1-5 stars)  
- Read/write detailed reviews  
- See **crowd-sourced ratings**  

### 🎨 Personalized Experience
- **Instant language switching** (English/Farsi)  
- **Dark/Light mode** support  
- **Favorites system** 
- Custom user profiles with avatars

## 📱 Screenshots
<div align="center" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 12px; margin: 20px 0;">
  <img src="https://github.com/user-attachments/assets/393ecf44-7485-4d91-a3b8-ce96cba3149f" width="150" alt="Home Page" style="border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <img src="https://github.com/user-attachments/assets/7b5e5d09-399b-4373-b1b1-36b589fed06b" width="150" alt="Categories" style="border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <img src="https://github.com/user-attachments/assets/0446904c-6e55-48bf-99c2-6c7618b36852" width="150" alt="Favoriets" style="border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <img src="https://github.com/user-attachments/assets/594b9a7f-27d4-4267-b59e-4ba17fc66784" width="150" alt="Place Description" style="border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
  <img src="https://github.com/user-attachments/assets/e7140ef5-c9f7-4405-b0fb-88d93b5dc1b7" width="150" alt="Map" style="border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
   <img src="https://github.com/user-attachments/assets/5f1e608f-36f8-4716-8350-44f49f757710" width="150" alt="Comment" style="border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
   <img src="https://github.com/user-attachments/assets/0324ee28-d6e0-465b-88c0-0b0fc3c3798c" width="150" alt="Search" style="border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
</div>

## 🛠 Technology Stack

#### Core Architecture
- **Language**: ![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blueviolet?logo=kotlin)
- **Backend**: ![Back4App](https://img.shields.io/badge/Backend-Back4App-ff69b4?logo=parse)
- **Pattern**: ![MVVM](https://img.shields.io/badge/Architecture-MVVM-important)

#### Essential Libraries
| Category        | Technology                    | Badge                                                                 |
|-----------------|-------------------------------|-----------------------------------------------------------------------|
| Networking      | Retrofit            | ![Retrofit](https://img.shields.io/badge/Retrofit-2.9.0-red)         |
| DI              | Hilt                          | ![Hilt](https://img.shields.io/badge/Hilt-2.48-yellow)               |
| Async           | Coroutines            | ![Coroutines](https://img.shields.io/badge/Coroutines-1.7.3-orange) |
| Local Storage   | DataStore                     | ![DataStore](https://img.shields.io/badge/DataStore-1.0.0-blue)      |
| Image Loading   | Glide                         | ![Glide](https://img.shields.io/badge/Glide-4.16.0-lightblue)        |
| Animations      | Lottie                        | ![Lottie](https://img.shields.io/badge/Lottie-6.1.0-green)           |
| Mapping         | OsmDroid                      | ![OsmDroid](https://img.shields.io/badge/OsmDroid-6.1.14-success)    |
| Localization    | Lingver                       | ![Lingver](https://img.shields.io/badge/Lingver-1.3.0-blueviolet)    |
# Database Schema

## Collections Structure

### 1. `Place` Collection
```json
{
  "objectId": "string",
  "location": {
    "__type": "GeoPoint",
    "latitude": "float",
    "longitude": "float"
  },
  "type": "string",
  "faName": "string",
  "enName": "string",
  "faAddress": "string",
  "enAddress": "string",
  "faDescription": "string",
  "enDescription": "string",
  "createdAt": "string",
  "updatedAt": "string"
}
```
### 2. `Comments` Collection
```json
{
  "objectId": "string",
  "placeId": "string",
  "userId": "string",
  "commentText": "string",
  "ratingValue": "Int",
  "username": "string",
  "createdAt": "string",
  "updatedAt": "string"
}
```
### 3. `Photos` Collection
```json
{
  "objectId": "string",
  "placeId": "string",
  "photosUrl": [
    "string",
    "string"
  ],
  "createdAt": "string",
  "updatedAt": "string"
}
```
### 4. `FavoritePlaces` Collection
```json
{
  "objectId": "string",
  "placeId": "string",
  "userId": "string",
  "createdAt": "string",
  "updatedAt": "string"
}
```
## Schema Overview
The database follows a **NoSQL document-based structure** hosted on Back4App. All collections feature automatically generated unique identifiers and timestamp fields.

### Key Characteristics
1. **Auto-generated IDs**  
   - `objectId` fields are 10-character unique strings  
   - Automatically created by Back4App when new records are inserted  
   - Example: `"04oP06lV0P"`  
   - Immutable after creation  

2. **Automatic Timestamps**  
   - `createdAt`: Set when record is first created (UTC)  
   - `updatedAt`: Updated on every modification (UTC)  
   - Format: ISO 8601 (`"2025-06-28T19:06:05.129Z"`)  

3. **Bilingual Support**  
   All text fields have parallel Persian (fa) and English (en) versions

## 📊 Sample Data
### Sample Places
Here are some example place entries from the Place class:
```json
[
  {
    "location": {
      "__type": "GeoPoint",
      "latitude": 29.63284,
      "longitude": 52.556683
    },
    "faName": "مجتمع رستوران هفت خان",
    "enName": "Haft Kahn Restaurant Complex",
    "faAddress": "بلوار قرآن ، شیراز 71364 ایران",
    "enAddress": "Quran Blvd, Shiraz 71364 Iran",
    "faDescription": "",
    "enDescription": "",
    "type": "restaurant",
    "createdAt": "2025-07-12T13:48:59.988Z",
    "updatedAt": "2025-07-12T13:48:59.988Z",
    "objectId": "kdBGxfcwpo"
  },
  {
    "location": {
      "__type": "GeoPoint",
      "latitude": 29.625626599999997,
      "longitude": 52.5420072
    },
    "enName": "Hazrat Ali Asghar Hospital Shiraz",
    "faName": "بیمارستان حضرت علی اصغر (ع) شیراز",
    "type": "hospital",
    "faAddress": "شیراز، پل حر، خیابان شهید مشکین فام، روبروی خیابان سمیه",
    "enAddress": "Shiraz, Bridge Har, Shahid Meshkin Fam Street, Opposite Somayeh Street",
    "faDescription": "بیمارستان حضرت علی اصغر (ع) واقع شهرستان شیراز بیمارستانی است آموزشی...",
    "enDescription": "Hazrat Ali Asghar Hospital is a Shiraz Hospital...",
    "createdAt": "2025-05-09T16:05:16.127Z",
    "updatedAt": "2025-05-09T16:05:16.127Z",
    "objectId": "zBUrOtuQR3"
  },
  {
    "location": {
      "__type": "GeoPoint",
      "latitude": 29.935101,
      "longitude": 52.890367
    },
    "faName": "تخت جمشید",
    "enName": "Persepolis",
    "faAddress": "استان فارس، شهرستان مرودشت، مجموعه تخت جمشید",
    "enAddress": "Fars Province, Marvdasht County, Persepolis Complex",
    "faDescription": "امپراتوری هخامنشی نامی آشنا برای هر دوست‌دار تاریخ است و شهر باستانی تخت جمشید یا پرسپولیس یادگاری ارزشمند از این سلسله به شمار می‌رود. تخت جمشید اثری است که نامش در فهرست میراث جهانی ایران می‌درخشد و افتخاری برای هر ایرانی محسوب می‌شود. ستون های تخت جمشید و نقش‌ونگارها و حکاکی‌های این شهر هر کسی را به خود خیره می‌کنند و از گذشته‌های دور سخن می‌گویند؛ می‌خواهیم برایتان از شگفتی های تخت جمشید و معماری منحصربه‌فرد آن بگوییم. تا انتهای این مسیر با ما همراه باشید.",
    "enDescription": "The Achaemenid Empire is a familiar name for every lovers of history, and the ancient city of Persepolis or Persepolis is a valuable reminder of this dynasty. Persepolis is a work that shines on the Iranian World Heritage List and is an honor for every Iranian. The columns of Persepolis and engravings and engravings of this city stun and speak of the distant past; We want to tell you about the wonders of Persepolis and its unique architecture. Join us by the end of this route.",
    "type": "history",
    "createdAt": "2025-07-12T10:52:58.525Z",
    "updatedAt": "2025-07-15T10:54:58.163Z",
    "objectId": "MxTELaEANI"
  },
  {
    "location": {
      "__type": "GeoPoint",
      "latitude": 29.6086796,
      "longitude": 52.548242800000025
    },
    "faName": "مسجد نصیرالملک شیراز",
    "enName": "Nasir al -Molk Mosque in Shiraz",
    "faAddress": "شیراز، خیابان لطفعلی خان زند",
    "enAddress": "Shiraz, Lotfali Khan Zand Street",
    "faDescription": "نمی‌شود از جاذبه‌های فارس سخن بگوییم و نامی از مسجد نصیرالملک شیراز یا همان مسجد صورتی شیراز به میان نیاید. این مسجد با جلوه‌هایش دل از هر کسی می‌برد. کاشی‌کاری مسجد نصیرالملک آن‌قدر زیبا و ظریف است که می‌توان ساعت‌ها به آن نگریست و داستان‌ها در موردش گفت. هر کسی با دیدن عکس مسجد نصیرالملک وسوسه می‌شود تا برنامه سفرش به شیراز را بچیند. جلوه‌های خیره‌کننده مسجد نصیرالملک، کارناوال را بر آن داشته تا سری به شهر بهارنارنج بزند و قدم به درون این بنا بگذارد. همراه‌مان باشید تا از این مسجد تماشایی بیشتر بدانید...",
    "enDescription": "We cannot speak of the attractions of Fars and there is no mention of the Nasir al -Molk mosque in Shiraz or the pink mosque of Shiraz. This mosque with its effects takes their hearts. The tiling of the Nasir al -Molk Mosque is so beautiful and delicate that it can be looked at for hours and the stories told. Everyone is tempted to see the photo of the Nasir al -Molk Mosque to apply his trip to Shiraz. The stunning effects of the Nasir al -Molk Mosque have made the carnival to go to the city of Spring Orange and step into the building. Join us to find out more from this spectacular mosque ...",
    "type": "history",
    "createdAt": "2025-07-12T10:52:09.147Z",
    "updatedAt": "2025-07-12T10:52:09.147Z",
    "objectId": "6GWzpw7mG2"
  },
  {
    "location": {
      "__type": "GeoPoint",
      "latitude": 29.60922,
      "longitude": 52.54039
    },
    "faName": "اقامتگاه سنتی پنج دری شیراز",
    "enName": "Shiraz Traditional Panj Dari Residence",
    "faAddress": "شیراز  خیابان 9 دی  مقابل پارکینگ شاه چراغ  ابتدای کوچه هفت پیچ  پلاک 46",
    "enAddress": "Shiraz Street 9 January in front of King Parking Parking at the beginning of the Alley of Seven Plaque Plaque 46",
    "faDescription": "اقامتگاه سنتی پنج دری شیراز در زمینی به وسعت 720 متر واقع شده است. پیشینه این اقامتگاه به دوران قاجار باز می گردد و با قدمتی بیش از 120 سال، حس دلنشین زندگی در ادوار گذشته را به مهمانان القا می کند. در اسفند ماه سال 1396، این مجموعه پس از سه سال مرمت و بازسازی، به عنوان اقامتگاه سنتی به بهره برداری رسید. اقامتگاه پنج دری شیراز شامل یک ساختمان قدیمی و یک ساختمان جدید با نام یاس می باشد.  11 باب اتاق اقامتگاه پنج دری در طبقات اول و دوم ساختمان قدیمی و 6 باب اتاق در طبقات همکف و اول ساختمان یاس واقع شده اند. اتاق های ساختمان یاس فاقد یخچال، حمام و سرویس بهداشتی می باشند، که جهت رفاه حال مهمانان گرامی حمام و سرویس بهداشتی ایرانی و فرنگی به صورت عمومی در محوطه ساختمان یاس تعبیه شده است. همچنین یخچال به صورت عمومی در آشپزخانه ساختمان یاس برای استفاده مهمانان فراهم شده است. قرارگیری اقامتگاه سنتی پنج دری در خیابان 9دی، موجب دسترسی آسان گردشگران به مرقد مطهر شاهچراغ، مسجد نصیرالملک و مسجد جامع عتیق می شود که یکی از مزایای مهم این اقامتگاه به شمار می آید.افراد سالمند و خانواده هایی که کودک خردسال به همراه دارند توجه نمایند اقامتگاه سنتی پنج دری در مسیری واقع شده که امکان رفت و آمد خودرو تا درب اقامتگاه وجود ندارد و میهمانان گرامی باید مسیری در حدود 70 متر را تا اقامتگاه پیاده طی کنند، همچنین در قسمت ورودی و اتاق ها تعدادی پله وجود دارد.لازم به ذکر است ساختمان یاس در فاصله 50 متری از ساختمان اصلی قرار گرفته است.قوانین کودکدر صورتي که سن مهمان از 6 سال بيشتر باشد، مهمان بزرگسال محسوب مي‌شود در صورتي که سن خردسال بين 0 تا 3 سال باشد، به صورت رايگان محاسبه مي‌شود. //در صورتي که سن مهمان از 6 سال بيشتر باشد، مهمان بزرگسال محسوب مي‌شود در صورتي که سن خردسال بين 3 تا 6 سال باشد، مبلغ رزرو 50% محاسبه مي‌شودوای فایدر محوطه و اتاق ها (500 مگابایت رایگان)",
    "enDescription": "The traditional five -door Shiraz residence is located on a land of 720 meters. The background of this residence goes back to the Qajar era and, for more than 120 years, gives guests a pleasant sense of life in the past. In March 2016, the complex was put into operation as a traditional residence after three years of restoration. Shiraz's five -door residence includes an old building and a new building called Jasmine.  11 The five -door residence room is located on the first and second floors of the old building and 6 rooms on the ground floor and the first of the jasmine building. Jasmine building rooms lack refrigerators, bathrooms and bathrooms, which are publicly built in the jasmine building for the welfare of the dear bathrooms and bathrooms. The refrigerator is also publicly provided in the kitchen of Jasmine building for guests. The location of the traditional five -door residence on the 9th Street makes it easy for tourists to access the shrine of Shahcharagh, the Nasir al -Molk Mosque and the Old Jame Mosque, which is one of the important benefits of this residence. Elderly and families who have young children have no attention to the traditional residence in the traditional residence. The entrance and the rooms have a number of stairs. It should be noted that the Jasmine building is 50 meters from the main building. If the guest age is older than 6 years, the adult is considered to be free if the young age is between 0 and 3 years. // If the guest age is older than 6 years, the guest is considered an adult if the young age is between 3 and 6 years, the reservation amount will be calculated 50% and the benefits of the room and rooms (500 MB free)",
    "type": "hotel",
    "createdAt": "2025-07-12T13:51:49.426Z",
    "updatedAt": "2025-07-12T13:51:49.426Z",
    "objectId": "CUJM5b1Izl"
  }
]
```
### Sample Photos
Example photo entries from the Photos class:
```json
[
  {
    "photosUrl": [
      "https://media-cdn.tripadvisor.com/media/photo-o/05/ba/90/ab/gissia-cofeshop-second.jpg",
      "https://media-cdn.tripadvisor.com/media/photo-o/05/ba/92/06/haft-khan-restaurant.jpg",
      "https://media-cdn.tripadvisor.com/media/photo-o/05/ba/90/b2/sindokht-restaurant-ground.jpg",
      "https://media-cdn.tripadvisor.com/media/photo-o/05/ba/90/b0/vip-saloon-second-floor.jpg",
      "https://media-cdn.tripadvisor.com/media/photo-o/05/ba/90/af/haft-khan-restaurant.jpg"
    ],
    "placeId": "kdBGxfcwpo",
    "createdAt": "2025-07-12T13:49:00.390Z",
    "updatedAt": "2025-07-12T13:49:00.390Z",
    "objectId": "XVZdUQeMzd"
  },
  {
    "photosUrl": [
      "https://cdn.balad.ir/crowd-images/all/original/ei_1629661533060.jpg?x-img=v1/crop,x_1080,y_0,w_1840,h_1840/resize,h_360/format,type_webp,lossless_false/autorotate",
      "https://cdn.balad.ir/crowd-images/all/original/1veUzTdt9AEkY0-f53bb0c9b8b7471eb6d524407791f6df.jpg?x-img=v1/crop,x_155,y_0,w_530,h_530/resize,h_360/format,type_webp,lossless_false/autorotate"
    ],
    "placeId": "zBUrOtuQR3",
    "createdAt": "2025-01-28T13:15:17.686Z",
    "updatedAt": "2025-05-09T16:29:32.525Z",
    "objectId": "cJL4nNG88H"
  },
  {
    "photosUrl": [
      "https://media.karnaval.ir/uploads/2018/03/IQFn5o2c2gPtef78-1522229877265.jpg",
      "https://media.karnaval.ir/uploads/2018/04/KDqZaSsYf3EJTEYC-1524336997838.jpg",
      "https://media.karnaval.ir/uploads/2018/04/F0qacQwPW0uKpn6f-1524336997787.jpg",
      "https://media.karnaval.ir/uploads/2018/04/cFcKQx08FYANt9wC-1524377457220.jpg",
      "https://media.karnaval.ir/uploads/2018/04/dtQNMM9K9mlkJl3H-1524336618859.jpg"
    ],
    "placeId": "MxTELaEANI",
    "createdAt": "2025-07-12T10:52:58.752Z",
    "updatedAt": "2025-07-12T10:52:58.752Z",
    "objectId": "K0blr4tXxA"
  },
  {
    "photosUrl": [
      "https://media.karnaval.ir/uploads/2018/12/3rcg7NJJsvJXeoCj-1546081408133.jpg",
      "https://media.karnaval.ir/uploads/2018/12/4sPBzopJIAWgSH9t-1546081478425.jpg",
      "https://media.karnaval.ir/uploads/2018/12/TamCnfnUa0gbYd3a-1546081567380.jpg",
      "https://media.karnaval.ir/uploads/2018/12/6jKx8POVvrJKNaSD-1546081557577.jpg",
      "https://media.karnaval.ir/uploads/2018/12/wzptfux21ETmlrpv-1546083528998.jpg"
    ],
    "placeId": "6GWzpw7mG2",
    "createdAt": "2025-07-12T10:52:09.683Z",
    "updatedAt": "2025-07-12T10:52:09.683Z",
    "objectId": "XLR2GKvQD8"
  },
  {
    "photosUrl": [
      "https://media.karnaval.ir/uploads/2024/06/78750.webp",
      "https://media.karnaval.ir/uploads/2024/06/78738.webp",
      "https://media.karnaval.ir/uploads/2024/06/78737.webp",
      "https://media.karnaval.ir/uploads/2024/06/78739.webp",
      "https://media.karnaval.ir/uploads/2024/06/78740.webp"
    ],
    "placeId": "CUJM5b1Izl",
    "createdAt": "2025-07-12T13:51:49.827Z",
    "updatedAt": "2025-07-27T09:27:55.804Z",
    "objectId": "bZNlULUXW4"
  }
]
```
   
## 🔐 API Key Setup  
To run the project, create a `local.properties` file in the root directory with these values:  

```properties  
# app/local.properties  
WEATHER_KEY=your_weatherapi_key_here  
BACK4APP_SERVER_URL=https://parseapi.back4app.com/  
BACK4APP_CLIENT_KEY=your_back4app_client_key  
BACK4APP_APP_ID=your_back4app_app_id
```
These keys will be automatically loaded into:

```kotlin
BuildConfig.WEATHER_API_KEY  
BuildConfig.BACK4APP_SERVER_URL  
BuildConfig.BACK4APP_CLIENT_KEY  
BuildConfig.BACK4APP_APP_ID  
```
Important: Never commit this file! It's already in .gitignore

## 🚀 Build

1. **Clone the repository:**

```bash
git clone https://github.com/KianMahmoudi/shiraz-gard.git
```
2. **Add API keys:**
Create app/local.properties with your keys (see [API Key Setup](#-api-key-setup))

3. **Open in Android Studio:**
    Build the project (Ctrl+F9)
     Run on emulator or device

5. **For production builds:**
```bash
./gradlew assembleRelease
```
## 📞 Contact

**Project Lead**: Kian Mahmoudi
✉️ **Email**: [kianmahmoudi9@gmail.com](mailto:kianmahmoudi9@gmail.com)  
📱 **Telegram**: [@DotDrMahmood](https://t.me/DotDrMahmood)  
💻 **GitHub**: [KianMahmoudi](https://github.com/KianMahmoudi)  
🌐 **Project Repository**: [https://github.com/KianMahmoudi/shiraz-gard](https://github.com/KianMahmoudi/shiraz-gard) 
## 📜 License
Copyright 2025 Kian Mahmoudi

Licensed under the Apache License 2.0:
- ✅ **Allowed**:
  - Commercial use
  - Modification
  - Private use
  - Distribution (with license copy)
- ❌ **Restricted**:
  - Redistributing **identical** copies on app stores
  - Using project name/logo without attribution
- ℹ️ Full terms: [LICENSE](./LICENSE)
