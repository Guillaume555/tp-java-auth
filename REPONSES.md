# Réponses TP Carnet de contacts

## Q1. Pourquoi la colonne user_id est indispensable ?

Sans user_id, on ne sait pas à qui appartient un contact. C'est cette colonne
qui permet de filtrer avec `WHERE user_id = ?` pour que chaque utilisateur
ne récupère que ses propres contacts.

## Q2. Que se passe-t-il sans ON DELETE CASCADE ?

Avec la clé étrangère mais sans CASCADE, MySQL refuse de supprimer un
utilisateur qui a encore des contacts (erreur 1451) : il faudrait d'abord
supprimer ses contacts à la main.
Sans clé étrangère du tout, l'utilisateur serait supprimé mais ses contacts
resteraient en base avec un user_id qui ne correspond plus à personne
(contacts "orphelins").

