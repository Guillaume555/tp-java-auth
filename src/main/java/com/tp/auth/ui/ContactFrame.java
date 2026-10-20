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

        JPanel top = new JPanel(new BorderLayout());
        top.add(userLabel, BorderLayout.WEST);
        top.add(logoutBtn, BorderLayout.EAST);

        JButton addBtn = new JButton("Ajouter");
        JButton editBtn = new JButton("Modifier");
        JButton deleteBtn = new JButton("Supprimer");
        addBtn.addActionListener(e -> openDialog(null));
        editBtn.addActionListener(e -> edit());

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // haut : user + déconnexion, centre : table, bas : boutons
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(top, BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);

        setContentPane(content);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        refreshTable();
    }

    private void refreshTable() {
        try {
            contacts = service.list(user.getId());
            model.setRowCount(0);
            for (Contact c : contacts) {
                model.addRow(new Object[] { c.getNom(), c.getPrenom(), c.getEmail(), c.getTelephone() });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void edit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un contact");
            return;
        }
        openDialog(contacts.get(row));
    }

    // null = ajout, sinon modif
    private void openDialog(Contact c) {
        ContactDialog dialog = new ContactDialog(this, c, user.getId());
        dialog.setVisible(true); // bloque jusqu'à la fermeture du formulaire
        if (dialog.isSaved()) refreshTable();
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}