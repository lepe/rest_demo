var apiUrl = "/api/v1";
//var apiUrl = "http://rest-demo.alepe.com/api/v1";

document.addEventListener("DOMContentLoaded", function() {
    var editId = 0;
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
        if(id) {
            $get(apiUrl + "/person/" + id, function(person) {
                photo.show = true;
                photo.img.src = getProfilePhoto(id);
                profile.name.value = person.first_name + " " + person.last_name;
                profile.age.value = person.age;
                profile.colour.div.css = person.favourite_colour;
                profile.colour.i = person.favourite_colour;
                profile.hobbies.items.length = 0;
                for(var h in person.hobby) {
                    profile.hobbies.items.push(person.hobby[h]);
                }
                editId = person.id;
            });
        } else {
            photo.img.src = getProfilePhoto(id);
            profile.name.value = "";
            profile.age.value = "";
            profile.colour.div.css = "white";
            profile.colour.i = "";
            profile.hobbies.items.length = 0;
            editId = 0;
        }
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
    /*
     * Wrapper for confirmations
     */
    function yn(msg, yes, no) {
        notie.confirm({
          text: "Are you sure you want to " + msg + "?",
          cancelText: 'No',
          position: "top",
        }, yes, no || function() {});
    }

    // Main table list. We are showing only the first 100
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
                        return false;
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

    // Photo in profile
    var photo = m2d2("#photo", {
        show : false,
        img : {
            src : getProfilePhoto(0)
        }
    });

    // Profile settings
    var profile = m2d2("#profile", {
        form : {
            css : "",
            onsubmit : function(e) {
                var elems = this.elements;
                var data = {
                    first_name : elems.name.value.split(" ")[0],
                    last_name : elems.name.value.split(" ")[1],
                    age : elems.age.value
                }
                if(editId) {
                    $put(apiUrl + "/person/"+editId, data, function(res) {
                        ok("Saved successfully!");
                        table.m2d2.update();
                    }, function() {
                        ng("Error while saving data.");
                    });
                }
                return false;
            }
        },
        name : {
            value : "",
            required : true,
            readOnly : true,
            placeholder: "First Last"
        },
        age  : {
            value : "",
            min : 1,
            max : 110,
            required : true,
            readOnly : true
        },
        colour : {
            div : {
                css : ""
            },
            i : ""
        },
        hobby : {
            value : "",
            readOnly : true
        },
        hobbies : {
            template: "li",
            items : []
        }
    });

    var actions = m2d2("#actions", {
        rm : {
            onclick : function() {
                yn("delete", function() {
                    if(editId) {
                        $delete(apiUrl + "/person/"+editId, function(res) {
                            updateProfile(0);
                            table.m2d2.update();
                        });
                    }
                });
                return false;
            }
        }
    });

    // Login button
    var login = m2d2("#login", {
        show: true,
        onclick: function() {
            var value = "admin";
            /*notie.input({
              text: "Please input password",
              submitText: 'Login',
              position: "top",
              type: 'password'
            }, function(value) {*/
                $post(apiUrl + "/auth/login", {
                    user: "admin",
                    pass: value
                }, function(){
                    login.show = false;
                    logout.show = true;
                    profile.form.css = "edit";
                    // Set editable fields:
                    profile.name.readOnly = false;
                    profile.age.readOnly = false;
                    profile.hobby.readOnly = false;
                    ok("Login succeed!");
                }, function() {
                    ng("Login failed!");
                });
           // }, function (value) {});
            return false;
        }
    });

    // Logout button
    var logout = m2d2("#logout", {
        show: false,
        onclick: function() {
            $get(apiUrl + "/auth/logout", function() {
                login.show = true;
                logout.show = false;
                profile.form.css = "";
                // Readonly fields:
                profile.name.readOnly = true;
                profile.age.readOnly = true;
                profile.hobby.readOnly = true;
                ok("Logged out");
            });
            return false;
        }
    });

    // Hobby list (all options)
    var hobby_list = m2d2("#hobby_list", {
        template : "option",
        items : hobbiesFullList
    });

});