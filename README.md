<h1 align="center">HANASHI</h1>

<p align="center">
Hanashi is a mobile application that allows users to read and share story snippets. The app is built using Kotlin and uses dicoing's story API to fetch the stories. The app also uses Google Maps API to display the location of the stories.
</p>

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

-   Android Studio Iguana | 2023.3.1 Patch 1 or later
-   Android SDK (specified in `local.properties`)
-   Google Maps API key (specified in `local.properties`)

### Installing

1. Clone the repository: `git clone https://github.com/xirf/hanashi.git`
2. Open the project in Android Studio
3. Get your google maps API key from the [Google Cloud Platform Console](https://console.cloud.google.com/)
4. Add the API key to the `local.properties` file as `MAPS_API_KEY=********`
5. Sync the Gradle files and build the project
6. Run the project on an emulator or a physical device

## Contributing

Everyone is welcome to contribute to the project. feel free to fork the repository and submit a pull request.

## Authors

-   **xirf** - _Development and enhancements_ - [xirf](https://github.com/xirf)
-   **dicoding** - _Story API_ - [dicoding](https://www.dicoding.com/)

## License

This project is licensed under the MIT License - see the `LICENSE.md` file for details
