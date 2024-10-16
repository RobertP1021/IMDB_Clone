Postolache Robert Adrian
Difficulty level of the assignment: Medium
Time allocated for implementation: 2 weeks

In implementing the assignment, I tried to use all the methods from the support classes mentioned in the assignment requirements. I also created new methods in several classes, such as IMDB and Admin, to simplify the implementation of certain functionalities.

In the Staff class, I created two lists (userCreated and assignedRequests) to keep track of which user created a particular actor/production, and to clarify which user is responsible for resolving a request made by another user.

The way I used Design Patterns is as follows:

In the Admin class, I implemented RequestHolder, a static class that stores requests of type OTHERS/DELETE_ACCOUNT intended for the admin team. In this class, I have a list of type Request, where I store all these requests, and three methods for adding, deleting, and accessing the requests.
The UserFactory class, where I implemented the Factory Pattern, was used to create a new user and, similarly, to read users from JSON files.
The static InformationBuilder class, where I implemented the Builder Pattern, was used to instantiate Information objects and was used together with UserFactory when creating a new user.
For the Observer Pattern, I created two interfaces, Subject and Observer, which I included in the Rating, Request, and User classes to notify users based on one of four reasons for receiving a notification. In Rating and Request, I created three methods: one to add, one to delete, and one to notify the user, to populate the list with the necessary observers.
For the Strategy Pattern, I created an interface, ExperienceStrategy, and four classes: ActorExperience, IssueExperience, RatingExperience, and ProductionExperience, to more easily update the user's experience based on the action they performed.
I also created the InvalidCommandException class to ensure that if a user, for example, enters a String instead of an Int, they are notified of the mistake and given the opportunity to re-enter the correct data.

I tried to ensure that I implemented all the specific requirements of the assignment, such as deleting a Contributor User and transferring the deletion/update rights of the productions/actors created by them to the admin team. Thus, I hope I haven't forgotten anything.

I also added a minimal graphical interface where the user can log in, using the accounts read from the JSON file to verify if the entered data is correct. Then, I redirected the user to a main page where all options are displayed.

I only managed to implement this for an Admin-type user. Additionally, I implemented in detail the option to display the productions in the system, with a box for a potential image of the production and a button to return to the main page.

Since this was a more interesting part of the project, I'll go into a bit more detail on the elements used to create the graphical interface:

Swing Framework: I used Swing components such as JFrame, JTextField, JPasswordField, JButton, etc., to create a user-friendly graphical interface.
Layout Managers: I used layout managers, such as GridLayout and BorderLayout, to organize and place components within the application window.
CardLayout: I used CardLayout to manage different panels in the application, such as the login screen and production details.
JOptionPane: I used JOptionPane to display message dialogs to communicate authentication results or other informational messages.
