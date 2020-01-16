DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
`first` VARCHAR(100),
`last` VARCHAR(100),
`age` UNSIGNED TINYINT NOT NULL DEFAULT 0,
`color` VARCHAR(10),
`hobby` TEXT);
CREATE INDEX byfirst ON person(`first`);
CREATE INDEX bylast ON person(`last`);

INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("John", "Grove", 35, "blue", "fishing,jogging,movies");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Sarah", "Harrison", 28, "red", "reading,computer");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Retta", "Ausherman", 45, "orange", "gardening,walking");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Mina", "Churchwell", 22, "none", "listening to music,exercise");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Kala", "Gilliland", 29, "white", "team sports,reading");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Tamie", "Grace", 24, "yellow", "shopping");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Selma", "Pedersen", 20, "green", "traveling,sleeping");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Tom", "Hiller", 31, "blue", "socializing,sewing");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Bridgette", "Korus", 42, "green", "golf,relaxing");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Dorian", "Cieslak", 55, "red", "play music,crafts,reading,movies");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Ehtel", "Mena", 34, "pink", "bicycling,playing cards");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Deena", "Beddingfield", 23, "orange", "hiking,camping");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Nanci", "Tempah", 22, "brown", "cooking,swimming");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Maranda", "Snow", 45, "blue", "skiing,writing,reading");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Colin", "Lymon", 28, "green", "animal care,bowling,dancing");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Leonila", "Wein", 29, "red", "tennis,theater,movies");
INSERT INTO `person` (`first`,`last`,`age`,`color`,`hobby`) VALUES("Lien", "Dileo", 27, "black", "volunteer work,animal care");
