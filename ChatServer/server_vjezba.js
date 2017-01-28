var express = require('express')
var app = express()
var bodyParser = require("body-parser");
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

var messages = "";
var groupAndMessagesHashMap = {};
var allGroups = "CENTRAL";
var html = "";

// Dagelić server http://dagelic.com/chat-exercise/?action=list&live
// http://dagelic.com/chat-exercise/?action=new&group=CENTRAL&message=poruka&user=petar

app.get('/chat-exercise/', function (req, res) {
    if(req.query.action=="new"){
      if(req.query.group==undefined || req.query.group=="") {req.query.group="CENTRAL";}
      if(req.query.user==undefined || req.query.user=="") {req.query.user="*AnonymousUser*";}
      if(req.query.message==undefined || req.query.message=="") {req.query.message="*EmptyMessage*";}
      
      if(!allGroups.includes(req.query.group)) {allGroups=allGroups+"||"+req.query.group;}
      
      var timeAndDate = new Date();
      var displayTimeAndDate = (timeAndDate.getDate()<10 ? "0"+ timeAndDate.getDate(): timeAndDate.getDate())
                                +"."
                                +(timeAndDate.getMonth()<10 ? "0"+ timeAndDate.getMonth(): timeAndDate.getMonth())
                                +"."
                                +timeAndDate.getFullYear()
                                +". "
                                +(timeAndDate.getHours()<10 ? "0"+ timeAndDate.getHours(): timeAndDate.getHours())
                                +":"
                                +(timeAndDate.getMinutes()<10 ? "0"+ timeAndDate.getMinutes(): timeAndDate.getMinutes())
                                +":"
                                +(timeAndDate.getSeconds()<10 ? "0"+ timeAndDate.getSeconds(): timeAndDate.getSeconds());
      
      if(groupAndMessagesHashMap[req.query.group]==undefined) {groupAndMessagesHashMap[req.query.group] = " " + req.query.user + " - (" +displayTimeAndDate+ "):\n " + req.query.message + "\n\n";}
      else {groupAndMessagesHashMap[req.query.group] = " " + req.query.user + " - (" +displayTimeAndDate+ "):\n " + req.query.message + "\n\n" + groupAndMessagesHashMap[req.query.group];}
      
      html = "<b>" + req.query.user + "</b>" + " in group " + "<b>" + req.query.group + "</b>" + " - (" + displayTimeAndDate + "):<br>" + req.query.message + "<hr>" + html;
      
      res.end();
    }
    else if(req.query.action=="list"&&req.query.live!=undefined){
      if(groupAndMessagesHashMap["CENTRAL"]!=undefined){
        var html2 = groupAndMessagesHashMap["CENTRAL"].replace(/\n/g, "<br>");
      }
      var timeAndDate = new Date();
      var displayTimeAndDate = (timeAndDate.getDate()<10 ? "0"+ timeAndDate.getDate(): timeAndDate.getDate())
                                +"."
                                +(timeAndDate.getMonth()<10 ? "0"+ timeAndDate.getMonth(): timeAndDate.getMonth())
                                +"."
                                +timeAndDate.getFullYear()
                                +". "
                                +(timeAndDate.getHours()<10 ? "0"+ timeAndDate.getHours(): timeAndDate.getHours())
                                +":"
                                +(timeAndDate.getMinutes()<10 ? "0"+ timeAndDate.getMinutes(): timeAndDate.getMinutes())
                                +":"
                                +(timeAndDate.getSeconds()<10 ? "0"+ timeAndDate.getSeconds(): timeAndDate.getSeconds());
      res.send(
        `
          <html>
          <head>
          <title>Petar's Node.js Express chat server</title>
          <meta http-equiv="refresh" content="1" >
          </head>
          <body>
          <br>
          <h1 align="center">Petar's Node.js Express chat server</h1>
          <br>
          <div style="width:600px;margin:auto;">
          <div style="text-align:right;">
        `
          +displayTimeAndDate+
        `
          </div>
          <br>
          <br>
        `
          +html+
        `
          <div>
          </body>
          </html>
        `
        );
    }
    else if(req.query.action=="list") {
      if(req.query.group==undefined || req.query.group=="") {req.query.group="CENTRAL";}
      res.send(groupAndMessagesHashMap[req.query.group]);
    }
    else if(req.query.action=="groups") {
      res.send(allGroups);
    }
    res.end();
});

app.post('/', function (req, res) {
	if(req.body.action=="new"){
		messages = "User: "+req.body.user+"\nMessage: "+req.body.message+"\n\n" + messages;
		res.end();
	}
	else if (req.body.action=="list"){
		res.end(messages);
	}
	else if (req.body.action=="groups"){
		res.end();
	}
	else res.end('Error, "action" not correctly specified!')
});
