CREATE TABLE `person` (
`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
`first_name` VARCHAR(100),
`last_name` VARCHAR(100),
`age` UNSIGNED TINYINT NOT NULL DEFAULT 0,
`favourite_colour` VARCHAR(10),
`hobby` TEXT);
CREATE INDEX byfirst ON person(`first_name`);
CREATE INDEX bylast ON person(`last_name`);

INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Sarah", "Harrison", 28, "red", "Reading,Computer");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("John", "Grove", 35, "blue", "Fishing,Jogging,Movies");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Retta", "Ausherman", 45, "orange", "Gardening,Walking");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Mina", "Churchwell", 22, "purple", "Listening to music,Exercise");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Kala", "Gilliland", 29, "white", "Team sports,Reading");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Tamie", "Grace", 24, "yellow", "Shopping");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Tom", "Hiller", 31, "blue", "Socializing,Sewing");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Mike", "Pedersen", 20, "green", "Traveling,Sleeping");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Bridgette", "Korus", 42, "green", "Golf,Relaxing");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Dorian", "Cieslak", 55, "red", "Play music,Crafts,Reading,Movies");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Ehtel", "Mena", 34, "pink", "Bicycling,Playing cards");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Deena", "Beddingfield", 23, "orange", "Hiking,Camping");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Nanci", "Tempah", 22, "brown", "Cooking,Swimming");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Maranda", "Snow", 45, "blue", "Skiing,Writing,Reading");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Colin", "Lymon", 28, "green", "Animal care,Bowling,Dancing");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Leonila", "Wein", 29, "red", "Tennis,Theater,Movies");
INSERT INTO `person` (`first_name`,`last_name`,`age`,`favourite_colour`,`hobby`) VALUES("Lien", "Dileo", 27, "black", "Volunteer work,Animal care");

CREATE TABLE `auth` (
`user` VARCHAR(20) PRIMARY KEY NOT NULL,
`pass` VARCHAR(60)
);
INSERT INTO `auth` (`user`,`pass`) VALUES ("admin", "UCDMOzHjLRTMaCbBSkvfLOzF/5CWhDivPGUWYGucl57tXlgCH/atu");