var apiUrl = "/api/v1";
//var apiUrl = "https://rest-demo.alepe.com/api/v1";

document.addEventListener("DOMContentLoaded", function() {
    var editId = 0;

    ///////////////////// Private functions ////////////////////////////

    /**
     * Return the path to the profile photo
     */
    function getProfilePhoto(id) {
        if(id > 100) {
            var mult = Math.round(id / 100);
            id = id - (mult * 100);
        }
        return "/img/profiles/" + id + ".jpg";
    }
    /**
     * It will create a Hobby element
     */
    function addHobby(text) {
        var exists = false;
        for(var h in profile.hobbies.items) {
            var hobby = profile.hobbies.items[h].text;
            if(text == hobby) {
                return; //Already exists.
            }
        }
        text = text.replace(/[^a-zA-Z ,\'/&-]/gi, '');
        profile.hobbies.items.push({
           text : text,
           onclick : function(e) {
               var index = Array.prototype.indexOf.call(this.parentNode.childNodes, this);
               profile.hobbies.items.splice(index - 1, 1);
           }
        });
    }
    /**
     * Update the profile view
     */
    function updateProfile(id) {
        if(id) {
            loading.show = true;
            $get(apiUrl + "/person/" + id, function(person) {
                loading.show = false;
                photo.show = true;
                photo.img.src = getProfilePhoto(id);
                profile.name.value = person.first_name + " " + person.last_name;
                profile.name._node.value = profile.name.value;
                profile.age.value = person.age;
                profile.age._node.value = profile.age.value;
                profile.colour.b.css = person.favourite_colour;
                profile.favourite.value = person.favourite_colour;
                profile.favourite._node.value = profile.favourite.value;
                profile.hobbies.items.length = 0;
                for(var h in person.hobby) {
                    addHobby(person.hobby[h]);
                }
                editId = person.id;
                actions.disabled = false;
            }, function() {
                loading.show = false;
            });
        } else {
            photo.img.src = getProfilePhoto(id);
            profile.name.value = "";
            profile.name._node.value = profile.name.value;
            profile.age.value = "";
            profile.age._node.value = profile.age.value;
            profile.colour.b.css = "none";
            profile.colour.i = "";
            profile.favourite.value = "none";
            profile.favourite._node.value = profile.favourite.value;
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
    /*
     * Actions to perform during login / logout
     * @param isOn: true = login, false = logout
     */
    function authLogin(isIn) {
        if(isIn) {
            loading.show = false;
            login.show = false;
            logout.show = true;
            profile.form.css = "edit";
            // Set editable fields:
            profile.name.readOnly = false;
            profile.age.readOnly = false;
            profile.hobby.readOnly = false;
            profile.name.title = "Names are only accepted in the format: 'first_name<space>last_name'";
            sessionStorage.setItem("logged", true);
        } else {
            loading.show = false;
            login.show = true;
            logout.show = false;
            profile.form.css = "";
            // Readonly fields:
            profile.name.readOnly = true;
            profile.age.readOnly = true;
            profile.hobby.readOnly = true;
            profile.name.title = "";
            sessionStorage.removeItem("logged");
        }
    }

    /////////////////////////// M2D2 Objects ////////////////////////

    //Loading pane
    var loading = m2d2("#loading", {
        show: false
    });

    // Aside show/hide control
    var aside = m2d2("aside", function() {
        show : true
    });
    // This handles the close button on aside
    var close_aside = m2d2("#close", {
        show : false,
        onclick : function() {
            aside.show = false;
        }
    });

    // Main table list. We are showing only the first 100
    var table = m2d2("#table", function(callback) {
        loading.show = true;
        $get(apiUrl + "/people/0/100", function(row) {
            loading.show = false;
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
                        aside.show = true;
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
                        b : {
                            css : person.favourite_colour
                        }
                    }
                });
            }
            callback(people);
            if(! editId) {
                updateProfile(first);
            }
        }, function() {
            loading.show = false;
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
                var hobbyList = [];
                for(var i in profile.hobbies.items) {
                    hobbyList.push(profile.hobbies.items[i].text);
                }
                var data = {
                    first_name  : elems.name.value.split(" ")[0],
                    last_name   : elems.name.value.split(" ")[1],
                    age         : elems.age.value,
                    favourite_colour : elems.favourite.value,
                    hobby       : hobbyList
                }
                if(editId) {
                    loading.show = true;
                    $put(apiUrl + "/person/"+editId, data, function(res) {
                        loading.show = false;
                        ok("Saved successfully!");
                        table.m2d2.update();
                    }, function() {
                        loading.show = false;
                        ng("Error while saving data.");
                    });
                } else {
                    loading.show = true;
                    $post(apiUrl + "/person", data, function(res) {
                        loading.show = false;
                        if(res.ok) {
                            editId = res.id;
                            updateProfile(editId);
                            ok("Saved successfully!");
                            table.m2d2.update();
                        }
                    }, function() {
                        loading.show = false;
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
            placeholder: "First Last",
            pattern  : "^[A-Za-z]+\\s[A-Za-z]+$",
            title    : "",
            onkeydown : function(e) {
                if(e.keyCode === 13){
                    e.preventDefault();
                    profile.age._node.focus();
                    return false;
                }
            }
        },
        age  : {
            value : "",
            min : 1,
            max : 110,
            required : true,
            readOnly : true,
            onkeydown : function(e) {
                if(e.keyCode === 13){
                    e.preventDefault();
                    return false;
                }
            }
        },
        colour : {
            b : {
                css : ""
            }
        },
        favourite : {
            value : "",
            onchange : function() {
                profile.colour.b.css = this.value;
            },
            onmousedown : function() {
                return profile.form.css == "edit";
            },
            onkeydown : function(e) {
                if(e.keyCode === 13){
                    e.preventDefault();
                    return false;
                }
            }
        },
        hobby : {
            value : "",
            readOnly : true,
            onchange : function() {
                addHobby(this.value);
                this.value = "";
            },
            onkeydown : function(e) {
                if(e.keyCode === 13){
                    e.preventDefault();
                    addHobby(this.value);
                    this.value = "";
                    return false;
                }
            }
        },
        hobbies : {
            template: "li",
            items : []
        }
    });

    var actions = m2d2("#actions", {
        rm : {
            disabled : false,
            onclick : function() {
                yn("delete", function() {
                    if(editId) {
                        loading.show = true;
                        $delete(apiUrl + "/person/"+editId, function(res) {
                            loading.show = false;
                            updateProfile(0);
                            aside.show = false;
                            table.m2d2.update();
                        }, function() {
                            loading.show = false;
                        });
                    }
                });
                return false;
            }
        },
        'new' : {
            onclick: function() {
                actions.disabled = true;
                updateProfile(0);
                profile.name._node.focus();
                return false;
            }
        }
    });

    // Login button
    var login = m2d2("#login", {
        show: true,
        onclick: function() {
            notie.input({
              text: "Please input password",
              submitText: 'Login',
              position: "top",
              type: 'password'
            }, function(value) {
                loading.show = true;
                $post(apiUrl + "/auth/login", {
                    user: "admin",
                    pass: value
                }, function() {
                    authLogin(true);
                    ok("Login succeed!");
                }, function() {
                    loading.show = false;
                    ng("Login failed!");
                    authLogin(false);
                });
            }, function (value) {});
            return false;
        }
    });

    // Logout button
    var logout = m2d2("#logout", {
        show: false,
        onclick: function() {
            loading.show = true;
            $get(apiUrl + "/auth/logout", function() {
                authLogin(false);
                ok("Logged out");
            }, function() {
                loading.show = true;
            });
            return false;
        }
    });

    // Hobby list (all options)
    var hobby_list = m2d2("#hobby_list", {
        template : "option",
        items : hobbiesFullList
    });

    //////////////////////////// INIT //////////////////////
    if(sessionStorage.getItem("logged") == "true") {
        authLogin(true);
    }
});