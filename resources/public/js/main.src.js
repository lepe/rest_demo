var apiUrl = "/api/v1";
//var apiUrl = "http://rest-demo.alepe.com/api/v1";

document.addEventListener("DOMContentLoaded", function() {

    /**
     * Return the path to the profile photo
     */
    function getProfilePhoto(id) {
        return "/img/profiles/" + id + ".jpg";
    }
    /**
     * Update the profile view
     */
    function updateProfile(id) {
        $get(apiUrl + "/person/" + id, function(person) {
            photo.show = true;
            photo.img.src = getProfilePhoto(id);
            profile.name = person.first_name + " " + person.last_name;
            profile.age = person.age;
            profile.colour.div.css = person.favourite_colour;
            profile.colour.i = person.favourite_colour;
            profile.hobbies.items.length = 0;
            for(var h in person.hobby) {
                profile.hobbies.items.push(person.hobby[h]);
            }
        });
    }
    /**
     * Wrapper for success
     */
    function ok(msg) {
        notie.alert({
          type: "success",
          text: msg || "Success!" ,
          stay: false,
          time: 2,
          position: "bottom"
        });
    }
    /**
     * Wrapper for failure
     */
    function ng(msg) {
        notie.alert({
          type: "error",
          text: msg || "Failed!",
          stay: false,
          time: 2,
          position: "bottom"
        });
    }

    var table = m2d2("#table", function(callback) {
        $get(apiUrl + "/people/0/100", function(row) {
            var people = [];
            var first = 0;
            for(var p in row) {
                var person = row[p];
                if(!first) {
                    first = person.id;
                }
                people.push({
                    dataset : { id : person.id },
                    onclick : function(e) {
                        var row = e.target;
                        var id = this.dataset.id;
                        updateProfile(id);
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
                            css : person.favourite_colour
                        }
                    }
                });
            }
            callback(people);
            updateProfile(first);
        }, function() {
            ng("There was a problem while connecting to service.");
        });
    });

    var photo = m2d2("#photo", {
        show : false,
        img : {
            src : getProfilePhoto(0)
        }
    });

    var profile = m2d2("#profile", {
        css : "",
        name : "",
        age  : "",
        colour : {
            div : {
                css : ""
            },
            i : ""
        },
        hobbies : {
            template: "li",
            items : []
        }
    });

    var login = m2d2("#login", {
        show: true,
        onclick: function() {
            notie.input({
              text: "Please input password",
              submitText: 'Login',
              position: "top",
              type: 'password'
            }, function(value) {
                $post(apiUrl + "/auth/login", {
                    user: "admin",
                    pass: value
                }, function(){
                    login.show = false;
                    logout.show = true;
                    profile.css = "edit";
                    ok("Login succeed!");
                }, function() {
                    ng("Login failed!");
                });
            }, function (value) {});
        }
    });

    var logout = m2d2("#logout", {
        show: false,
        onclick: function() {
            $get(apiUrl + "/auth/logout", function() {
                login.show = true;
                logout.show = false;
                profile.css = "";
            });
        }
    });

    var hobby_list = m2d2("#hobby_list", {
        template : "option",
        items : hobbiesFullList
    });

});