CREATE TABLE DOCUMENT (
  id INT PRIMARY KEY,
  nom VARCHAR(255),
  type VARCHAR(10),
  adulte BOOLEAN
);

CREATE TABLE ABONNE (
  id INT PRIMARY KEY,
  nom VARCHAR(255),
  dateNaissance DATE,
  dateBannissement DATE
);

INSERT INTO DOCUMENT(id, nom, type, adulte) VALUES
(1, 'The Shawshank Redemption', 'DVD', 0),
(2, 'The Godfather', 'DVD', 0),
(3, 'The Dark Knight', 'DVD', 1),
(4, 'Pulp Fiction', 'DVD', 1),
(5, 'Forrest Gump', 'DVD', 0),
(6, 'The Matrix', 'DVD', 1),
(7, 'Star Wars: Episode IV - A New Hope', 'DVD', 0),
(8, 'The Silence of the Lambs', 'DVD', 1),
(9, 'The Lord of the Rings: The Fellowship of the Ring', 'DVD', 0),
(10, 'Jurassic Park', 'DVD', 0);

INSERT INTO ABONNE(id, nom, dateNaissance, dateBannissement) VALUES
(1, 'Jean Bernard', '1985-05-23', NULL),
(2, 'Marie Martin', '1972-02-12', NULL),
(3, 'Lucas Dubois', '1999-07-06', NULL),
(4, 'Sophie Lecomte', '1988-09-30', NULL),
(5, 'David Dupont', '1982-12-14', NULL),
(6, 'Céline Moreau', '1996-11-07', NULL),
(7, 'Nicolas Rousseau', '1977-03-21', NULL),
(8, 'Caroline Leclerc', '2001-06-18', NULL),
(9, 'Titouan Bernard', '2010-01-02', NULL),
(10, 'John Cena', '1983-04-17', NULL);