package com.ecommerce.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utilitaires fichiers :
 *  - FLUX D'OCTETS (InputStream / OutputStream) pour copier un fichier binaire (image produit) ;
 *  - API NIO.2 (Path, Files) pour les dossiers et les informations sur les fichiers.
 */
public final class FichierUtils {

    private FichierUtils() { }   // classe utilitaire : pas d'instance

    /** Copie octet par octet avec un buffer. Renvoie le nombre d'octets copies. */
    public static long copierOctets(Path source, Path destination) throws IOException {
        if (destination.getParent() != null) {
            Files.createDirectories(destination.getParent());
        }
        long total = 0;
        try (InputStream in = new BufferedInputStream(Files.newInputStream(source));
             OutputStream out = new BufferedOutputStream(Files.newOutputStream(destination))) {
            byte[] buffer = new byte[4096];
            int lus;
            while ((lus = in.read(buffer)) != -1) {
                out.write(buffer, 0, lus);
                total += lus;
            }
        }
        return total;
    }

    /** Cree le dossier (et ses parents) s'il n'existe pas. */
    public static Path preparerDossier(String nom) throws IOException {
        Path dossier = Path.of(nom);
        if (!Files.exists(dossier)) {
            Files.createDirectories(dossier);
        }
        return dossier;
    }

    /** Liste les fichiers d'un dossier ayant une extension donnee (Files.list + stream). */
    public static List<Path> listerFichiers(Path dossier, String extension) throws IOException {
        try (Stream<Path> fichiers = Files.list(dossier)) {
            return fichiers
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(extension))
                    .sorted()
                    .toList();
        }
    }

    public static String description(Path fichier) throws IOException {
        return fichier.getFileName() + " (" + Files.size(fichier) + " octets)";
    }
}
