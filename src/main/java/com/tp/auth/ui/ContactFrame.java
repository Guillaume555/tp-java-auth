package com.tp.auth.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.tp.auth.model.Contact;
import com.tp.auth.model.User;
import com.tp.auth.service.ContactService;

public class ContactFrame extends JFrame {

    private final User user;
    private final ContactService service = new ContactService();
    private List<Contact> contacts = new ArrayList<>();

    private final JTextField searchField = new JTextField(20);

    // cellules non modifiables
    private final DefaultTableModel model = new DefaultTableModel(
            new String[] { "Nom", "Prénom", "Email", "Téléphone" }, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public ContactFrame(User user) {
        super("Mes contacts");
        this.user = user;

        JLabel userLabel = new JLabel("Connecté en tant que " + user.getUsername());
        JButton logoutBtn = new JButton("Se déconnecter");
        logoutBtn.addActionListener(e -> logout());

        JPanel userRow = new JPanel(new BorderLayout());
        userRow.add(userLabel, BorderLayout.WEST);
        userRow.add(logoutBtn, BorderLayout.EAST);

        JButton searchBtn = new JButton("Rechercher");
        searchBtn.addActionListener(e -> refreshTable());
        searchField.addActionListener(e -> refreshTable());

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchRow.add(new JLabel("Rechercher :"));
        searchRow.add(searchField);
        searchRow.add(searchBtn);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.add(userRow, BorderLayout.NORTH);
        top.add(searchRow, BorderLayout.SOUTH);

        JButton addBtn = new JButton("Ajouter");
        JButton editBtn = new JButton("Modifier");
        JButton deleteBtn = new JButton("Supprimer");
        addBtn.addActionListener(e -> openDialog(null));
        editBtn.addActionListener(e -> edit());
        deleteBtn.addActionListener(e -> delete());

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // haut : user + recherche, centre : table, bas : boutons
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(top, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);

        setContentPane(content);
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        refreshTable();
    }

    private void refreshTable() {
        try {
            contacts = service.search(user.getId(), searchField.getText());
            model.setRowCount(0);
            for (Contact c : contacts) {
                model.addRow(new Object[] { c.getNom(), c.getPrenom(), c.getEmail(), c.getTelephone() });
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    // null + message si rien n'est sélectionné
    private Contact getSelectedContact() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un contact");
            return null;
        }
        return contacts.get(row);
    }

    private void edit() {
        Contact c = getSelectedContact();
        if (c != null) openDialog(c);
    }

    private void delete() {
        Contact c = getSelectedContact();
        if (c == null) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Supprimer " + c.getNom() + " " + c.getPrenom() + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        try {
            service.delete(c.getId(), user.getId());
            refreshTable();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    // null = ajout, sinon modif
    private void openDialog(Contact c) {
        ContactDialog dialog = new ContactDialog(this, c, user.getId());
        dialog.setVisible(true); // bloque jusqu'à la fermeture du formulaire
        if (dialog.isSaved()) refreshTable();
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}