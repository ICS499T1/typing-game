# Space Racer
Space Racer is a typing multi and single player game to make learning to type faster, easier, and more fun. A user will be able to create an account and compete with more players. The stats will help the user figure out what it's needed to make it into the leaderboard.
# Table of Contents
1. [Tools Used](#tools-used)
2. [Setup](#setup)
3. [Contributors](#contributors)
## Tools Used
Project: 
- Java
- Spring Framework
- Gradle
- ReactJS
- PostgreSQL

APIs: 
  - http://metaphorpsum.com/

Deployment: 
- Heroku
- Netlify
## Setup
In order to set up the game locally, you will need to pull the backend and frontend code. 

Backend Instructions:
- Create an application.properties (src/main/resources/application.properties) file and establish a connection with your local PostgreSQL database there.
- Add a game.secret property in application.properties (should be an alphanumeric string) for JWT token generator.
- Run the application!

Frontend Instructions:
- Go to resources/Global.js and modify the API and DOMAIN variables to match your localhost values. Ex.:  API: 'http://localhost:8080', DOMAIN: 'http://localhost:3000'.
- Run *npm install* to install packages you are missing.
- Run *npm start* to run the application.

You are good to go, enjoy playing locally!
## Contributors
- Shuja Uddin
- Saajine Sathappan
- Ksenia Golova
- Gina Bjork


