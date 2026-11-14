package com.ecommerce.io;

import com.ecommerce.model.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * SERIALISATION : transforme des objets Java en octets dans un fichier, et inversement.
 * Les champs "transient" (motDePasse, connecte, panier, commandes) ne sont pas ecrits.
 */
public class ClientSerializer {

    public void sauvegarder(List<Client> clients, Path fichier) throws IOException {
        if (fichier.getParent() != null) {
            Files.createDirectories(fichier.getParent());
        }
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(fichier))) {
            out.writeObject(new ArrayList<>(clients));   // ArrayList est Serializable
        }
    }

    public List<Client> charger(Path fichier) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(fichier))) {
            Object lu = in.readObject();
            // Pattern matching : on verifie le type au lieu d'un cast "aveugle"
            if (!(lu instanceof List<?> liste)) {
                throw new IOException("Le fichier ne contient pas une liste");
            }
            List<Client> clients = new ArrayList<>();
            for (Object o : liste) {
                if (o instanceof Client c) {
                    clients.add(c);
                }
            }
            return clients;
        }
    }
}
