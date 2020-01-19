var apiUrl = "/api/v1";
//var apiUrl = "http://rest-demo.alepe.com/api/v1";

/**
 * Return the path to the profile photo
 */
function getProfilePhoto(id) {
    return "/img/profiles/" + id + ".jpg";
}

document.addEventListener("DOMContentLoaded", function() {
    var table = m2d2("#table", function(callback) {
        $get(apiUrl + "/people/0/100", function(row) {
            var people = [];
            for(var p in row) {
                var person = row[p];
                people.push({
                    dataset : { id : person.id },
                    onclick : function(e) {
                        var row = e.target;
                        var id = this.dataset.id;
                        $get(apiUrl + "/person/" + id, function(person) {
                            photo.img.src = getProfilePhoto(id);
                            profile.name = person.first_name + " " + person.last_name;
                            profile.age = person.age;
                            profile.colour = person.favourite_colour;
                            profile.hobbies.items.length = 0;
                            for(var h in person.hobby) {
                                profile.hobbies.items.push(person.hobby[h]);
                            }
                        });
                    },
                    photo : {
                        img : {
                            src : getProfilePhoto(person.id),
                            alt : person.first_name + " " + person.last_name
                        }
                    },
                    name  : person.first_name + " " + person.last_name,
                    age   : person.age,
                    colour : {
                        div : {
                            bgcolor : person.favourite_colour
                        }
                    }
                });
            }
            callback(people);
        }, function() {
            //TODO
            alert("There was a problem while connecting to service.")
        });
    });

    var photo = m2d2("#photo", {
        img : {
            src : getProfilePhoto(0)
        }
    });

    var profile = m2d2("#profile", {
        name : "",
        age  : "",
        colour : "",
        hobbies : {
            template: "<li>",
            items : []
        }
    });

});