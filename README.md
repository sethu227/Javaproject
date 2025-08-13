# FileGuard - Intelligent Duplicate Detection & File Management

**FileGuard** is a powerful and intuitive web application designed to help you find and manage duplicate files on your system. It uses advanced algorithms to accurately identify duplicates, categorize files, and provide an easy-to-use interface for cleaning up your storage.

## ✨ Features

-   **🚀 High-Speed Scanning**: Optimized engine processes thousands of files per second with minimal resource usage.
-   **🧠 Smart Duplicate Detection**: Identifies duplicate files based on content hash, ensuring accuracy regardless of filename.
-   **🎵 Advanced Music Analysis**: Detects duplicate audio files by analyzing audio fingerprints and metadata, even with different formats or quality.
-   **🎬 Advanced Video Analysis**: Finds duplicate video files across different formats, qualities, and resolutions using video fingerprinting.
-   **📂 Automatic Categorization**: Automatically organizes scanned files into categories like Photos, Documents, Videos, Music, and Archives.
-   **📊 Interactive Dashboard**: View detailed scan results, including statistics on total files, categorized files, and storage usage.
-   **🗑️ Safe Deletion**: Review groups of duplicate files, select the ones you want to remove, and delete them permanently with a confirmation step.
-   **💅 Modern UI**: A clean, responsive, and user-friendly web interface built with Bootstrap and enhanced with animations.

## 🛠️ Tech Stack

-   **Backend**: Java, Spring Boot
-   **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
-   **Styling**: Bootstrap 5, Font Awesome
-   **Build Tool**: Maven (or Gradle)

## 🚀 Getting Started

### Prerequisites

-   Java JDK 17 or later
-   Maven 3.x or Gradle 7.x
-   Git

### Installation & Running

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/sethu227/Javaproject.git
    cd Javaproject
    ```

2.  **Build the project:**
    *Using Maven:*
    ```bash
    mvn clean install
    ```
    *Using Gradle:*
    ```bash
    gradle build
    ```

3.  **Run the application:**
    ```bash
    java -jar target/FileGuard-0.0.1-SNAPSHOT.jar 
    ```
    *(Note: The JAR file name might be different. Check your `target` or `build/libs` directory.)*

4.  **Access the application:**
    Open your web browser and navigate to `http://localhost:8080`.

## 📖 How to Use

1.  **Start a Scan**: On the home page, enter the full path to the directory you want to scan.
2.  **Enable Categorization (Optional)**: Check the "Enable Smart Categorization" box and select the file types you want to organize. The application will create subfolders for these categories within the scanned directory and move the files accordingly.
3.  **Launch Scan**: Click the "Launch Scan" button to begin.
4.  **Review Results**: After the scan is complete, you will see a summary of all files found.
5.  **View Duplicates**: Click the "View Duplicates" button to see a list of all duplicate files, grouped by their content hash.
6.  **Remove Duplicates**: Select the checkboxes next to the files you wish to delete. The "Remove Selected" button will show you the number of files and the total space you will save.
7.  **Confirm Deletion**: A confirmation pop-up will appear to prevent accidental deletion. Once confirmed, the selected files will be permanently removed from your system.
