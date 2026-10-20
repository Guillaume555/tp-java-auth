package com.tp.auth;

import java.util.List;

import com.tp.auth.dao.UserDao;
import com.tp.auth.model.Contact;
import com.tp.auth.service.ContactService;

public class TestContact {

    public static void main(String[] args) throws Exception {
        ContactService service = new ContactService();
        int aliceId = new UserDao().findByUsername("alice").getId();

        System.out.println("--- liste de départ");
        afficher(service.list(aliceId));

        System.out.println("--- validation (3 erreurs attendues)");
        System.out.println(service.save(new Contact(0, aliceId, "", "Paul", null, null)));
        System.out.println(service.save(new Contact(0, aliceId, "Petit", "Paul", "paul.mail", null)));
        System.out.println(service.save(new Contact(0, aliceId, "Petit", "Paul", null, "06-12")));

        System.out.println("--- ajout");
        System.out.println(service.save(new Contact(0, aliceId, "Petit", "Paul", "paul@mail.fr", "06 11 22 33 44")));
        afficher(service.list(aliceId));

        System.out.println("--- recherche 'pau'");
        afficher(service.search(aliceId, "pau"));

        System.out.println("--- modif du tel");
        Contact paul = service.search(aliceId, "Petit").get(0);
        paul.setTelephone("+33 7 00 00 00 00");
        System.out.println(service.save(paul));
        afficher(service.search(aliceId, "Petit"));

        System.out.println("--- suppr avec un autre userId (ne doit rien supprimer)");
        service.delete(paul.getId(), 9999);
        afficher(service.search(aliceId, "Petit"));

        System.out.println("--- suppr normale");
        service.delete(paul.getId(), aliceId);
        afficher(service.search(aliceId, "Petit"));
    }

    private static void afficher(List<Contact> list) {
        for (Contact c : list) {
            System.out.println(c.getId() + " | " + c.getNom() + " " + c.getPrenom()
                    + " | " + c.getEmail() + " | " + c.getTelephone());
        }
        System.out.println("(" + list.size() + " contact(s))");
    }
}