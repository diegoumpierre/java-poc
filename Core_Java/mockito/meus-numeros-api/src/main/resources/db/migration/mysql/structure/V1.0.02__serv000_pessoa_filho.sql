CREATE TABLE `000_pessoa_filho` (
  `oid_000_pessoa_filho` int(11) NOT NULL AUTO_INCREMENT,
  `dta_nascimento` datetime(6) DEFAULT NULL,
  `nom_nome` varchar(255) DEFAULT NULL,
  `oid_000_pessoa` int(11) DEFAULT NULL,
  PRIMARY KEY (`oid_000_pessoa_filho`),
  KEY `FKq2m1t9swyfnauj2es6sd4864h` (`oid_000_pessoa`),
  CONSTRAINT `FKq2m1t9swyfnauj2es6sd4864h` FOREIGN KEY (`oid_000_pessoa`) REFERENCES `000_pessoa` (`oid_000_pessoa`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;