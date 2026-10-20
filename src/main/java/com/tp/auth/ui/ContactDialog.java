package com.tp.auth.ui;

import java.awt.*;
import javax.swing.*;

import com.tp.auth.model.Contact;
import com.tp.auth.service.ContactService;

public class ContactDialog extends JDialog {

    private final JTextField nomField = new JTextField(20);
    private final JTextField prenomField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField telField = new JTextField(20);
    private final JLabel errorLabel = new JLabel(" ", SwingConstants.CENTER);

    private final ContactService service = new ContactService();
    private final int contactId;
    private final int userId;
    private boolean saved = false;

    // contact null = ajout, sinon modif
    public ContactDialog(JFrame parent, Contact contact, int userId) {
        super(parent, contact == null ? "Ajouter un contact" : "Modifier un contact", true);
        this.userId = userId;
        this.contactId = (contact == null) ? 0 : contact.getId();

        if (contact != null) {
            nomField.setText(contact.getNom());
            prenomField.setText(contact.getPrenom());
            emailField.setText(contact.getEmail());
            telField.setText(contact.getTelephone());
        }

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Nom :"));
        form.add(nomField);
        form.add(new JLabel("Prénom :"));
        form.add(prenomField);
        form.add(new JLabel("Email :"));
        form.add(emailField);
        form.add(new JLabel("Téléphone :"));
        form.add(telField);

        errorLabel.setForeground(Color.RED);

        JButton saveBtn = new JButton("Enregistrer");
        JButton cancelBtn = new JButton("Annuler");
        saveBtn.addActionListener(e -> save());
        cancelBtn.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(saveBtn);

        JPanel buttons = new JPanel();
        buttons.add(saveBtn);
        buttons.add(cancelBtn);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        content.add(form, BorderLayout.NORTH);
        content.add(errorLabel, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void save() {
        Contact c = new Contact(contactId, userId, nomField.getText(), prenomField.getText(),
                emailField.getText(), telField.getText());
        try {
            String error = service.save(c);
            if (error == null) {
                saved = true;
                dispose();
            } else {
                errorLabel.setText(error);
            }
        } catch (Exception ex) {
            errorLabel.setText("Erreur : " + ex.getMessage());
        }
    }

    public boolean isSaved() {
        return saved;
    }
}