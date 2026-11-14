# E-commerce Java — périmètre Dev 2

## Contrat Repository

`Repository<T, ID>` est le contrat partagé avec les implémentations JDBC et JPA.
Il expose les opérations CRUD, `saveAll(Collection<? extends T>)` pour lire des
entités productrices, `copierVers(Collection<? super T>)` pour écrire vers une
destination consommatrice et `findBy(Predicate<? super T>)`. Le wildcard `super`
permet notamment de fournir un `Predicate<Object>` pour filtrer des `Produit`.
`findPage(numero, taille)` utilise une pagination indexée à partir de zéro et
rejette un numéro négatif ou une taille non positive.

`InMemoryRepository` fournit l'implémentation de référence. `Page<T>` copie sa
liste d'éléments pour empêcher les modifications externes. `CollectionUtils`
montre les bornes PECS : `max` accepte une collection de sous-types comparables,
et `copier` lit depuis `? extends T` puis écrit dans `? super T`.

## Builder de Commande

`Commande.Builder` est une classe imbriquée statique. Le client est obligatoire
dès la création du builder et `build()` exige au moins une ligne. Exemple :

```java
Commande commande = new Commande.Builder(client)
        .ajouterLigne(produit, 2)
        .build();
```

## Classes imbriquées et streams

`Panier` est itérable grâce à sa classe interne `PanierIterator`; l'ajout d'un
produit présent fusionne les lignes. `StatistiqueService.rapportVentes()` déclare
une classe locale `LigneRapport`. `DemoDev2` compare un comparateur anonyme
(montant puis date) à son équivalent lambda. Les services illustrent notamment
`filter`, `map`, `flatMap`, `sorted`, `distinct`, `skip`, `limit`, `peek`, les
collecteurs de groupement et partitionnement, `reduce`, les prédicats de
correspondance et les recherches `Optional` terminées avec `orElseThrow()`.

## Vérification

Depuis ce dossier : `mvn test`. La démonstration se lance avec
`mvn -q exec:java -Dexec.mainClass=com.ecommerce.demo.DemoDev2` si le plugin
Exec Maven est disponible, ou directement depuis l'IDE.
