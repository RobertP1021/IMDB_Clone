package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingLogin extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private IMDB imdb;

    private JPanel cardPanel;
    private CardLayout cardLayout;

    private JFrame mainAppWindow;

    public SwingLogin(IMDB imdb) {
        this.imdb = imdb;
        initializeComponents();
    }

    private void initializeComponents() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                User authenticatedUser = authenticateUser(email, password);

                if (authenticatedUser != null) {
                    showMainApplicationWindow(authenticatedUser);
                    dispose();
                } else {
                    showResultDialog("Datele introduse sunt incorecte. Incearca din nou.");
                }
            }
        });
        add(panel);
    }

    private User authenticateUser(String email, String password) {
        for (Regular regular : imdb.regularUsers) {
            if (regular.getInformation().getCredentials().getEmail().equalsIgnoreCase(email)
                    && regular.getInformation().getCredentials().getPassword().equalsIgnoreCase(password)) {
                return regular;
            }
        }

        for (Contributor contributor : imdb.contributors) {
            if (contributor.getInformation().getCredentials().getEmail().equalsIgnoreCase(email)
                    && contributor.getInformation().getCredentials().getPassword().equalsIgnoreCase(password)) {
                return contributor;
            }
        }

        for (Admin admin : imdb.admins) {
            if (admin.getInformation().getCredentials().getEmail().equalsIgnoreCase(email)
                    && admin.getInformation().getCredentials().getPassword().equalsIgnoreCase(password)) {
                return admin;
            }
        }

        return null;
    }

    private void showResultDialog(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showMainApplicationWindow(User authenticatedUser) {
        mainAppWindow = new JFrame("Main Application Window");
        mainAppWindow.setSize(800, 600);
        mainAppWindow.setLocationRelativeTo(null);

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        if (authenticatedUser instanceof Admin) {
            cardPanel.add(createAdminOptionsPanel(authenticatedUser), "MAIN");
        } else if (authenticatedUser instanceof Contributor) {

        } else if (authenticatedUser instanceof Regular) {

        }

        mainAppWindow.setContentPane(cardPanel);
        mainAppWindow.setVisible(true);
    }

    private JPanel createAdminOptionsPanel(User authenticatedUser) {
        JPanel adminOptionsPanel = new JPanel();
        adminOptionsPanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Bine ati venit, " + authenticatedUser.getUsername() + "!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        adminOptionsPanel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(0, 1));

        JButton viewProductionDetailsButton = new JButton("Vezi detalii de productie");
        JButton viewActorDetailsButton = new JButton("Vezi detalii despre actori");
        JButton viewNotificationsButton = new JButton("Vezi notificari");
        JButton searchButton = new JButton("Cauta actor/film/serial");
        JButton manageFavoritesButton = new JButton("Adauga/Sterge actor/film/serial de la favorite");
        JButton manageUserButton = new JButton("Adauga/Sterge utilizator");
        JButton manageSystemButton = new JButton("Adauga/Sterge Actor/Productie din sistem");
        JButton logoutButton = new JButton("Delogare");
        JButton updateInfoButton = new JButton("Actualizare informatii despre productie/actor");
        JButton viewRequestsButton = new JButton("Vizualizare cereri");

        viewProductionDetailsButton.addActionListener(e -> {
            showProductionDetailsPanel();
        });
        viewActorDetailsButton.addActionListener(e -> {

        });

        buttonsPanel.add(viewProductionDetailsButton);
        buttonsPanel.add(viewActorDetailsButton);
        buttonsPanel.add(viewNotificationsButton);
        buttonsPanel.add(searchButton);
        buttonsPanel.add(manageFavoritesButton);
        buttonsPanel.add(manageUserButton);
        buttonsPanel.add(manageSystemButton);
        buttonsPanel.add(logoutButton);
        buttonsPanel.add(updateInfoButton);
        buttonsPanel.add(viewRequestsButton);

        adminOptionsPanel.add(buttonsPanel, BorderLayout.CENTER);

        return adminOptionsPanel;
    }

    private void showProductionDetailsPanel() {
        JPanel productionDetailsPanel = new JPanel();
        productionDetailsPanel.setLayout(new BorderLayout());

        JButton backButton = new JButton("Inapoi la Pagina Principala");
        backButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "MAIN");
        });
        productionDetailsPanel.add(backButton, BorderLayout.WEST);

        JPanel movieDetailsPanel = new JPanel();
        movieDetailsPanel.setLayout(new BoxLayout(movieDetailsPanel, BoxLayout.Y_AXIS));

        for (Movie movie : imdb.movies) {

            JPanel productionPanel = new JPanel();
            productionPanel.setLayout(new BorderLayout());
            productionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            JLabel titleLabel = new JLabel(movie.getTitle());
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
            productionPanel.add(titleLabel, BorderLayout.NORTH);

            JLabel photoLabel = new JLabel("Poza Lipsa");
            photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            productionPanel.add(photoLabel, BorderLayout.WEST);

            JTextArea productionTextArea = new JTextArea();
            productionTextArea.setEditable(false);

            productionTextArea.append("Type: Movie" + "\n");
            productionTextArea.append("Directors: " + String.join(", ", movie.getDirectors()) + "\n");
            productionTextArea.append("Actors: " + String.join(", ", movie.getActors()) + "\n");
            productionTextArea.append("Genres: ");
            for(Genre genre : movie.getGenres()){
                productionTextArea.append(String.valueOf(genre) + ", ");
            }
            productionTextArea.append("\n");
            productionTextArea.append("Ratings:\n");
            for (Rating rating : movie.getRatings()) {
                productionTextArea.append("  - " + rating.getUsername() + ": " + rating.getScore() + " - " + rating.getComments() + "\n");
            }
            productionTextArea.append("Plot: " + movie.getPlotDescription() + "\n");
            productionTextArea.append("Average Rating: " + movie.getAverageRating() + "\n");
            productionTextArea.append("Duration: " + movie.getDuration() + "\n");
            productionTextArea.append("Release Year: " + movie.getReleaseYear() + "\n");

            productionPanel.add(new JScrollPane(productionTextArea), BorderLayout.CENTER);

            movieDetailsPanel.add(productionPanel);
        }

        productionDetailsPanel.add(new JScrollPane(movieDetailsPanel), BorderLayout.CENTER);

        cardPanel.add(productionDetailsPanel, "PRODUCTION_DETAILS");

        cardLayout.show(cardPanel, "PRODUCTION_DETAILS");

        mainAppWindow.revalidate();
        mainAppWindow.repaint();
    }

    public void showLoginFrame() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
        });
    }
}

