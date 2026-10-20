package com.tp.auth.service;

import java.util.List;

import com.tp.auth.dao.ContactDao;
import com.tp.auth.model.Contact;

public class ContactService {

    private final ContactDao dao = new ContactDao();

    public List<Contact> list(int userId) throws Exception {
        return dao.findByUser(userId);
    }

    public List<Contact> search(int userId, String text) throws Exception {
        if (text == null || text.isBlank()) return dao.findByUser(userId);
        return dao.search(userId, text.trim());
    }

    // null = ok, sinon message d'erreur
    public String save(Contact c) throws Exception {
        String error = validate(c);
        if (error != null) return error;

        if (c.getId() == 0) dao.insert(c);
        else dao.update(c);
        return null;
    }

    public void delete(int id, int userId) throws Exception {
        dao.delete(id, userId);
    }

    private String validate(Contact c) {
        c.setNom(clean(c.getNom()));
        c.setPrenom(clean(c.getPrenom()));
        c.setEmail(clean(c.getEmail()));
        c.setTelephone(clean(c.getTelephone()));

        if (c.getNom() == null) return "Le nom est obligatoire";
        if (c.getPrenom() == null) return "Le prénom est obligatoire";
        if (c.getEmail() != null && (!c.getEmail().contains("@") || !c.getEmail().contains(".")))
            return "Email invalide";
        if (c.getTelephone() != null && !c.getTelephone().matches("\\+?[0-9 ]+"))
            return "Téléphone invalide";
        return null;
    }

    private String clean(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}