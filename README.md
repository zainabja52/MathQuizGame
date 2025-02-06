## 🎉 Mobile Math Quiz Game 🎉

### Overview
This Android application, a Mobile Math Quiz Game, challenges users with five timed math questions, tracking scores based on correct and incorrect answers. The questions are fetched from a CSV file and presented randomly without repetition.

### Features

- **📝 User Registration**: Users can register by providing their username, email, and birthdate, with checks for existing nicknames in the database.
- **🕹️ Quiz Mechanics**: The application loads questions from a CSV file, presenting them one at a time with a 10-second timer for each.
- **📊 Scoring System**: Points are awarded for correct answers and deducted for incorrect answers or if the timer expires.
- **🏁 End of Quiz Summary**: Displays the user's final score and inserts it into the database. Various queries are available to fetch top scores, count total players, and more.
- **🔄 Play Again Option**: Allows users to restart the quiz or exit.

https://github.com/user-attachments/assets/85004b0b-4fbd-4887-b746-d87e9c7c2176

### Technical Specifications

- **SQLite Database**: Utilizes SQLite for storing user data and scores.
- **Random Question Selection**: Ensures that the same question is not repeated within a single game session.
- **Timed Responses**: Each question is timed with a countdown, encouraging quick thinking.

- **Feedback Mechanisms**: Toast messages provide instant feedback on user responses.

### Programming Languages and Tools:
<img align="left" alt="Java" width="50px" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" /> <img align="left" alt="Android Studio" width="50px" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/androidstudio/androidstudio-original.svg" /> <img align="left" alt="XML" width="50px" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/xml/xml-original.svg" /> <br><br>

### Installation and Setup

1. Clone the repository:
    ```bash
    git clone https://github.com/zainabja52/MathQuizGame
    ```
2. Open the project in Android Studio.
3. Build and run the application on an Android device or emulator (API Level 26 to match a Pixel 3a XL with graphic=software).

### Code Structure
- **MainActivity.java**: Manages user registration and validation.
- **QuizActivity.java**: Handles loading and displaying questions, timers, and collecting answers.
- **EndActivity.java**: Displays the game's final scores and query results.
- **DatabaseHelper.java**: Manages all interactions with the SQLite database, including CRUD operations and custom queries.

### Database Schema
- **Table 1: Scores**
  - `id`: Auto-incremented identifier.
  - `nickname`: Unique username.
  - `score`: Points scored by the player.
  - `timestamp`: Time of record creation.

- **Table 2: Questions**
  - `id`: Unique identifier for each question.
  - `question`: The quiz question text.
  - `correct_answer`: The correct response to the question.
  - `option1`, `option2`, `option3`, `option4`: Incorrect answers provided as options. 

### Customization

#### UI Customization
Colors and styles can be customized in the `res/values/colors.xml` and drawable resources to enhance visual appeal and match brand guidelines.

### User Feedback and Navigation
Interactive elements such as buttons and toast messages are used throughout the application to guide the user experience and provide feedback.

### Error Handling
Robust error handling mechanisms ensure the application handles file reading failures or database connectivity issues gracefully, enhancing user experience and application stability.

### Background Image for Login
Enhance the user login experience with a background image: <br><br>
![loginbkg](https://github.com/user-attachments/assets/d4e95c2d-a090-4930-8980-9f9a47abd2c9)


