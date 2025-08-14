# FileGuard: Content-Based Duplicate File Manager

A Spring Boot web application designed to intelligently find and manage duplicate files (including text, images, and videos) by analyzing their content, not just names or metadata.

## 📖 Description

FileGuard solves the common problem of redundant files cluttering storage space. Unlike traditional duplicate finders that rely on simple checksums or file names, this project uses advanced content analysis and fuzzy hashing. This allows it to identify visually similar videos (even in different formats or encodings), text files with minor differences, and other near-duplicates.

The backend is built with **Java** and the **Spring Boot** framework, providing a robust server-side application. The user interface is rendered using **Thymeleaf**, making it accessible from any web browser.

## ✨ Features

*   **Content-Based Detection:** Finds duplicates by analyzing file content, not just metadata like name or size.
*   **Fuzzy Hashing:** Employs algorithms like `ssdeep` to find similar, but not necessarily identical, files.
*   **Broad Video Format Support:** Accurately detects duplicate videos across different formats (MP4, AVI, MOV, MKV, etc.) and compression levels.
*   **Web-Based Interface:** An easy-to-use UI to specify a directory, launch a scan, and review the duplicate file groups.
*   **Configurable Similarity Thresholds:** Fine-tune the sensitivity of the detection algorithm for different file types.

## 📸 Screenshots

Here are some screenshots showcasing the FileGuard application in action.

**1. Main Dashboard**
*The main entry point of the application where you can select a directory to scan.*
![Main Dashboard](./webimages/Screenshot%20(294).png)

**2. Scan in Progress**
*Real-time progress of the file analysis.*
![Scan in Progress](./webimages/Screenshot%20(295).png)

**3. Text File Duplicate Results**
*Identifies text files with similar content.*
![Text File Duplicate Results](./webimages/Screenshot%20(296).png)

**4. Video File Duplicate Results**
*Groups visually identical videos, even with different formats or encodings.*
![Video File Duplicate Results](./webimages/Screenshot%20(297).png)

**5. Image File Duplicate Results**
*Finds duplicate images based on their visual content.*
![Image File Duplicates](./webimages/Screenshot%20(309).png)

**6. Managing Duplicates**
*Review and manage the detected duplicate files.*
![Managing Duplicates](./webimages/Screenshot%20(310).png)

## 🚀 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You will need the following software installed on your system to run the application.
*   **Java Development Kit (JDK) 17** or newer.
*   Apache Maven
*   **ssdeep** (for fuzzy hashing). This must be installed and available in your system's PATH.
*   An IDE like IntelliJ IDEA, Eclipse, or VS Code is recommended.

### Installation

A step-by-step guide on how to set up the development environment.

1.  Clone the repository:
    ```bash
    git clone https://github.com/sethu227/Javaproject.git
    ```
2.  Navigate into the project directory:
    ```bash
    cd Javaproject
    ```
3.  Build the project and install dependencies using Maven:
    ```bash
    mvn install
    ```

## 🏃‍♀️ Usage

You can run the application in two ways:

1.  **Using the Maven Spring Boot Plugin (Recommended for development):**
    ```bash
    mvn spring-boot:run
    ```

2.  **Running the executable JAR:**
    ```bash
    java -jar target/appmanager-0.0.1-SNAPSHOT.jar
    ```

Once the application is running, open your web browser and navigate to `http://localhost:8080` to access the FileGuard interface.
