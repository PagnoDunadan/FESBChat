var express = require('express');
var app = express();
var bodyParser = require("body-parser");
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

var fs = require("fs")
var path = require('path');
app.use(express.static(path.join(__dirname, 'public')));

var API_URL = "http://192.168.1.221:3000/";

var users = {};
users['admin'] = {password: 'password', thumbnail: 'https://www.heroesofnewerth.com/images/heroes/120/icon_128.jpg', friends: '||admina||', groups: '||', lastOnline: 0};
users['admina'] = {password: 'password', thumbnail: 'http://www.heroesofnewerth.com/images/heroes/120/icon_128_alt3.jpg', friends: '||admin||', groups: '||', lastOnline: 0};
var friendMessages = {};


// Login
app.post('/login', function (req, res) {
  
    if(req.body.action == "ExistingUser") {
        
        if(req.body.user in users && req.body.password == users[req.body.user].password) {
            
            res.send('LoginSuccessful');
            console.log(req.body.user + " logged in");
            var date = new Date();
            users[req.body.user].lastOnline = date.getTime();
        }
        else {
            
            res.send('UserNotFound');
            console.log(req.body.user + " not found and can't log in");
        }
    }
    else if(req.body.action == "NewUser") {
        
        if( !(req.body.user in users) ) {
            
            users[req.body.user] = { password: req.body.password, thumbnail: '', friends: '||', groups: '||', lastOnline: 0 };
            res.send('SignUpSuccessful');
            console.log(req.body.user + " signed up");
            var date = new Date();
            users[req.body.user].lastOnline = date.getTime();
        }
        else {
            
            res.send('UserAlreadyExists');
            console.log(req.body.user + " already exists and can't sign up");
        }
    }
    else {
        
        res.end();
    }
});


// Profile picture default
app.get('/profile', function (req, res) {
    
    res.sendFile(__dirname + '/users/profile.png');
    console.log('Default profile picture sent to Android app');
});


// Profile picture url
app.get('/getProfilePictureUrl', function (req, res) {
    
    
    if(req.query.user in users) {
        
        res.end(users[req.query.user].thumbnail);
        console.log(req.query.user + "'s profile picture url sent to Android app");
    }
    else {
        
        res.end('UserNotFound');
        console.log(req.query.user + " not found and can't send profile picture url");
    }
});

// Change profile picture url
app.post('/changeProfilePictureUrl', function (req, res) {
    
    
    if(req.body.user in users) {
        
        users[req.body.user].thumbnail = req.body.pictureUrl;
        console.log(users[req.body.user].thumbnail);
        res.end('PictureUpdated');
        console.log(req.body.user + "'s profile picture url updated");
    }
    else {
        
        res.end('UserNotFound');
        console.log(req.body.user + " not found and can't update profile picture url");
    }
});

// Upload profile picture
app.post('/uploadProfilePicture', function (req, res) {
    
    
    if(req.body.user in users) {
        
        fs.writeFile(__dirname + '/public/' + req.body.user+'.png', req.body.image, {encoding: 'base64'}, function(err){
            //Finished
        });
        
        users[req.body.user].thumbnail = API_URL+req.body.user+".png";
        res.end('PictureUpdated');
        console.log(req.body.user + "'s profile picture: " + req.body.image);
        console.log(req.body.user + "'s profile picture uploaded");
    }
    else {
        
        res.end('UserNotFound');
        console.log(req.body.user + " not found and can't update profile picture url");
    }
});


// Add a friend
app.post('/addFriend', function (req, res) {
    
    if(req.body.user in users) {
        
        if(req.body.friendName in users) {
            
            if((users[req.body.user].friends).indexOf("||" + req.body.friendName + "||") == -1 && (users[req.body.friendName].friends).indexOf("||" + req.body.user + "||") == -1) {
                
                if(req.body.user == req.body.friendName) {
                    
                    users[req.body.user].friends = "||" + req.body.friendName + users[req.body.user].friends;
                }
                else {
                    
                    users[req.body.user].friends = "||" + req.body.friendName + users[req.body.user].friends;
                    users[req.body.friendName].friends = "||" + req.body.user + users[req.body.friendName].friends;
                }
                res.send('FriendAdded');
                console.log(req.body.user + " added " + req.body.friendName + " as a friend");
                console.log(req.body.user + "'s friends: " + users[req.body.user].friends);
                console.log(req.body.friendName + "'s friends: " + users[req.body.friendName].friends);
            }
            else {
                
                res.send('AlreadyFriends');
                console.log(req.body.user + " and " + req.body.friendName + " are already friends");
            }
        }
        else {
            
            res.send('FriendNotFound');
            console.log(req.body.friendName + " not found and can't be added to " + req.body.user + "'s friends");
        }
        var date = new Date();
        users[req.body.user].lastOnline = date.getTime();
    }
    else {
        
        res.send('UserNotFound');
        console.log(req.body.user + " not found and can't add friends");
    }
});


// Add a friend preview
app.post('/addFriendPreview', function (req, res) {
    
    if(req.body.user in users) {
        
        var preview = "";
        for(var key in users) {
            
            if (key.startsWith(req.body.friendName)) {
               preview = preview + key + ", ";
            }
        }
        preview = preview.slice(0, -2);
        res.end(preview);
        var date = new Date();
        users[req.body.user].lastOnline = date.getTime();
    }
    else {
        
        res.send('UserNotFound');
        console.log(req.body.user + " not found and can't add friends");
    }
});


// Add friend preview list
app.post('/addFriendList', function (req, res) {
    
    if(req.body.user in users) {
        
        var preview = "[";
        var friendsStatus = false;
        for(var key in users) {
            
            if (key.indexOf(req.body.friendName) != -1 && key!=req.body.user) {
                
                if( (users[key].friends).indexOf("||"+req.body.user+"||") != -1 ) {
                    
                    friendsStatus = true;
                }
                else {
                    friendsStatus = false;
                }
                
                preview = preview + '{' 
                                        + '"name":"' + key + '",'
                                        + '"thumbnail":"' + users[key].thumbnail + '",'
                                        + '"friendsStatus":"' + friendsStatus
                                  + '"},';
            }
        }
        preview = preview.slice(0, -1);
        preview = preview + "]";
        
        if(preview == "]") {
            
            res.end('None');
        }
        else {
            
            res.end(preview);
        }
        
        var date = new Date();
        users[req.body.user].lastOnline = date.getTime();
        console.log(preview);
    }
    else {
        
        res.send('UserNotFound');
        console.log(req.body.user + " not found and can't add friends");
    }
});


// Remove a friend
app.post('/removeFriend', function (req, res) {
    
    if(req.body.user in users) {
        
        if(req.body.friendName in users) {
            
            if((users[req.body.user].friends).indexOf("||" + req.body.friendName + "||") != -1 && (users[req.body.friendName].friends).indexOf("||" + req.body.user + "||") != -1) {
                
                users[req.body.user].friends = users[req.body.user].friends.replace("||"+req.body.friendName+"||", "||");
                users[req.body.friendName].friends = users[req.body.friendName].friends.replace("||"+req.body.user+"||", "||");
                
                res.send('FriendRemoved');
                console.log(req.body.user + " removed " + req.body.friendName + " from friends list");
                console.log(req.body.user + "'s friends: " + users[req.body.user].friends);
                console.log(req.body.friendName + "'s friends: " + users[req.body.friendName].friends);
            }
            else {
                
                res.send('NotFriends');
                console.log(req.body.user + " is not friend with " + req.body.friendName);
            }
        }
        else {
            
            res.send('FriendNotFound');
            console.log(req.body.friendName + " not found and can't be removed from " + req.body.user + "'s friends list");
        }
        var date = new Date();
        users[req.body.user].lastOnline = date.getTime();
    }
    else {
        
        res.send('UserNotFound');
        console.log(req.body.user + " not found and can't remove friends");
    }
});

// Remove a friend preview
app.post('/removeFriendPreview', function (req, res) {
    
    if(req.body.user in users) {
        
        var friends = [];
        friends = users[req.body.user].friends.slice(2, -2).split("||");
        var preview = "";
        
        for(var i=0; i<friends.length; i++) {
            
            if( friends[i].indexOf(req.body.friendName) != -1 ) {
                
                preview += friends[i] + ", ";
            }
        }
        
        preview = preview.slice(0, -2);
        res.end(preview);

        var date = new Date();
        users[req.body.user].lastOnline = date.getTime();
    }
    else {
        
        res.send('UserNotFound');
        console.log(req.body.user + " not found and can't remove friends");
    }
});


// Get friends list
app.get('/getFriendsList', function (req, res) {
    
    if(req.query.user in users) {
        
        if(users[req.query.user].friends == "||") {
            
            res.send("FriendsListEmpty");
            console.log(req.query.user + "'s friends list is empty");
        }
        else {
            
            var friends = users[req.query.user].friends;
            friends = friends.slice(2, -2);
            friends = friends.split("||");
            
            var friendsJSON = "[";
            for(i=0; i<friends.length; i++) {
                
                friendsJSON = friendsJSON + '{"ID":' + i + ', "thumbnail":"' + users[friends[i]].thumbnail + '", "name":"' + friends[i] + '", "lastOnline":' +  users[friends[i]].lastOnline + '},';
            }
            friendsJSON = friendsJSON.slice(0, -1);
            friendsJSON = friendsJSON+"]";
            console.log(req.query.user + "'s friends list: " + friendsJSON);
            
            res.send(friendsJSON);
            
            console.log(req.query.user + "'s friends list sent to Android app");
        }
        var date = new Date();
        users[req.query.user].lastOnline = date.getTime();
    }
    else {
        
        res.send("UserNotFound");
        console.log(req.query.user + " not found");
    }
});


// 1 on 1 messages
app.post('/sendFriendMessage', function (req, res) {
    
    if(req.body.user in users) {
        
        if(req.body.friend in users) {
            
            if(friendMessages[req.body.user+req.body.friend] != undefined || friendMessages[req.body.friend+req.body.user] != undefined) {
                
                if(friendMessages[req.body.user+req.body.friend] != undefined) {
                    
                    // Testing
                    var date = new Date();
                    friendMessages[req.body.user+req.body.friend][Object.keys(friendMessages[req.body.user+req.body.friend]).length] = {sender: req.body.user, profileImage: users[req.body.user].thumbnail, time: date.getTime(), message: req.body.message, seen: "||"};
                    users[req.body.user].lastOnline = date.getTime();
                }
                else if(friendMessages[req.body.friend+req.body.user] != undefined) {
                    
                    // Testing phase
                    var date = new Date();
                    friendMessages[req.body.friend+req.body.user][Object.keys(friendMessages[req.body.friend+req.body.user]).length] = {sender: req.body.user, profileImage: users[req.body.user].thumbnail, time: date.getTime(), message: req.body.message, seen: "||"};
                    users[req.body.user].lastOnline = date.getTime();
                }
            }
            else {
                
                friendMessages[req.body.user+req.body.friend] = {};
                var date = new Date();
                friendMessages[req.body.user+req.body.friend][0] = {sender: req.body.user, profileImage: users[req.body.user].thumbnail, time: date.getTime(), message: req.body.message, seen: "||"};
                users[req.body.user].lastOnline = date.getTime();
            }
            console.log(JSON.stringify(friendMessages, null, 4));
            res.end('MessageSaved');
        }
        else {
            
            res.end('FriendNotFound');
            console.log(req.body.user + " can't send messages to " + req.body.friend + ", " + req.body.friend + " not found");
        }
    }
    else {
        
        res.end('UserNotFound');
        console.log(req.body.user + " not found and can't send messages");
    }
});

app.post('/getFriendMessages', function (req, res) {
    
    if(req.body.user in users) {
        
        if(req.body.friend in users) {
            
            if(friendMessages[req.body.user+req.body.friend] != undefined || friendMessages[req.body.friend+req.body.user] != undefined) {
                
                if(friendMessages[req.body.user+req.body.friend] != undefined) {
                    
                    var friendMessagesJSON = "[";
                    
                    for(var i=0; i<Object.keys(friendMessages[req.body.user+req.body.friend]).length; i++){
                        
                        if((friendMessages[req.body.user+req.body.friend][i].seen).indexOf("||" + req.body.user + "||") == -1) {
                            friendMessages[req.body.user+req.body.friend][i].seen += req.body.user + "||";
                        }
                        friendMessagesJSON += JSON.stringify(friendMessages[req.body.user+req.body.friend][i], null, 4) + ",";
                    }
                    friendMessagesJSON = friendMessagesJSON.slice(0, -1);
                    friendMessagesJSON = friendMessagesJSON+"]";
                    console.log(friendMessagesJSON);
                    res.end(friendMessagesJSON);
                    
                }
                else if(friendMessages[req.body.friend+req.body.user] != undefined) {
                    
                    var friendMessagesJSON = "[";
                    
                    for(var i=0; i<Object.keys(friendMessages[req.body.friend+req.body.user]).length; i++){
                        
                        if((friendMessages[req.body.friend+req.body.user][i].seen).indexOf("||" + req.body.user + "||") == -1) {
                            friendMessages[req.body.friend+req.body.user][i].seen += req.body.user + "||";
                        }
                        friendMessagesJSON += JSON.stringify(friendMessages[req.body.friend+req.body.user][i], null, 4) + ",";
                    }
                    friendMessagesJSON = friendMessagesJSON.slice(0, -1);
                    friendMessagesJSON = friendMessagesJSON+"]";
                    console.log(friendMessagesJSON);
                    res.end(friendMessagesJSON);
                }
            }
            res.end();
            var date = new Date();
            users[req.body.user].lastOnline = date.getTime();
        }
        else {
            
            res.end('FriendNotFound');
            console.log(req.body.user + " can't receive messages, " + req.body.friend + " not found");
        }
    }
    else {
        
        res.end('UserNotFound');
        console.log(req.body.user + " not found and can't receive messages");
    }
});


// Get online status
app.get('/getOnlineStatus', function (req, res) {
    if(req.query.user in users) {

        res.end(users[req.query.user].lastOnline.toString());
        console.log(req.query.user + "'s online status sent");
    }
    else {
        
        res.end('UserNotFound');
        console.log(req.query.user + "not found and can't get online status");
    }
});


// Get groups list
app.get('/groups', function (req, res) {
  res.end(`
  [{      "ID" : 1,
        "thumbnail" : "http://dagelic.com/vjezbe/android/carssmall/porsche997.jpg",
        "name" : "Grupa1"
    },{ 
        "ID" : 2,
        "thumbnail" : "http://dagelic.com/vjezbe/android/carssmall/gtr.jpg",
        "name" : "Grupa2"
    },{ 
        "ID" : 3,
        "thumbnail" : "http://dagelic.com/vjezbe/android/carssmall/gtr.jpg",
        "name" : "Grupa3"
    }
]
  `)
  console.log('Grupe poslane Android aplikaciji');
});


// Start server
app.listen(3000, function () {
  console.log("FESB Chat server listening on port 3000!")
});